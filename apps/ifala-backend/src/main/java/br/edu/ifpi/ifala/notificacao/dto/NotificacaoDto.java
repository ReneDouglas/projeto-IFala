package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de Notificação.
 *
 * @author Phaola
 */

public record NotificacaoDto(Long id, String conteudo, TiposNotificacao tipo, Long denunciaId,
    Boolean lida, String lidaPor, LocalDateTime dataEnvio) {
}
