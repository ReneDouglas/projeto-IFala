package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para respostas de autenticação.
 */
public class AuthResponseDto {
  private String accessToken;
  private String refreshToken;
  private Boolean success;
  private String redirectUrl;
  private String message;

  /**
   * Construtor para AuthResponseDto.
   *
   * @param accessToken Token de acesso.
   * @param refreshToken Token de atualização.
   * @param success Indica se a autenticação foi bem-sucedida.
   * @param redirectUrl URL de redirecionamento após autenticação.
   * @param message Mensagem adicional sobre a autenticação.
   */
  public AuthResponseDto(String accessToken, String refreshToken, Boolean success,
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
