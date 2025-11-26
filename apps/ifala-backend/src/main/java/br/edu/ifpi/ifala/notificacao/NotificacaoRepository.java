package br.edu.ifpi.ifala.notificacao;

import java.util.List;
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
   * Busca as 10 notificações não lidas mais antigas, ordenadas por data de envio. Limita o
   * resultado para evitar problemas de performance e visualização.
   * 
   * @param pageable Paginação com limite de 10 registros
   * @return Lista com no máximo 10 notificações não lidas ordenadas da mais antiga para a mais
   *         recente
   */
  @Query("SELECT n FROM Notificacao n WHERE n.lida = false ORDER BY n.dataEnvio ASC")
  List<Notificacao> findLidaFalseOrderByDataEnvioAsc(Pageable pageable);

  @Modifying
  @Query("update Notificacao n set n.lida = true, n.lidaPor = :user where n.denuncia.id = :denunciaId and n.lida = false")
  int marcarComoLidaPorDenuncia(@Param("denunciaId") Long denunciaId, @Param("user") String user);

}
