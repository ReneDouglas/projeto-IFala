package br.edu.ifpi.ifala.autenticacao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
  @Value("${jwt.refresh-expiration-seconds}")
  private Long refreshTokenDurationSeconds;

  // Permite controlar se o cookie deve ser marcado como 'Secure' (recomendado em
  // produção)
  @Value("${app.cookie.secure:false}")
  private boolean secureCookie;

  // Caminho onde o cookie de refresh será válido (configurável)
  @Value("${app.cookie.path:/}")
  private String refreshCookiePath;

  @Value("${app.cookie.same-site:Lax}")
  private String sameSite;

  /**
   * Cria o cookie HttpOnly para o Refresh Token.
   */
  public ResponseCookie createRefreshTokenCookie(String token) {
    // Em ambiente de desenvolvimento HTTP, secure=false para que o cookie funcione
    // em localhost.
    return ResponseCookie.from("refreshToken", token).httpOnly(true).secure(secureCookie)
        .sameSite(sameSite).path(refreshCookiePath) // Path e SameSite configuráveis
        .maxAge(refreshTokenDurationSeconds) // maxAge é em segundos
        .build();
  }

  /**
   * Cria um cookie "vazio" que expira imediatamente para fazer o logout.
   */
  public ResponseCookie createLogoutCookie() {
    return ResponseCookie.from("refreshToken", "").httpOnly(true).secure(secureCookie)
        .sameSite(sameSite).path(refreshCookiePath).maxAge(0) // Expira imediatamente
        .build();
  }
}
