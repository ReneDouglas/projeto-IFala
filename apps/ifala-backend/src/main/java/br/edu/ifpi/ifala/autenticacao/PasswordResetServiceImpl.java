package br.edu.ifpi.ifala.autenticacao;

import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

  private final JavaMailSender mailSender;
  private final UserRepository userRepository;

  public PasswordResetServiceImpl(JavaMailSender mailSender, UserRepository userRepository) {
    this.mailSender = mailSender;
    this.userRepository = userRepository;
  }

  @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
  private String resetPasswordUrl;

  @Override
  public void sendPasswordReset(Usuario user) {
    String token = UUID.randomUUID().toString();

    user.setPasswordResetToken(token);
    user.setPasswordResetExpires(Instant.now().plus(1, ChronoUnit.HOURS));
    userRepository.save(user);

    String link = resetPasswordUrl + "?token=" + token + "&email=" + user.getEmail();

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(user.getEmail());
    msg.setSubject("Redefinição de senha - IFala");
    msg.setText("Olá,\n\nAcesse o link abaixo para redefinir sua senha (válido por 1 hora):\n"
        + link + "\n\nSe não solicitou, ignore esta mensagem.");
    mailSender.send(msg);
  }
}
