package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.EmailRequest;

/**
 * Serviço responsável pelo envio de e-mails no sistema.
 *
 * @author Phaola
 */

public interface EmailService {

  /**
   * Envia um e-mail usando um DTO com suporte a múltiplos destinatários, cc, bcc e HTML.
   */
  void sendEmail(EmailRequest request);

  /**
   * Envia um e-mail de redefinição de senha.
   * 
   * @param email E-mail do destinatário
   * @param subject Assunto do e-mail
   * @param body Corpo do e-mail
   */
  void sendPasswordResetEmail(String email, String subject, String body);

}
