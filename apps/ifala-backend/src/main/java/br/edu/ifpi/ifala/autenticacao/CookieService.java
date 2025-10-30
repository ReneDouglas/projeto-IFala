package br.edu.ifpi.ifala.autenticacao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Serviço para manipulação de cookies relacionados à autenticação.
 * 
 * @author Phaola
 */

@Service
public class CookieService {

  @Value("${jwt.refresh-expiration-seconds}")
  private Long refreshTokenDurationSeconds;

  private final String REFRESH_COOKIE_PATH = "/api/v1/auth";

  /**
   * Cria o cookie HttpOnly para o Refresh Token.
   */
  public ResponseCookie createRefreshTokenCookie(String token) {
    return ResponseCookie.from("refreshToken", token).httpOnly(true).secure(true).sameSite("Strict")
        .path(REFRESH_COOKIE_PATH).maxAge(refreshTokenDurationSeconds).build();
  }

  /**
   * Cria um cookie "vazio" que expira imediatamente para fazer o logout.
   */
  public ResponseCookie createLogoutCookie() {
    return ResponseCookie.from("refreshToken", "").httpOnly(true).secure(true).sameSite("Strict")
        .path(REFRESH_COOKIE_PATH).maxAge(0) // Expira imediatamente
        .build();
  }
}
