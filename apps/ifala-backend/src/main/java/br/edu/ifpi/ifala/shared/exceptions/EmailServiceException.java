package br.edu.ifpi.ifala.shared.exceptions;

public class EmailServiceException extends AutenticacaoException {
  public EmailServiceException() {
    super("Falha ao enviar e-mail de redefinição de senha. Verifique as configurações de e-mail.",
        500);
  }

  public EmailServiceException(String message) {
    super(message, 500);
  }
}
