package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para mudança de senha.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

// DTO para receber os dados de mudança de senha
public record MudarSenhaRequestDTO(
    @NotBlank(message = "O e-mail é obrigatório.") @Email(
        message = "O e-mail deve ser válido.") String email,
    String currentPassword, String newPassword, String token) {

  /**
   * Implementação segura de toString() que mascara senhas e tokens. NUNCA expor dados sensíveis nos
   * logs.
   */
  @Override
  public String toString() {
    return "MudarSenhaRequestDTO{" + "email='" + email + '\'' + ", currentPassword='***'"
        + ", newPassword='***'" + ", token='" + maskToken(token) + '\'' + '}';
  }

  private static String maskToken(String token) {
    if (token == null)
      return "null";
    if (token.isEmpty())
      return "[empty]";
    if (token.length() <= 8)
      return "***";
    return token.substring(0, 8) + "...***";
  }
}
