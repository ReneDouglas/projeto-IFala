package br.edu.ifpi.ifala.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca um RefreshToken pelo seu valor de token (a string aleatória).
   * 
   * @param token a string do refresh token.
   * @return um Optional contendo o RefreshToken, se encontrado.
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Deleta todos os RefreshTokens associados a um usuário específico. Esta operação é executada em
   * uma transação.
   * 
   * @param usuario o objeto Usuario cujos tokens devem ser deletados.
   */
  @Transactional
  void deleteByUsuario(Usuario usuario);
}
