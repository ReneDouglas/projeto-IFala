package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import br.edu.ifpi.ifala.notificacao.dto.PaginatedNotificacaoDto;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface NotificacaoService {

  /**
   * Lista as notificações não lidas com paginação.
   * 
   * @param pageable Parâmetros de paginação
   * @return DTO paginado com notificações não lidas e metadados
   */
  PaginatedNotificacaoDto listarNaoLidas(Pageable pageable);

  /**
   * Marca uma notificação como lida e retorna o DTO.
   * 
   * @param id ID da notificação
   * @param usuario Usuário que marcou como lida
   * @return DTO da notificação ou empty se não encontrada
   */
  Optional<NotificacaoDto> marcarComoLidaDto(Long id, String usuario);

  Optional<Notificacao> marcarComoLida(Long id, String usuario);

  void marcarComoLidaPorDenuncia(Long denunciaId, String usuario);

  boolean existsById(Long id);

  void deletar(Long id);

}
