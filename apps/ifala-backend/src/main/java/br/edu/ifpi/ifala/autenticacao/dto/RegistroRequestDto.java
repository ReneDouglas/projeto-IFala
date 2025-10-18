package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import br.edu.ifpi.ifala.shared.enums.Perfis;

public record RegistroRequestDto(@NotBlank(message = "O nome é obrigatório.") String nome,

    @Email(message = "O e-mail deve ser válido.") @NotBlank(
        message = "O e-mail é obrigatório.") String email,

    @NotBlank(message = "A senha é obrigatória.") String senha,

    List<Perfis> roles) {

}
