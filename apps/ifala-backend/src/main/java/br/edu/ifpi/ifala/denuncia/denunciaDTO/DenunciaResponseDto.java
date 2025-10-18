package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

public class DenunciaResponseDto {

  private UUID tokenAcompanhamento;
  private Status status;
  private Categorias categoria;
  private LocalDateTime criadoEm;

  public UUID getTokenAcompanhamento() {
    return tokenAcompanhamento;
  }

  public void setTokenAcompanhamento(UUID tokenAcompanhamento) {
    this.tokenAcompanhamento = tokenAcompanhamento;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Categorias getCategoria() {
    return categoria;
  }

  public void setCategoria(Categorias categoria) {
    this.categoria = categoria;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }
}