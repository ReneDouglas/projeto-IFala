package br.edu.ifpi.ifala.utils.repositories;

import br.edu.ifpi.ifala.utils.entities.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade CursoEntity.
 * 
 * @author luisthedevmagician
 */

@Repository
public interface CursoRepository extends JpaRepository<CursoEntity, Long> {
}
