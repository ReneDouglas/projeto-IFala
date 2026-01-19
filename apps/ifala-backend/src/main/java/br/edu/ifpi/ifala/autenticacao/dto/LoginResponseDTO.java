package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para respostas de login.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

import java.time.Instant;

public record LoginResponseDTO(String token, Instant issuedAt, Instant expirationTime,
    String refreshToken, boolean passwordChangeRequired, String redirect, String message) {

  /**
   * Implementação segura de toString() que mascara tokens JWT e refresh. NUNCA expor tokens
   * completos nos logs.
   */
  @Override
  public String toString() {
    return "LoginResponseDTO{" + "token='" + maskToken(token) + '\'' + ", issuedAt=" + issuedAt
        + ", expirationTime=" + expirationTime + ", refreshToken='" + maskToken(refreshToken) + '\''
        + ", passwordChangeRequired=" + passwordChangeRequired + ", redirect='" + redirect + '\''
        + ", message='" + message + '\'' + '}';
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
