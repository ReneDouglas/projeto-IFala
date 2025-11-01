package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para receber requisições de criação/atualização de notificações.
 */
public class NotificationRequestDto {

  @NotBlank(message = "O título é obrigatório")
  @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
  private String titulo;

  @NotBlank(message = "A mensagem é obrigatória")
  @Size(min = 10, max = 500, message = "A mensagem deve ter entre 10 e 500 caracteres")
  private String mensagem;

  @NotNull(message = "O tipo de notificação é obrigatório")
  private TiposNotificacao tipo;

  public String getTitulo() {
    return titulo;
  }
  
  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getMensagem() {
    return mensagem;
  }
  
  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public TiposNotificacao getTipo() {
    return tipo;
  }
  
  public void setTipo(TiposNotificacao tipo) {
    this.tipo = tipo;
  }
}
