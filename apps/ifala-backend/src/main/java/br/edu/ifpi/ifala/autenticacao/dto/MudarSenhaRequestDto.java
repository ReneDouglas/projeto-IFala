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
public record MudarSenhaRequestDto(
    @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "O e-mail deve ser válido.") String email,
    String currentPassword, @NotBlank(message = "A nova senha é obrigatória.") String newPassword,
    String token) {
}
