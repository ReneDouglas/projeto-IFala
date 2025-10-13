package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para respostas de autenticação.
 */
public class AuthResponseDTO {
  private String accessToken;
  private String refreshToken;
  private Boolean success;
  private String redirectUrl;
  private String message;

  public AuthResponseDTO(String accessToken, String refreshToken, Boolean success,
      String redirectUrl, String message) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.success = success;
    this.redirectUrl = redirectUrl;
    this.message = message;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public Boolean getSuccess() {
    return success;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public String getMessage() {
    return message;
  }
}
