package br.edu.ifpi.ifala.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para requisições de login (email e senha).
 */
@Schema(name = "Requisição de Login", description = "Dados necessários para autenticar um usuário.")
public class LoginRequestDTO {

  @Schema(description = "Nome de usuário (geralmente matrícula ou e-mail).",
      example = "20211TINFO001", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @Schema(description = "Senha de acesso do usuário.", example = "senhaForte123",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
