package br.edu.ifpi.ifala.shared.exceptions;

/**
 * Exceção personalizada para erros de autenticação/autorização e erros de negócio relacionados.
 * Carrega um status HTTP para que o handler global possa converter para a resposta adequada.
 * 
 * @author Phaola
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
