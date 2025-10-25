package br.edu.ifpi.ifala.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;
  private final TokenBlacklistService tokenBlacklistService;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
      TokenBlacklistService tokenBlacklistService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.tokenBlacklistService = tokenBlacklistService;
  }

  // Lista de endpoints que não exigem Access Token (Bearer) e, portanto, pulam a
  // lógica do filtro.
  private boolean shouldSkipFilter(String path) {
    return path != null && (path.equals("/api/v1/auth/login") ||
        path.equals("/api/v1/auth/refresh") ||
        path.equals("/api/v1/auth/redefinir-senha") ||
        path.startsWith("/swagger-ui/") ||
        path.startsWith("/v3/api-docs/"));
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();

    // --- 1. Pula o filtro para rotas públicas (incluindo /refresh) ---
    if (shouldSkipFilter(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    final String token = extractToken(request);
    // Log curto para depuração: não logar o token inteiro por segurança
    logger.debug("Header Authorization presente: {}", token != null ? "sim" : "não");

    if (token == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Access Token não encontrado.\"}");
      return;
    }

    if (tokenBlacklistService.isBlacklisted(token)) {
      logger.warn("Token na blacklist! Requisição bloqueada.");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Token inválido ou revogado.\"}");
      return;
    }

    try {
      String username = jwtUtil.extractUsername(token);

      // Loga a expiração do token para debugging
      try {
        java.util.Date exp = jwtUtil.extractClaims(token).getExpiration();
        logger.debug("Token expira em: {} (now={})", exp, new java.util.Date());
      } catch (Exception e) {
        logger.debug("Não foi possível extrair claims para logging de expiração: {}", e.getMessage());
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        boolean valido = jwtUtil.validateToken(token, userDetails.getUsername());

        if (valido) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
              null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
          logger.info("Usuário autenticado: {}", username);
        } else {
          logger.warn("Access Token inválido para usuário: {}. Requisição bloqueada.", username);
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write("{\"error\": \"Access Token inválido.\"}");
          return;
        }
      }
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      logger.warn("Access Token expirado detectado para recurso protegido. Requisição bloqueada.");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Access Token expirado.\"}");
      return;
    } catch (Exception e) {
      logger.error("Erro ao processar Access Token JWT: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Access Token inválido ou malformado.\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extrai o token "Bearer " do cabeçalho de Autorização.
   */
  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}