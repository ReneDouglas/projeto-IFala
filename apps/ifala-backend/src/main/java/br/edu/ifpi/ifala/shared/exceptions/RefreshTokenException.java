package br.edu.ifpi.ifala.shared.exceptions;

/**
 * Exceção específica para problemas relacionados ao fluxo de Refresh Token. Estende
 * AutenticacaoException para carregar um status HTTP junto com a mensagem. Usada para sinalizar ao
 * handler global que deve ser enviado um cookie de logout (o handler decide isso).
 */
public class RefreshTokenException extends AutenticacaoException {
  public RefreshTokenException(String message, int statusCode) {
    super(message, statusCode);
  }
}
