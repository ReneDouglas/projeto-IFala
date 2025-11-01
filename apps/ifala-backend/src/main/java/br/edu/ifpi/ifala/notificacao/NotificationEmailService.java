package br.edu.ifpi.ifala.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo envio de notificações por e-mail.
 * Gerencia o envio de e-mails para notificações externas do sistema IFala.
 * Este serviço utiliza o JavaMailSender para realizar o envio dos e-mails
 * de forma assíncrona e confiável.
 */
@Service
public class NotificationEmailService {

  private final JavaMailSender mailSender;

  /**
   * Construtor que injeta o serviço de envio de e-mails.
   *
   * @param mailSender serviço de envio de e-mails
   */
  @Autowired
  public NotificationEmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Envia um e-mail quando uma nova denúncia é cadastrada.
   * Este método é usado para notificações do tipo EXTERNO.
   *
   * @param notificacao A notificação a ser enviada por email
   */
  public void sendNewDenunciaEmail(Notificacao notificacao) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("sistema.ifala.developmentt@gmail.com");
    message.setTo("sistema.ifala.developmentt@gmail.com");
    message.setSubject("[IFala] Nova Denúncia Cadastrada");
    
    String text = String.format(
        "Olá!\n\n"
        + "Uma nova denúncia foi registrada no sistema IFala.\n\n"
        + "Título: %s\n"
        + "Mensagem: %s\n\n"
        + "Atenciosamente,\nEquipe IFala!",
        notificacao.getTitulo(),
        notificacao.getMensagem()
    );
    
    message.setText(text);
    mailSender.send(message);
  }
}
