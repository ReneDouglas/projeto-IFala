package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para alterar o status de uma denúncia com envio automático de mensagem
 * de acompanhamento.
 * 
 * @author Guilherme Alves
 */
@Schema(name = "Alterar Status",
    description = "Dados para alterar o status de uma denúncia com mensagem automática.")
public record AlterarStatusDto(

    @Schema(description = "Novo status da denúncia.", example = "EM_ANALISE",
        requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(
            message = "O status não pode ser nulo") Status status) {
}
