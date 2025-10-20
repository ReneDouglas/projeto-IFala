package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

public record TokenDataDto(String token, Instant issuedAt, Instant expirationTime) {
}
