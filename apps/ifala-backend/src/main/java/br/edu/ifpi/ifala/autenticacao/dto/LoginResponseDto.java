package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

/**
 * DTO para respostas de login.
 *
 * Author: Phaola
 */
public record LoginResponseDto(String token, Instant issuedAt, Instant expirationTime,
    String refreshToken, boolean passwordChangeRequired, String redirect, String message) {
}
