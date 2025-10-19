package br.edu.ifpi.ifala.autenticacao;

import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class RedefinirSenhaServiceImpl implements RedefinirSenhaService {

  private final JavaMailSender mailSender;
  private final UsuarioRepository userRepository;

  public RedefinirSenhaServiceImpl(JavaMailSender mailSender, UsuarioRepository userRepository) {
    this.mailSender = mailSender;
    this.userRepository = userRepository;
  }

  // Esta URL é usada para gerar o link de redefinição de senha enviado por e-mail.
  // O link só funcionará corretamente quando o front-end estiver configurado e acessível.
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
