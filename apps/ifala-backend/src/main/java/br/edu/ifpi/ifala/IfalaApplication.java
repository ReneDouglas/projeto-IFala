package br.edu.ifpi.ifala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Classe principal da aplicação Ifala. Responsável por iniciar o contexto Spring Boot.
 *
 * @author Renê Morais
 */
@SpringBootApplication
@EnableAsync
public class IfalaApplication {

  /**
   * Ponto de entrada da aplicação IFala.
   *
   * @param args Argumentos de linha de comando (não utilizados).
   */
  public static void main(String[] args) {
    SpringApplication.run(IfalaApplication.class, args);
  }
}
