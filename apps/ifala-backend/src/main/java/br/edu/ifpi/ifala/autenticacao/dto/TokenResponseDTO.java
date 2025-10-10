package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para encapsular os tokens retornados pelo Keycloak.
 */
public class TokenResponseDto {
  private String accessToken;
  private String refreshToken;

  /**
   * Construtor para TokenResponseDto.
   *
   * @param accessToken o token de acesso retornado pelo Keycloak
   * @param refreshToken o token de atualização retornado pelo Keycloak
   */
  public TokenResponseDto(String accessToken, String refreshToken) {
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
