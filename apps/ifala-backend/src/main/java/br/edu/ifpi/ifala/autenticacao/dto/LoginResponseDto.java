package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO para respostas de login.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

import java.time.Instant;

public record LoginResponseDto(String token, Instant issuedAt, Instant expirationTime,
    String refreshToken, boolean passwordChangeRequired, String redirect, String message) {
}
