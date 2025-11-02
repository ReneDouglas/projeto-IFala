package br.edu.ifpi.ifala.security;

import br.edu.ifpi.ifala.autenticacao.dto.TokenDataDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitário para criação e validação de tokens JWT.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

@Component
public class JwtUtil {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expirationSeconds;

  private Key signingKey;

  @PostConstruct
  public void init() {
    this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public TokenDataDTO generateToken(String username) {
    Date issuedAt = new Date();

    Date expiration = new Date(System.currentTimeMillis() + (expirationSeconds * 1000));
    String token = Jwts.builder().setSubject(username).setIssuedAt(issuedAt)
        .setExpiration(expiration).signWith(signingKey, SignatureAlgorithm.HS256).compact();
    logger.info("Gerando JWT para {}: issuedAt={}, expiration={}", username, issuedAt, expiration);
    return new TokenDataDTO(token, issuedAt.toInstant(), expiration.toInstant());
  }

  public Claims extractClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
  }

  public boolean validateToken(String token, String username) {
    return (username.equals(extractUsername(token)) && !isTokenExpired(token));
  }

  /**
   * Valida se o token pode ser usado para refresh. Permite tokens expirados para que possam ser
   * renovados. * @param token o token JWT
   * 
   * @return true se o token é válido (mesmo que expirado)
   */
  public boolean canRefreshToken(String token) {
    try {
      // Tenta extrair. Se passar, é válido e não expirou (mas este método é
      // tipicamente usado para Refresh Token, que pode estar expirado).
      extractClaims(token);
      return true;
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      // Token expirado mas válido para refresh (neste caso, é um Refresh Token)
      return true;
    } catch (Exception e) {
      // Token inválido (assinatura incorreta, malformado, etc.)
      return false;
    }
  }

  /**
   * Extrai o username de um token mesmo que esteja expirado. Útil para o processo de refresh token.
   * * @param token o token JWT
   * 
   * @return o username do token
   */
  public String extractUsernameFromExpiredToken(String token) {
    try {
      return extractUsername(token);
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      // No caso de expiração, a exceção contém os claims (o corpo do token)
      return e.getClaims().getSubject();
    }
  }
}
