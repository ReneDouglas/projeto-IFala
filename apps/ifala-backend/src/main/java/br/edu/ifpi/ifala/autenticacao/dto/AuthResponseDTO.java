package br.edu.ifpi.ifala.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respostas de autenticação.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Resposta de Autenticação",
    description = "Objeto de resposta para operações de login, logout e primeiro acesso.")
public class AuthResponseDTO {
  @Schema(description = "Token de acesso JWT para autorização de requisições.",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;
  @Schema(
      description = "Token de atualização para obter um novo token de acesso sem precisar logar novamente.",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;
  @Schema(description = "Indica se a operação foi bem-sucedida.", example = "true")
  private Boolean success;
  @Schema(description = "URL para a qual o frontend deve redirecionar o usuário após a operação.",
      example = "/dashboard")
  private String redirectUrl;
  @Schema(description = "Mensagem informativa sobre o resultado da operação.",
      example = "Login bem-sucedido. Redirecionando para: /dashboard")
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

  /**
   * Implementação segura de toString() que mascara informações sensíveis. Nunca deve expor tokens
   * completos nos logs.
   */
  @Override
  public String toString() {
    return "AuthResponseDTO{" + "accessToken='" + maskToken(accessToken) + '\'' + ", refreshToken='"
        + maskToken(refreshToken) + '\'' + ", success=" + success + ", redirectUrl='" + redirectUrl
        + '\'' + ", message='" + message + '\'' + '}';
  }

  /**
   * Mascara um token mostrando apenas os primeiros 8 caracteres seguidos de "...***".
   * 
   * @param token o token a ser mascarado
   * @return token mascarado ou indicação de null/vazio
   */
  private static String maskToken(String token) {
    if (token == null) {
      return "null";
    }
    if (token.isEmpty()) {
      return "[empty]";
    }
    if (token.length() <= 8) {
      return "***";
    }
    return token.substring(0, 8) + "...***";
  }
}
