package br.edu.ifpi.ifala.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Rotas públicas - não requerem autenticação ou JWT. O Controller fará a
            // validação do Refresh Token.
            .requestMatchers("/api/v1/auth/login", "/actuator/**", "/api/v1/auth/redefinir-senha",
                "/api/v1/public/**", "/api/v1/auth/refresh", "/swagger-ui/**", "/v3/api-docs/**",
                "/swagger-ui.html", "/webjars/**", "/swagger-resources/**")
            .permitAll()
            // Rotas protegidas - requerem autenticação com token válido
            .requestMatchers("/api/v1/auth/sair", "/api/v1/auth/admin/registrar-usuario",
                "/api/v1/utils/**")
            .authenticated()
            // Todas as outras rotas requerem autenticação
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
      UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
    return new JwtAuthenticationFilter(jwtUtil, userDetailsService, tokenBlacklistService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}
