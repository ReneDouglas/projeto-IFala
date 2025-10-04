package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para requisições de logout.
 */
public class LogoutRequestDTO {
  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
