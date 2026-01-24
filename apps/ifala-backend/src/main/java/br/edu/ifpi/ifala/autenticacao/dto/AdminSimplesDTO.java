package br.edu.ifpi.ifala.autenticacao.dto;

/**
 * DTO simples para representar um admin (nome e email).
 * Usado para listagem em filtros de denúncias.
 */
public record AdminSimplesDTO(String nome, String email) {
}
