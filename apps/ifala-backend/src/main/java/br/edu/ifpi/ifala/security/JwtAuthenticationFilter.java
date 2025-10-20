package br.edu.ifpi.ifala.security; // DEVE SER A PRIMEIRA LINHA DE CÓDIGO!

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractToken(request);
    if (token != null) {
      logger.info("Token recebido: {}", token);

      // Verifica se o token está na blacklist
      if (tokenBlacklistService.isBlacklisted(token)) {
        logger.warn("Token está na blacklist! Requisição bloqueada.");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Token inválido ou revogado.\"}");
        return; // Bloqueia a requisição
      }

      try {
        String username = jwtUtil.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          boolean valido = jwtUtil.validateToken(token, userDetails.getUsername());

          logger.info("Token para usuário {} é válido? {}", username, valido);

          if (valido) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Usuário autenticado: {}", username);
          } else {
            logger.warn("Token inválido ou expirado para usuário: {}. Requisição bloqueada.",
                username);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token inválido ou expirado.\"}");
            return; // Bloqueia a requisição
          }
        }
      } catch (io.jsonwebtoken.ExpiredJwtException e) {
        logger.warn("Token expirado detectado. Requisição bloqueada.");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Token expirado.\"}");
        return; // Bloqueia a requisição
      } catch (Exception e) {
        logger.error("Erro ao processar token JWT: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Token inválido.\"}");
        return; // Bloqueia a requisição
      }
    }
    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
