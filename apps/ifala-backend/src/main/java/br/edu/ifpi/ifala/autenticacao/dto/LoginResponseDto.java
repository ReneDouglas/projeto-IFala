package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

public record LoginResponseDto(String token, Instant issuedAt, Instant expirationTime,
    String refreshToken, boolean passwordChangeRequired, String redirect, String message) {
}
