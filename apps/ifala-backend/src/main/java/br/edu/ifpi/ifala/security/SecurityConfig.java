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
  /**
   * Define as configurações de segurança, incluindo regras de acesso e filtros. O Spring injetará o
   * JwtAuthenticationFilter (criado no método @Bean abaixo) diretamente como um parâmetro. * @param
   * http O objeto HttpSecurity para configurar.
   * 
   * @param jwtAuthenticationFilter O filtro JWT injetado pelo Spring.
   * @return O SecurityFilterChain configurado.
   * @throws Exception Se houver erro na configuração.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/login", "/api/v1/auth/redefinir-senha",
                "/api/v1/auth/sair")
            .permitAll().requestMatchers("/api/v1/auth/admin/registrar-usuario").authenticated()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Bean que define como o JwtAuthenticationFilter deve ser criado. O Spring o injetará nos métodos
   * que o requerem (como o securityFilterChain). * @param jwtUtil O utilitário para manipulação de
   * tokens.
   * 
   * @param userDetailsService O serviço para carregar detalhes do usuário.
   * @return Uma nova instância de JwtAuthenticationFilter.
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
      UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
    return new JwtAuthenticationFilter(jwtUtil, userDetailsService, tokenBlacklistService);
  }

  /**
   * Bean para injetar o PasswordEncoder (BCrypt) no AuthController. * @return Uma instância de
   * PasswordEncoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Expõe o AuthenticationManager para uso em outros serviços (como o login). * @param
   * configuration Configuração de autenticação do Spring.
   * 
   * @return O AuthenticationManager.
   * @throws Exception Se houver erro.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}
