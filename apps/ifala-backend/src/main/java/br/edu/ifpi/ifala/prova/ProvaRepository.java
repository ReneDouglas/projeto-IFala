package br.edu.ifpi.ifala.prova;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository para operações de banco de dados relacionadas a Provas.
 *
 * @author Guilherme Alves
 */
@Repository
public interface ProvaRepository extends JpaRepository<Prova, Long> {

  /**
   * Busca todas as provas associadas a uma denunciaa .
   *
   * @param denunciaId ID da denuncia
   * @return Lista de provas da denuncia
   */
  List<Prova> findByDenunciaId(Long denunciaId);
}
