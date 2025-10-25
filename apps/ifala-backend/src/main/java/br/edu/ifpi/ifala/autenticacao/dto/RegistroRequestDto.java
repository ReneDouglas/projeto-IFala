package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RegistroRequestDTO(@NotBlank(message = "O nome é obrigatório.") String nome,

    @Email(message = "O e-mail deve ser válido.") @NotBlank(message = "O e-mail é obrigatório.") String email,

    @NotBlank(message = "A senha é obrigatória.") String senha,

    @NotBlank(message = "O nome de usuário é obrigatório.") String username,

    List<String> roles) {

}
