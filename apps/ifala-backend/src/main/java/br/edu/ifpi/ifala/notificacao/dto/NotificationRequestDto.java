package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para criação e atualização de notificações.
 */
public class NotificationRequestDto {

    @NotBlank(message = "O título é obrigatório.")
    private String titulo;

    @NotBlank(message = "A mensagem é obrigatória.")
    private String mensagem;

    @NotNull(message = "O tipo de notificação é obrigatório.")
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
