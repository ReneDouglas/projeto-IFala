package br.edu.ifpi.ifala.autenticacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para requisições de primeiro acesso (username e senha temporária).
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Requisição de Primeiro Acesso", description = "Credenciais temporárias para o primeiro acesso do usuário.")
public class PrimeiroAcessoRequestDto {

  @Schema(description = "Nome de usuário (matrícula ou e-mail).", example = "20211TINFO002", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @Schema(description = "Senha temporária fornecida ao usuário.", example = "senhaTemporariaXYZ", requiredMode = Schema.RequiredMode.REQUIRED)
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
