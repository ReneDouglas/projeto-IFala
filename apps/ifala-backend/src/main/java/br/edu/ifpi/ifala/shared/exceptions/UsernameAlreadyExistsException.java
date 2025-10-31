package br.edu.ifpi.ifala.shared.exceptions;

public class UsernameAlreadyExistsException extends AutenticacaoException {
  public UsernameAlreadyExistsException() {
    super("Username já cadastrado. Por favor, escolha outro.", 409);
  }

  public UsernameAlreadyExistsException(String message) {
    super(message, 409);
  }
}
