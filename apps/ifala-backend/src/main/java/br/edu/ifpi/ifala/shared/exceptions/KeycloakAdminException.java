package br.edu.ifpi.ifala.shared.exceptions;

public class KeycloakAdminException extends Exception {
  /**
   * Construtor com mensagem de erro.
   *
   * @param message Mensagem de erro
   */
  public KeycloakAdminException(String message) {
    super(message);
  }

  /**
   * Construtor com mensagem de erro e causa.
   *
   * @param message Mensagem de erro
   * @param cause Causa da exceção
   */
  public KeycloakAdminException(String message, Throwable cause) {
    super(message, cause);
  }
}
