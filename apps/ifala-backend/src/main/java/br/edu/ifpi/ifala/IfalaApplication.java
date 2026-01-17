package br.edu.ifpi.ifala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;


/**
 * Classe principal da aplicação Ifala. Responsável por iniciar o contexto Spring Boot.
 *
 * @author Renê Morais
 */
@SpringBootApplication
@EnableRetry
@EnableCaching
public class IfalaApplication {

  /**
   * Ponto de entrada da aplicação IFala.
   *
   * @param args Argumentos de linha de comando (não utilizados).
   */
  public static void main(String[] args) {
    SpringApplication.run(IfalaApplication.class, args);
  }

  @jakarta.annotation.PostConstruct
  public void init() {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Sao_Paulo"));
  }
}
