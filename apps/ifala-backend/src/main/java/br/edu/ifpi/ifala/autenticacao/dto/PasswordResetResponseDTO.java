package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO de resposta para requisição de redefinição de senha. Retorna um indicador de sucesso e uma
 * mensagem legível para o cliente.
 *
 * @author Phaola
 */
public record PasswordResetResponseDTO(boolean success, String message) {
}
