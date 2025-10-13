package br.edu.ifpi.ifala;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

/**
 * Classe principal da aplicação Ifala. Responsável por iniciar o contexto Spring Boot.
 *
 * @author Renê Morais
 */
@SpringBootApplication
public class IfalaApplication {

  /**
   * Ponto de entrada da aplicação IFala.
   *
   * @param args Argumentos de linha de comando (não utilizados).
   */
  public static void main(String[] args) {
    SpringApplication.run(IfalaApplication.class, args);
  }

  /**
   * Bean para permitir injeção de RestTemplate nos serviços.
   */
  @Bean
  public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(30000);
    factory.setReadTimeout(30000);

    RestTemplate restTemplate = new RestTemplate(factory);
    return restTemplate;
  }
}
