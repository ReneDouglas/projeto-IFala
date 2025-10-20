package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@Email(message = "O e-mail deve ser válido.") String email,
    String username, @NotBlank(message = "A senha é obrigatória.") String password) {
}


