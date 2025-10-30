package br.edu.ifpi.ifala.shared.exceptions;

public class PasswordMismatchException extends AutenticacaoException {
  public PasswordMismatchException() {
    super("Senha atual incorreta.", 401);
  }

  public PasswordMismatchException(String message) {
    super(message, 401);
  }
}
