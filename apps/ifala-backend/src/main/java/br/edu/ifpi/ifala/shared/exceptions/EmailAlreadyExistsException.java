package br.edu.ifpi.ifala.shared.exceptions;

public class EmailAlreadyExistsException extends AutenticacaoException {
  public EmailAlreadyExistsException() {
    super("E-mail já cadastrado. Utilize outro e-mail ou recupere a senha.", 409);
  }

  public EmailAlreadyExistsException(String message) {
    super(message, 409);
  }
}
