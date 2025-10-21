package br.edu.ifpi.ifala.utils.repositories;

import br.edu.ifpi.ifala.utils.entities.GrauEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade GrauEntity.
 * 
 * @author luisthedevmagician
 */


@Repository
public interface GrauRepository extends JpaRepository<GrauEntity, Long> {
}
