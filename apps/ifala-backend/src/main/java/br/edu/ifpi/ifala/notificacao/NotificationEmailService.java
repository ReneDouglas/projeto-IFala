package br.edu.ifpi.ifala.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationEmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNewDenunciaEmail(Notificacao notificacao, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[IFala] Nova Denúncia Cadastrada");
        String text = String.format(
                "Olá!\n\nUma nova denúncia foi registrada no sistema IFala.\n\nTítulo: %s\nMensagem: %s\n\nAtenciosamente,\nEquipe IFala!",
                notificacao.getTitulo(), notificacao.getMensagem());
        message.setText(text);
        mailSender.send(message);
    }
}
