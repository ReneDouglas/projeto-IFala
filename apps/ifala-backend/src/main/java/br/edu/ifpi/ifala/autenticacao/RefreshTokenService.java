package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.shared.exceptions.RefreshTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
  private final RefreshTokenRepository refreshTokenRepository;
  private final UsuarioRepository usuarioRepository;

  @Value("${jwt.refresh-expiration-seconds}")
  private Long refreshTokenDurationSeconds;

  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
      UsuarioRepository usuarioRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.usuarioRepository = usuarioRepository;
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  // Cria e salva um novo Refresh Token no banco
  public RefreshToken createRefreshToken(String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Garante que o usuário tenha apenas um refresh token por vez
    refreshTokenRepository.deleteByUsuario(usuario);

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUsuario(usuario);

    Instant now = Instant.now();
    Instant expiry = now.plusSeconds(refreshTokenDurationSeconds);
    logger.debug("Creating refresh token for {}: now={}, refreshTokenDurationSeconds={}, expiry={}",
        email, now, refreshTokenDurationSeconds, expiry);
    refreshToken.setDataExpiracao(expiry);
    refreshToken.setToken(UUID.randomUUID().toString());

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshToken createRefreshToken(String email, Instant explicitExpiry) {
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // Garante que o usuário tenha apenas um refresh token por vez
    refreshTokenRepository.deleteByUsuario(usuario);

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUsuario(usuario);
    refreshToken.setDataExpiracao(explicitExpiry);
    refreshToken.setToken(UUID.randomUUID().toString());

    logger.debug("Creating refresh token for {} with explicit expiry={}", email, explicitExpiry);
    return refreshTokenRepository.save(refreshToken);
  }

  // Verifica se o token expirou
  public RefreshToken verifyExpiration(RefreshToken token) {
    Instant now = Instant.now();
    Instant expiry = token.getDataExpiracao();
    logger.debug("Verifying refresh token expiration: tokenExpiry={}, now={}", expiry, now);
    // Considera expirado se expiry <= now (mais explícito que compareTo < 0)
    if (!expiry.isAfter(now)) {

      refreshTokenRepository.delete(token);
      throw new RefreshTokenException("Refresh token expirado. Por favor, faça login novamente.",
          401);
    }
    return token;
  }

  // Deleta o token (usado no logout)
  @Transactional
  public void deleteByToken(String token) {
    int deleted = refreshTokenRepository.deleteByToken(token);
    logger.debug("deleteByToken({}) returned {}", token, deleted);
    if (deleted == 0) {
      throw new RefreshTokenException(
          "Refresh token inválido ou já utilizado. Faça login novamente.", 401);
    }
  }
}
