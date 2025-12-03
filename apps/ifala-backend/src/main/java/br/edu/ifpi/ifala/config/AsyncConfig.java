package br.edu.ifpi.ifala.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuração que habilita o processamento assíncrono (@EnableAsync) e define o Thread Pool a ser
 * usado pelos métodos @Async. * É altamente recomendável configurar o Executor para controlar o
 * número de threads e evitar o esgotamento de recursos.
 * 
 * @author Phaola
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  /**
   * Define um ThreadPoolTaskExecutor customizado para as tarefas assíncronas. Este Executor será
   * usado por padrão pelos métodos anotados com @Async.
   */
  @Bean(name = "notificationTaskExecutor")
  public Executor notificationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // Número de threads que serão mantidas ativas, mesmo que ociosas
    executor.setCorePoolSize(5);
    // Número máximo de threads que podem ser criadas no pool
    executor.setMaxPoolSize(10);
    // Capacidade da fila para armazenar tarefas antes de criar novas threads
    executor.setQueueCapacity(25);
    // Prefixo para o nome das threads (útil para logs)
    executor.setThreadNamePrefix("Notification-Async-");
    executor.initialize();
    return executor;
  }
}
