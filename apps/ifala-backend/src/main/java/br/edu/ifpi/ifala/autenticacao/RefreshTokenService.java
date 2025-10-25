package br.edu.ifpi.ifala.autenticacao;

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
  @Transactional
  public RefreshToken createRefreshToken(String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUsuario(usuario);
    refreshToken.setDataExpiracao(Instant.now().plusSeconds(refreshTokenDurationSeconds));
    refreshToken.setToken(UUID.randomUUID().toString());

    RefreshToken saved = refreshTokenRepository.save(refreshToken);
    logger.info("Refresh token salvo no BD para usuário {}: id={}, token={}", email, saved.getId(),
        saved.getToken());
    return saved;
  }

  // Verifica se o token expirou
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getDataExpiracao().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new RuntimeException("Refresh token expirado. Por favor, faça login novamente.");
    }
    return token;
  }

  // Deleta o token (usado no logout)
  @Transactional
  public void deleteByToken(String token) {
    refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
  }
}
