package br.edu.ifpi.ifala.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório para a entidade Usuario.
 * 
 * @author Phaola
 */


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail(String email);

  Optional<Usuario> findByUsername(String username);

  Optional<Usuario> findByPasswordResetToken(String passwordResetToken);
}


