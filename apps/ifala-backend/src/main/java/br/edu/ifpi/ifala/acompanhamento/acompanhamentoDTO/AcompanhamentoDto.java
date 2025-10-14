package br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AcompanhamentoDto {

  @NotBlank(message = "A mensagem não pode ser vazia.")
  @Size(min = 1, max = 2000, message = "A mensagem deve ter entre 1 e 2000 caracteres.")
  private String mensagem;

  private String autor;
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