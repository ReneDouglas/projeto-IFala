package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.EmailRequest;
import br.edu.ifpi.ifala.shared.exceptions.EmailServiceException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.internet.MimeMessage;
import java.util.Objects;

/**
 * Serviço para enviar e-mails via SMTP.
 *
 * @author Phaola
 */

@Service
public class SmtpEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username:}")
  private String fromAddress;

  public SmtpEmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @PostConstruct
  void init() {
    if (fromAddress == null || fromAddress.isBlank()) {
      log.warn("spring.mail.username não configurado — e-mails podem falhar ao enviar sem from");
    }
  }

  @Override
  @Async
  public void sendEmail(EmailRequest request) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message,
          MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

      String subject = Objects.requireNonNull(request.subject());
      String body = Objects.requireNonNull(request.body());

      helper.setSubject(subject);
      if (fromAddress != null && !fromAddress.isBlank()) {
        helper.setFrom(fromAddress);
      }

      // To
      if (request.to() != null && !request.to().isEmpty()) {
        helper.setTo(request.to().toArray(new String[0]));
      }

      // CC/BCC
      if (request.cc() != null && !request.cc().isEmpty()) {
        helper.setCc(request.cc().toArray(new String[0]));
      }
      if (request.bcc() != null && !request.bcc().isEmpty()) {
        helper.setBcc(request.bcc().toArray(new String[0]));
      }

      helper.setText(body, request.html());

      mailSender.send(message);
    } catch (Exception ex) {
      log.error("Erro ao enviar e-mail", ex);
      throw new EmailServiceException(ex.getMessage());
    }
  }


}
