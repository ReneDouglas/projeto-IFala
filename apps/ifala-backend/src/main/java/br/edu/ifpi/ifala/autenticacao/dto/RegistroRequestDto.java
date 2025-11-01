package br.edu.ifpi.ifala.autenticacao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO para requisição de registro de usuário.
 *
 * @param nome Nome do usuário.
 * @param email E-mail do usuário.
 * @param senha Senha do usuário.
 * @param username Nome de usuário.
 * @param roles Lista de papéis/roles do usuário.
 * 
 * @author Phaola
 */

public record RegistroRequestDto(@NotBlank(message = "O nome é obrigatório.") String nome,

    @Email(message = "O e-mail deve ser válido.") @NotBlank(
        message = "O e-mail é obrigatório.") String email,

    @NotBlank(message = "A senha é obrigatória.") String senha,

    @NotBlank(message = "O nome de usuário é obrigatório.") String username,

    List<String> roles) {

}
