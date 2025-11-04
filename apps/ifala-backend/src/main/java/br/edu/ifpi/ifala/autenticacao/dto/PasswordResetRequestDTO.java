package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitar o envio do e-mail de redefinição de senha.
 * 
 * @author Phaola
 */
public record PasswordResetRequestDTO(@Email @NotBlank String email) {
}
