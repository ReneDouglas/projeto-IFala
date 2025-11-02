package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.Notificacao;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import java.time.LocalDateTime;

/**
 * DTO de resposta para exibição de notificações.
 */
public class NotificationResponseDto {

    private Long id;
    private String titulo;
    private String mensagem;
    private TiposNotificacao tipo;
    private LocalDateTime criadoEm;

    public NotificationResponseDto(Long id, String titulo, String mensagem, TiposNotificacao tipo, LocalDateTime criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.criadoEm = criadoEm;
    }

    public static NotificationResponseDto fromEntity(Notificacao n) {
        return new NotificationResponseDto(
                n.getId(),
                n.getTitulo(),
                n.getMensagem(),
                n.getTipo(),
                n.getCriadoEm()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public TiposNotificacao getTipo() {
        return tipo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
