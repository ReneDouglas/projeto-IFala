package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para requisições de primeiro acesso (username e senha temporária).
 */
public class PrimeiroAcessoRequestDto {
  private String username;
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
