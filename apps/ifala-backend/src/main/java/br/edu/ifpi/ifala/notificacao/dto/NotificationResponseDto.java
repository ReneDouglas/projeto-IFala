package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.Notificacao;
import br.edu.ifpi.ifala.notificacao.enums.TipoNotificacao;
import java.time.LocalDateTime;

/**
 * DTO de resposta para exibição de notificações.
 * Implementado como record, seguindo o padrão do backend do IFala.
 */
public record NotificationResponseDto(
        Long id,
        String titulo,
        String mensagem,
        TipoNotificacao tipo,
        LocalDateTime criadoEm) {

    /**
     * Constrói um DTO a partir da entidade Notificacao.
     *
     * @param n entidade Notificacao
     * @return um novo NotificationResponseDto com os dados da entidade
     */
    public static NotificationResponseDto fromEntity(Notificacao n) {
        return new NotificationResponseDto(
                n.getId(),
                n.getTitulo(),
                n.getMensagem(),
                n.getTipo(),
                n.getCriadoEm());
    }
}
