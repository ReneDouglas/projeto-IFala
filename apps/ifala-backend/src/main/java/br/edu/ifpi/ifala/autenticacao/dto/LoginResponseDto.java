package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

public record LoginResponseDTO(String token, Instant issuedAt, Instant expirationTime,
    String refreshToken, boolean passwordChangeRequired, String redirect, String message) {
}
