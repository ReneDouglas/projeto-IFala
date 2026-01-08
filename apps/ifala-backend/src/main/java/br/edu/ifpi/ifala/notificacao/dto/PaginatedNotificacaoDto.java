package br.edu.ifpi.ifala.notificacao.dto;

import java.util.List;

/**
 * DTO para resposta paginada de notificações.
 *
 * @author Phaola
 */
public record PaginatedNotificacaoDto(List<NotificacaoDto> items, long totalItems, int totalPages,
    int currentPage, int pageSize) {
}
