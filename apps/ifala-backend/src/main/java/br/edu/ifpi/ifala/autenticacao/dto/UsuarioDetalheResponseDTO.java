package br.edu.ifpi.ifala.autenticacao.dto;

import java.util.List;
import br.edu.ifpi.ifala.shared.enums.Perfis;

public record UsuarioDetalheResponseDTO(long id, String nome, String username, String email,
    List<Perfis> roles, boolean mustChangePassword) {
}
