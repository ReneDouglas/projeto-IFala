package br.edu.ifpi.ifala.autenticacao.dto;

import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarUsuarioRequestDTO(@NotBlank(message = "O nome é obrigatório.") String nome,

    @Email(message = "O e-mail deve ser válido.") @NotBlank(
        message = "O e-mail é obrigatório.") String email,

    @NotBlank(message = "O nome de usuário é obrigatório.") String username,

    List<String> roles,

    @NotNull(message = "O campo 'mustChangePassword' é obrigatório.") Boolean mustChangePassword) {
}
