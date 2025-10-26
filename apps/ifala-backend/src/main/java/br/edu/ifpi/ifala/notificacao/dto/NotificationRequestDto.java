package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.NotificationType;

public class NotificationRequestDto {

    private String titulo;
    private String mensagem;
    private NotificationType tipo;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public NotificationType getTipo() { return tipo; }
    public void setTipo(NotificationType tipo) { this.tipo = tipo; }
}
