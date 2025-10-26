package br.edu.ifpi.ifala.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationEmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia um e-mail quando uma nova denúncia é cadastrada.
     * Este método é usado para notificações do tipo EXTERNAL.
     *
     * @param notificacao 
     */
    public void sendNewDenunciaEmail(Notificacao notificacao) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("sistema.ifala.developmentt@gmail.com");

        message.setTo("sistema.ifala.developmentt@gmail.com");

        message.setSubject("[IFala] Nova Denúncia Cadastrada");

  
        message.setText(
            "Olá!\n\n" +
            "Uma nova denúncia foi registrada no sistema IFala.\n\n" +
            "Título: " + notificacao.getTitulo() + "\n" +
            "Mensagem: " + notificacao.getMensagem() + "\n\n" +
            "Atenciosamente,\nEquipe IFala!"
        );

        mailSender.send(message);
    }
}
