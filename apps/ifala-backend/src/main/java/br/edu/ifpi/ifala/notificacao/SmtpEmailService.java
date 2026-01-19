package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.EmailRequest;
import br.edu.ifpi.ifala.shared.exceptions.EmailServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import br.edu.ifpi.ifala.autenticacao.Usuario;
import br.edu.ifpi.ifala.autenticacao.UsuarioRepository;
import java.util.Optional;

/**
 * Serviço para enviar e-mails via SMTP.
 *
 * @author Phaola
 * @author Renê Morais
 */
@Service
public class SmtpEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);
  private final JavaMailSender mailSender;
  private final UsuarioRepository usuarioRepository;

  @Value("${spring.mail.username:}")
  private String fromAddress;

  public SmtpEmailService(JavaMailSender mailSender, UsuarioRepository usuarioRepository) {
    this.mailSender = mailSender;
    this.usuarioRepository = usuarioRepository;
  }

  /*
   * LÓGICA: 1. O Java tenta enviar. Fica "preso" até atingir o timeout configurado no properties
   * (ex: 60s). 2. Se der timeout, lança exceção. 3. O @Retryable captura. 4. Espera o delay do
   * backoff. 5. Tenta de novo.
   */
  @Override
  @Async
  @Retryable(retryFor = {Exception.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 5000, multiplier = 2))
  public void sendEmail(EmailRequest request) {

    // --- NOVO BLOQUEIO: Verifica se o usuário desativou notificações
    // Filtra destinatários que optaram por não receber notificações
    java.util.List<String> toFiltered = filterByNotificationPreference(request.to());
    java.util.List<String> ccFiltered = filterByNotificationPreference(request.cc());
    java.util.List<String> bccFiltered = filterByNotificationPreference(request.bcc());

    // Se não houver nenhum destinatário após filtrar, cancela envio
    if ((toFiltered == null || toFiltered.isEmpty()) 
        && (ccFiltered == null || ccFiltered.isEmpty()) 
        && (bccFiltered == null || bccFiltered.isEmpty())) {
      log.info("Envio CANCELADO: Nenhum destinatário quer receber notificações para '{}'", 
          request.subject());
      return;
    }

    // Cria novo request com destinatários filtrados
    EmailRequest filteredRequest = new EmailRequest(
        toFiltered, 
        ccFiltered, 
        bccFiltered, 
        request.subject(), 
        request.body(), 
        request.html()
    );

    log.info("[ASYNC] Tentando enviar e-mail (Assunto: '{}')", request.subject());
    try {
      executeSend(filteredRequest);
      log.info("E-mail enviado com sucesso: '{}'", request.subject());
    } catch (Exception e) {
      log.warn(
          "Erro no envio (Assunto: '{}'). O sistema tentará novamente se houver tentativas restantes. Erro: {}",
          request.subject(), e.getMessage());
      // Re-lança para ativar o Retry
      throw new EmailServiceException("Erro de envio SMTP: " + e.getMessage());
    }
  }

  /**
   * Filtra lista de emails removendo usuários que desativaram notificações.
   */
  private java.util.List<String> filterByNotificationPreference(java.util.List<String> emails) {
    if (emails == null || emails.isEmpty()) {
      return emails;
    }

    java.util.List<String> filtered = new java.util.ArrayList<>();
    for (String email : emails) {
      Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
      
      if (usuarioOpt.isEmpty()) {
        // Se não encontrou usuário no banco, permite envio (pode ser email externo)
        filtered.add(email);
      } else if (usuarioOpt.get().isReceberNotificacoes()) {
        // Usuário existe e quer receber notificações
        filtered.add(email);
      } else {
        // Usuário existe mas desativou notificações
        log.info("Envio CANCELADO para '{}': usuário optou por não receber notificações.", email);
      }
    }
    return filtered;
  }

  @Override
  @Async
  @Retryable(retryFor = {Exception.class}, maxAttempts = 3,
      backoff = @Backoff(delay = 5000, multiplier = 2))
  public void sendPasswordResetEmail(String email, String subject, String body) {
    log.info("[ASYNC] Tentando enviar reset de senha para: {}", email);
    try {
      executeSendRaw(email, subject, body);
      log.info("Reset de senha enviado para: {}", email);
    } catch (Exception e) {
      log.warn("Erro ao enviar reset de senha para {}. Retentando... Erro: {}", email,
          e.getMessage());
      throw new EmailServiceException("Erro de envio SMTP:" + e.getMessage());
    }
  }

  // --- Método Fallback (Quando desiste de vez) ---
  @Recover
  public void recoverEmail(Exception e, EmailRequest request) {
    log.error(
        "FALHA DEFINITIVA: Não foi possível enviar o e-mail '{}' após todas as tentativas e timeouts.",
        request.subject());
    // Sugestão: Salvar no banco com status 'ERRO' para reprocessamento manual ou notificar admin
  }

  @Recover
  public void recoverPasswordReset(Exception e, String email, String subject, String body) {
    log.error("FALHA DEFINITIVA: Não foi possível enviar reset de senha para '{}'.", email);
  }

  // --- Métodos Auxiliares Privados ---

  private void executeSend(EmailRequest request) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message,
        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

    helper.setSubject(Objects.requireNonNull(request.subject()));
    helper.setText(Objects.requireNonNull(request.body()), request.html());
    if (fromAddress != null && !fromAddress.isBlank()) {
      helper.setFrom(fromAddress);
    }
    if (request.to() != null) {
      helper.setTo(request.to().toArray(new String[0]));
    }
    if (request.bcc() != null) {
      helper.setBcc(request.bcc().toArray(new String[0]));
    }
    if (request.cc() != null) {
      helper.setCc(request.cc().toArray(new String[0]));
    }

    mailSender.send(message);
  }

  private void executeSendRaw(String to, String subject, String body) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body, true);
    if (fromAddress != null && !fromAddress.isBlank()) {
      helper.setFrom(fromAddress);
    }
    mailSender.send(message);
  }
}
