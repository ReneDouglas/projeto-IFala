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
// @RequiredArgsConstructor removido
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


    return http.build();
  }

}
