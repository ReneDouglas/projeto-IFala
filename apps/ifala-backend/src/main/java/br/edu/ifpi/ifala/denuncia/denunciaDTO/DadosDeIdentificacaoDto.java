package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Ano;
import br.edu.ifpi.ifala.shared.enums.Curso;
import br.edu.ifpi.ifala.shared.enums.Grau;
import br.edu.ifpi.ifala.shared.enums.Turma;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para representar os dados de identificação do denunciante em uma
 * denúncia.
 * 
 * @author Jhonatas G Ribeiro
 * @author Phaola (refatoração)
 */
// DTO convertido em record para representar os dados de identificação da
// denúncia
public record DadosDeIdentificacaoDto(
    @NotBlank(message = "O nome não pode ser vazio.") @Size(max = 255,
        message = "O nome completo não pode exceder 255 caracteres.") String nomeCompleto,

    @NotBlank(message = "O email não pode ser vazio.") @Email(
        message = "Formato de email inválido.") @Size(max = 255,
            message = "O email não pode exceder 255 caracteres.") String email,

    @NotNull(message = "O grau não pode ser nulo.") Grau grau,

    @NotNull(message = "O curso não pode ser nulo.") Curso curso,

    Ano ano,

    @NotNull(message = "A turma não pode ser nula.") Turma turma) {
}
