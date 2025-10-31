package br.edu.ifpi.ifala.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respostas de autenticação.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Resposta de Autenticação", description = "Objeto de resposta para operações de login, logout e primeiro acesso.")
public class AuthResponseDto {
  @Schema(description = "Token de acesso JWT para autorização de requisições.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;
  @Schema(description = "Token de atualização para obter um novo token de acesso sem precisar logar novamente.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;
  @Schema(description = "Indica se a operação foi bem-sucedida.", example = "true")
  private Boolean success;
  @Schema(description = "URL para a qual o frontend deve redirecionar o usuário após a operação.", example = "/dashboard")
  private String redirectUrl;
  @Schema(description = "Mensagem informativa sobre o resultado da operação.", example = "Login bem-sucedido. Redirecionando para: /dashboard")
  private String message;

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
