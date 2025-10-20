package br.edu.ifpi.ifala.utils.repositories;

import br.edu.ifpi.ifala.utils.entities.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade CategoriaEntity.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}
