package br.edu.ifpi.ifala.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

/**
 * Repositório para a entidade Usuario.
 * 
 * @author Phaola
 */


public interface UsuarioRepository
    extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
  Optional<Usuario> findByEmail(String email);

  Optional<Usuario> findByUsername(String username);

  Optional<Usuario> findByPasswordResetToken(String passwordResetToken);

  @Query("SELECT u.email FROM Usuario u WHERE u.email IS NOT NULL AND u.email <> ''")
  List<String> findAllEmailsExcludingBlanks();
}


