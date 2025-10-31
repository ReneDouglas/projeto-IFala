package br.edu.ifpi.ifala.shared.exceptions;

public class MissingAuthorizationHeaderException extends AutenticacaoException {
  public MissingAuthorizationHeaderException() {
    super("Header Authorization ausente ou inválido.", 400);
  }

  public MissingAuthorizationHeaderException(String message) {
    super(message, 400);
  }
}
