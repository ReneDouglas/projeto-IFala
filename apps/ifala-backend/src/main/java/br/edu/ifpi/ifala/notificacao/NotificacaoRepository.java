package br.edu.ifpi.ifala.notificacao;

import java.util.List;
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

  List<Notificacao> findByLidaFalse();

  List<Notificacao> findByLida(Boolean lida);

  @Modifying
  @Query("update Notificacao n set n.lida = true, n.lidaPor = :user where n.denuncia.id = :denunciaId and n.lida = false")
  int marcarComoLidaPorDenuncia(@Param("denunciaId") Long denunciaId, @Param("user") String user);

}
