package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import java.util.List;

/**
 * Interface que define os serviços de notificação.
 */ 
public interface NotificationService {

  /**
   * Cria uma nova notificação.
   *
   * @param dto Os dados da notificação a ser criada
   * @return A notificação criada
   */
  NotificationResponseDto create(NotificationRequestDto dto);

  /**
   * Recupera todas as notificações.
   *
   * @return Lista com todas as notificações
   */
  List<NotificationResponseDto> findAll();

  /**
   * Atualiza uma notificação existente.
   *
   * @param id O ID da notificação a ser atualizada
   * @param dto Os novos dados da notificação
   * @return A notificação atualizada
   */
  NotificationResponseDto update(Long id, NotificationRequestDto dto);

  /**
   * Remove uma notificação.
   *
   * @param id O ID da notificação a ser removida
   */
  void delete(Long id);
}
