package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para atualizar o status de uma denúncia.
 * 
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Atualizar Denúncia", description = "Dados para atualizar o status de uma denúncia.")
public record AtualizarDenunciaDto(

    @Schema(description = "Novo status da denúncia.", example = "EM_ANALISE", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "O status não pode ser nulo") Status status,

    @Schema(description = "Motivo da rejeição (obrigatório apenas se o status for REJEITADO).", example = "Falta de evidências.") @Size(max = 2000, message = "O motivo da rejeição não pode exceder 2000 caracteres.") String motivoRejeicao) {
}