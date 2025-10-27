package br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO que representa uma mensagem no histórico de acompanhamento de uma
 * denúncia.
 *
 * @author Jhonatas G Ribeiro
 */
@Schema(name = "Acompanhamento", description = "Representa uma mensagem no histórico de uma denúncia.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AcompanhamentoDto(
    @Schema(description = "Conteúdo da mensagem de acompanhamento.", example = "Gostaria de adicionar que o evento ocorreu próximo à biblioteca.", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A mensagem não pode ser vazia.") @Size(min = 1, max = 2000, message = "A mensagem deve ter entre 1 e 2000 caracteres.") String mensagem,

    @Schema(description = "Nome do autor da mensagem (pode ser 'DENUNCIANTE', o nome do denunciante ou o nome do administrador).", example = "Admin User", accessMode = Schema.AccessMode.READ_ONLY) String autor,

    @Schema(description = "Data e hora em que a mensagem foi enviada.", example = "2025-10-17T14:30:00", accessMode = Schema.AccessMode.READ_ONLY) LocalDateTime dataEnvio) {

  public AcompanhamentoDto(String mensagem) {
    this(mensagem, null, null);
  }
}