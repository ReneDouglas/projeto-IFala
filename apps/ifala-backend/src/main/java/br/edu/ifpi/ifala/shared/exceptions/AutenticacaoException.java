package br.edu.ifpi.ifala.shared.exceptions;

/**
 * Exceção personalizada para erros de autenticação/autoriza��o e erros de neg�cio relacionados.
 * Carrega um status HTTP para que o handler global possa converter para a resposta adequada.
 */
public class AutenticacaoException extends RuntimeException {
  private final int statusCode;

  public AutenticacaoException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
