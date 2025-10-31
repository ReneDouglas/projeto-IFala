package br.edu.ifpi.ifala.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para requisições de logout.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Requisição de Logout", description = "Dados necessários para invalidar a sessão de um usuário.")
public class LogoutRequestDto {

  @Schema(description = "Access token que será invalidado no servidor de autenticação.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
  private String refreshToken;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
