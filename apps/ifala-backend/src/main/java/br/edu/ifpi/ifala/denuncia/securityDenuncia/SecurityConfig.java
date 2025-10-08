package br.edu.ifpi.ifala.denuncia.securityDenuncia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // Desabilitar CSRF para APIs stateless(Rest)
        .authorizeHttpRequests(authz -> authz
            // Endpoints públicos
            .requestMatchers(HttpMethod.POST, "/api/v1/denuncias").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/denuncias/acompanhar/**").permitAll()

            // Endpoints protegidos (ex: requerem role 'ADMIN')
            .requestMatchers(HttpMethod.GET, "/api/v1/denuncias").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/v1/denuncias/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/denuncias/**").hasRole("ADMIN")

            // Qualquer outra requisição precisa de autenticação
            .anyRequest().authenticated());
    return http.build();
  }
}
