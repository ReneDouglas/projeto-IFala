package br.edu.ifpi.ifala.autenticacao.dto;

import java.io.Serializable;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisições de login, suportando autenticação por email ou username (matrícula/usuário).
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Requisição de Login", description = "Dados necessários para autenticar um usuário.")
public class LoginRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Email(message = "O email deve estar em um formato válido.")
  @Size(max = 255, message = "O email não pode ter mais de 255 caracteres.")
  @Schema(description = "E-mail do usuário (opcional, pode ser usado no lugar do username).",
      example = "usuario@ifpi.edu.br", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String email;

  // Ajustado para manter a validação do username (matrícula/usuário)
  @Size(min = 3, max = 25, message = "O nome de usuário deve ter entre 3 e 25 caracteres.")
  @Schema(
      description = "Nome de usuário (geralmente matrícula ou outro identificador, obrigatório se o email for nulo).",
      example = "20211TINFO001", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @NotBlank(message = "A senha é obrigatória.")
  @Size(min = 8, max = 100, message = "A senha deve ter pelo menos 8 caracteres.")
  @Schema(description = "Senha de acesso do usuário.", example = "senhaForte123",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  public LoginRequestDTO() {}

  public LoginRequestDTO(String email, String username, @NotBlank String password) {
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

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

  // Métodos utilitários (mantidos da versão completa)
  public String email() {
    return getEmail();
  }

  public String username() {
    return getUsername();
  }

  public String password() {
    return getPassword();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    LoginRequestDTO that = (LoginRequestDTO) o;
    return Objects.equals(email, that.email) && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, username);
  }

  @Override
  public String toString() {
    return "LoginRequestDto{" + "email='" + email + '\'' + ", username='" + username + '\'' + '}';
  }
}
