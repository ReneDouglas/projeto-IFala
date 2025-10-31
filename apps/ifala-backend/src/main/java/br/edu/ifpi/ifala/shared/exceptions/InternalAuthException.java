package br.edu.ifpi.ifala.shared.exceptions;

public class InternalAuthException extends AutenticacaoException {
  public InternalAuthException() {
    super("Erro interno de autenticação.", 500);
  }

  public InternalAuthException(String message) {
    super(message, 500);
  }
}
