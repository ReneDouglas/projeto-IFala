package br.edu.ifpi.ifala.denunciaFixada;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositório para gerenciar denúncias fixadas por usuários.
 *
 * @author Guilherme Alves
 */
@Repository
public interface DenunciaFixadaRepository extends JpaRepository<DenunciaFixada, Long> {

  /**
   * Busca uma denúncia fixada específica por usuário e denúncia.
   *
   * @param usuarioId  ID do usuário
   * @param denunciaId ID da denúncia
   * @return Optional contendo a DenunciaFixada se encontrada
   */
  Optional<DenunciaFixada> findByUsuarioIdAndDenunciaId(Long usuarioId, Long denunciaId);

  /**
   * Busca todos os IDs de denúncias fixadas por um usuário específico.
   *
   * @param usuarioId ID do usuário
   * @return Lista de IDs de denúncias fixadas
   */
  @Query("SELECT df.denuncia.id FROM DenunciaFixada df WHERE df.usuario.id = :usuarioId")
  List<Long> findDenunciaIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

  /**
   * Remove uma denúncia fixada específica.
   *
   * @param usuarioId  ID do usuário
   * @param denunciaId ID da denúncia
   */
  void deleteByUsuarioIdAndDenunciaId(Long usuarioId, Long denunciaId);

  /**
   * Verifica se uma denúncia está fixada por um usuário.
   *
   * @param usuarioId  ID do usuário
   * @param denunciaId ID da denúncia
   * @return true se a denúncia está fixada, false caso contrário
   */
  boolean existsByUsuarioIdAndDenunciaId(Long usuarioId, Long denunciaId);
}
