package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de refresh de token.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
public record RefreshTokenRequestDTO(@NotBlank(message = "O token é obrigatório.") String token) {

  /**
   * Implementação segura de toString() que mascara o refresh token. NUNCA expor tokens completos
   * nos logs.
   */
  @Override
  public String toString() {
    return "RefreshTokenRequestDTO{token='" + maskToken(token) + "'}";
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
