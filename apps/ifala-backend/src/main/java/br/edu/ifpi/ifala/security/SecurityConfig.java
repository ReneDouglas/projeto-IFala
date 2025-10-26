package br.edu.ifpi.ifala.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtConverter jwtConverter;

  public SecurityConfig(JwtConverter jwtConverter) {
    this.jwtConverter = jwtConverter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
        // endpoints públicos permitidos para todos
        .requestMatchers("/api/v1/public/**", "/actuator/**", "/auth/primeiro-acesso",
            "/auth/login", "/auth/logout", "/auth/recuperar-senha", "/v3/api-docs/**",
            "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**",
            "/webjars/**", "/api/v1/utils/**")
        .permitAll()

        // endpoints de admin restritos à ROLE "ADMIN"
        // .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

        // TEMPORARIAMENTE PERMITIDO PARA TESTES DA TASK 62
        // qualquer outra requisição diferente exige autenticação
        .anyRequest().permitAll())

        // TEMPORARIAMENTE DESABILITADO PARA TESTES DA TASK 62
        // .oauth2ResourceServer(
        // oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
