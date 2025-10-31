package br.edu.ifpi.ifala.autenticacao.dto;

import java.time.Instant;

/**
 * DTO que representa os dados de um token de autenticação.
 *
 * @param token          o token de autenticação
 * @param issuedAt       o instante em que o token foi emitido
 * @param expirationTime o instante em que o token expira
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

public record TokenDataDto(String token, Instant issuedAt, Instant expirationTime) {
}
