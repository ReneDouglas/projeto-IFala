package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import br.edu.ifpi.ifala.notificacao.dto.PaginatedNotificacaoDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre entidades Notificacao e DTOs.
 *
 * @author Phaola
 */
@Component
public class NotificacaoMapper {

  /**
   * Converte uma entidade Notificacao para NotificacaoDto.
   * 
   * @param notificacao Entidade a ser convertida
   * @return DTO correspondente ou null se a entidade for null
   */
  public NotificacaoDto toDto(Notificacao notificacao) {
    if (notificacao == null) {
      return null;
    }

    return new NotificacaoDto(notificacao.getId(), notificacao.getConteudo(), notificacao.getTipo(),
        notificacao.getDenuncia() != null ? notificacao.getDenuncia().getId() : null,
        notificacao.getLida(), notificacao.getLidaPor(), notificacao.getDataEnvio());
  }

  /**
   * Converte uma Page de Notificacao para PaginatedNotificacaoDto.
   * 
   * @param page Página de notificações do repositório
   * @return DTO paginado com metadados completos
   */
  public PaginatedNotificacaoDto toPaginatedDto(Page<Notificacao> page) {
    return new PaginatedNotificacaoDto(page.getContent().stream().map(this::toDto).toList(),
        page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
  }
}
