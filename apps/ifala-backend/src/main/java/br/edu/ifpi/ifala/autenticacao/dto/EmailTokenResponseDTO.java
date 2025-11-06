package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO de resposta para validação de token de redefinição de senha.
 *
 * @param email Email do usuário associado ao token
 */
public record EmailTokenResponseDTO(String email) {
}
