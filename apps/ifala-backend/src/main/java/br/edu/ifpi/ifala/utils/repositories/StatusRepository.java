package br.edu.ifpi.ifala.utils.repositories;

import br.edu.ifpi.ifala.utils.entities.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade StatusEntity.
 * 
 * @author luisthedevmagician
 */

@Repository
public interface StatusRepository extends JpaRepository<StatusEntity, Long> {
}
