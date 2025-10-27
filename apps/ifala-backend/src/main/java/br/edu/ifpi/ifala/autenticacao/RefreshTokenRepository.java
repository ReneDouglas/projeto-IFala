package br.edu.ifpi.ifala.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca um RefreshToken pelo seu valor de token (a string aleatória).
   * 
   * @param token a string do refresh token.
   * @return um Optional contendo o RefreshToken, se encontrado.
   */

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<RefreshToken> findByToken(String token);

  /**
   * Deleta todos os RefreshTokens associados a um usuário específico. Esta operação é executada em
   * uma transação.
   * 
   * @param usuario o objeto Usuario cujos tokens devem ser deletados.
   */
  @Modifying
  @Transactional
  void deleteByUsuario(Usuario usuario);

  /**
   * Remove diretamente o refresh token pelo valor da string. Usado para garantir que a operação
   * DELETE seja executada imediatamente no banco (evita depender do estado do EntityManager para
   * delete(entity)).
   */
  @Modifying
  @Transactional
  @Query("delete from RefreshToken r where r.token = :token")
  int deleteByToken(String token);

  /**
   * Atualiza o valor do token e a data de expiração de um refresh token existente. Retorna o número
   * de linhas afetadas (deve ser 1 se a rotação ocorreu).
   */
  @Modifying
  @Transactional
  @Query("update RefreshToken r set r.token = :newToken, r.dataExpiracao = :dataExpiracao where r.token = :oldToken")
  int rotateToken(String oldToken, String newToken, java.time.Instant dataExpiracao);
}
