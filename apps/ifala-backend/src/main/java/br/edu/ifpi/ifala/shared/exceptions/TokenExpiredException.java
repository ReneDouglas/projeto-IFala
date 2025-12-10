package br.edu.ifpi.ifala.shared.exceptions;

public class TokenExpiredException extends AutenticacaoException {
  public TokenExpiredException() {
    super("Token expirado para redefinição de senha.", 401);
  }

  public TokenExpiredException(String message) {
    super(message, 401);
  }
}
