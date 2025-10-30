package br.edu.ifpi.ifala.shared.exceptions;

public class UserNotFoundException extends AutenticacaoException {
  public UserNotFoundException() {
    super("Usuário não encontrado para o e-mail informado.", 404);
  }

  public UserNotFoundException(String message) {
    super(message, 404);
  }
}
