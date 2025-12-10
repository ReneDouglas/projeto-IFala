package br.edu.ifpi.ifala.shared.exceptions;

public class InvalidRoleException extends AutenticacaoException {
  public InvalidRoleException() {
    super("Perfil(s) fornecido(s) é(são) inválido(s).", 400);
  }

  public InvalidRoleException(String message) {
    super(message, 400);
  }
}
