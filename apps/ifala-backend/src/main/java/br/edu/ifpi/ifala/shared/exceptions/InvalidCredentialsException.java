package br.edu.ifpi.ifala.shared.exceptions;

public class InvalidCredentialsException extends AutenticacaoException {
  public InvalidCredentialsException() {
    super("Usuário ou senha inválidos.", 401);
  }

  public InvalidCredentialsException(String message) {
    super(message, 401);
  }
}
