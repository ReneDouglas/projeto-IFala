package br.edu.ifpi.ifala.notificacao;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationEmailService {

    private final JavaMailSender mailSender;

    public NotificationEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia um e-mail de notificação externa quando uma nova denúncia é cadastrada.
     *
     * @param notificacao dados da notificação criada
     * @param toEmail destinatário do e-mail
     */
    public void sendNewDenunciaEmail(Notificacao notificacao, String toEmail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[IFala] Nova Denúncia Cadastrada");

            String htmlContent = """
                    <html>
                    <body>
                        <h2>Nova Denúncia Registrada</h2>
                        <p><strong>Título:</strong> %s</p>
                        <p><strong>Mensagem:</strong></p>
                        <p>%s</p>
                        <br/>
                        <p>Atenciosamente,<br/>Equipe IFala</p>
                    </body>
                    </html>
                    """.formatted(notificacao.getTitulo(), notificacao.getMensagem());

            helper.setText(htmlContent, true); // true -> HTML

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de notificação.", e);
        }
    }
}
