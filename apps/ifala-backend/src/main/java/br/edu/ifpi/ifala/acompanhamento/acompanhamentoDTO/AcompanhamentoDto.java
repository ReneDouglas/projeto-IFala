package br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "Acompanhamento",
    description = "Representa uma mensagem no histórico de uma denúncia.")
public class AcompanhamentoDto {

  @Schema(description = "Conteúdo da mensagem de acompanhamento.",
      example = "Gostaria de adicionar que o evento ocorreu próximo à biblioteca.",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "A mensagem não pode ser vazia.")
  @Size(min = 1, max = 2000, message = "A mensagem deve ter entre 1 e 2000 caracteres.")
  private String mensagem;

  @Schema(
      description = "Nome do autor da mensagem (pode ser 'Denunciante' ou o nome do administrador).",
      example = "Admin User", accessMode = Schema.AccessMode.READ_ONLY)
  private String autor;

  @Schema(description = "Data e hora em que a mensagem foi enviada.",
      example = "2025-10-17T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime dataEnvio;

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }
}
