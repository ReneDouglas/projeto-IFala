package br.edu.ifpi.ifala.notificacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositório JPA para gerenciar entidades de Notificação no sistema.
 *
 * @author Phaola
 */

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

  /**
   * Busca TODAS as notificações não lidas, ordenadas por data de envio (mais recentes primeiro).
   * 
   * @return Lista completa de notificações não lidas ordenadas da mais recente para a mais antiga
   */

  /**
   * Busca notificações não lidas com paginação.
   * 
   * @param pageable Paginação com limite configurável
   * 
   * @return Página de notificações não lidas ordenadas da mais recente para a mais antiga
   */
  @Query("SELECT n FROM Notificacao n WHERE n.lida = false ORDER BY n.dataEnvio DESC")
  Page<Notificacao> findLidaFalseOrderByDataEnvioDesc(Pageable pageable);

  @Modifying
  @Query("update Notificacao n set n.lida = true, n.lidaPor = :user where n.denuncia.id = :denunciaId and n.lida = false")
  int marcarComoLidaPorDenuncia(@Param("denunciaId") Long denunciaId, @Param("user") String user);

}
