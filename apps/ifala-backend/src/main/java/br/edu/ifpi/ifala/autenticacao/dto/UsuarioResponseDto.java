package br.edu.ifpi.ifala.autenticacao.dto;

import java.util.List;
import br.edu.ifpi.ifala.shared.enums.Perfis;

/**
 * DTO de resposta para operações com usuário. Omite campos sensíveis como id, senha, createdAt,
 * etc.
 */
public record UsuarioResponseDto(String nome, String email, String username, List<Perfis> roles) {
}
