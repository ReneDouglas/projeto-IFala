package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.constraints.NotNull;

public class AtualizarDenunciaDto {

  @NotNull(message = "O status não pode ser nulo")
  private Status status;

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