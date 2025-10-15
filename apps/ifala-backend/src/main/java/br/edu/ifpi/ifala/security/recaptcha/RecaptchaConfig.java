package br.edu.ifpi.ifala.security.recaptcha;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurações do reCAPTCHA do Google.
 * Esta classe carrega as propriedades de configuração do reCAPTCHA a partir do
 * arquivo application.properties.
 * Só deve ser configurado em produção.
 *
 * @author Jhonatas G Ribeiro
 */
@Component
@ConfigurationProperties(prefix = "recaptcha")
public class RecaptchaConfig {

  private String secret; // Chave secreta do reCAPTCHA
  private String url; // URL do reCAPTCHA

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}