package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Atualizar Denúncia", description = "Dados para atualizar o status de uma denúncia.")
public class AtualizarDenunciaDto {


  @Schema(
      description = "Novo status da denúncia. Valores possíveis: ABERTA, EM_ANDAMENTO, CONCLUIDA, REJEITADA.",
      example = "EM_ANDAMENTO", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "O status não pode ser nulo")
  private Status status;

  @Schema(
      description = "Motivo da rejeição da denúncia. Obrigatório apenas se o status for 'REJEITADA'.",
      example = "Falta de evidências para prosseguir com a apuração.")
  private String motivoRejeicao;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMotivoRejeicao() {
    return motivoRejeicao;
  }

  public void setMotivoRejeicao(String motivoRejeicao) {
    this.motivoRejeicao = motivoRejeicao;
  }
}
