package br.edu.ifpi.ifala.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração de cache da aplicação usando Caffeine como provider. Define políticas de expiração e
 * tamanho máximo para os caches.
 * 
 * @author Renê Morais
 */
@Configuration
@EnableCaching
public class CacheConfig {

  @Value("${cache.user-details.expiration-minutes:5}")
  private int cacheExpirationMinutes;

  @Value("${cache.user-details.maximum-size:100}")
  private int cacheMaximumSize;

  /**
   * Configura o gerenciador de cache com Caffeine. Cache "userDetailsCache": - Expira após X
   * minutos sem acesso (configurável) - Máximo de Y entradas (configurável) - Otimizado para
   * reduzir consultas ao banco durante autenticação
   *
   * @return CacheManager configurado
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("userDetailsCache");
    cacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(cacheExpirationMinutes, TimeUnit.MINUTES)
            .maximumSize(cacheMaximumSize).recordStats());

    return cacheManager;
  }
}
