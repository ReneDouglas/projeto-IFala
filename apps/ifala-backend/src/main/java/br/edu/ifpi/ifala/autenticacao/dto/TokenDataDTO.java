package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

/**
 * DTO que representa os dados de um token de autenticação.
 *
 * @param token o token de autenticação
 * @param issuedAt o instante em que o token foi emitido
 * @param expirationTime o instante em que o token expira
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

public record TokenDataDTO(String token, Instant issuedAt, Instant expirationTime) {

  /**
   * Implementação segura de toString() que mascara o token JWT. NUNCA expor tokens completos nos
   * logs.
   */
  @Override
  public String toString() {
    return "TokenDataDTO{" + "token='" + maskToken(token) + '\'' + ", issuedAt=" + issuedAt
        + ", expirationTime=" + expirationTime + '}';
  }

  private static String maskToken(String token) {
    if (token == null)
      return "null";
    if (token.isEmpty())
      return "[empty]";
    if (token.length() <= 8)
      return "***";
    return token.substring(0, 8) + "...***";
  }
}
