package br.edu.ifpi.ifala.shared.exceptions;

public class InvalidTokenException extends AutenticacaoException {
  public InvalidTokenException() {
    super("Token inválido para redefinição de senha.", 401);
  }

  public InvalidTokenException(String message) {
    super(message, 401);
  }
}
