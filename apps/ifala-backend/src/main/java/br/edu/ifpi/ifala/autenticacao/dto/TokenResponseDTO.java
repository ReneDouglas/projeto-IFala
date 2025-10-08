package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para encapsular os tokens retornados pelo Keycloak.
 */
public class TokenResponseDTO {
  private String accessToken;
  private String refreshToken;

  public TokenResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }
}
