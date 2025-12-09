package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de refresh de token.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */
public record RefreshTokenRequestDTO(@NotBlank(message = "O token é obrigatório.") String token) {
}
