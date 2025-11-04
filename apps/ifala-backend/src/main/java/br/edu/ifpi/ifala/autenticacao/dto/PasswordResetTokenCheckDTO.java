package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO simples retornado ao validar um token de redefinição de senha. Retorna apenas email e
 * username.
 * 
 * @author Phaola
 */
public record PasswordResetTokenCheckDTO(String email, String username) {
}
