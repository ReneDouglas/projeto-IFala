package br.edu.ifpi.ifala.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail(String email);
}
