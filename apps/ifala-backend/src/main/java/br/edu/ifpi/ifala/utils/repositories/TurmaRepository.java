package br.edu.ifpi.ifala.utils.repositories;

import br.edu.ifpi.ifala.utils.entities.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade TurmaEntity.
 * 
 * @author luisthedevmagician
 */
@Repository
public interface TurmaRepository extends JpaRepository<TurmaEntity, Long> {
}
