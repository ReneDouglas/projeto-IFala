
// Classe Java apenas para gerar hash BCrypt

package br.edu.ifpi.ifala.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
  // Declaração do Logger
  private static final Logger log = LoggerFactory.getLogger(PasswordHasher.class);

  public static void main(String[] args) {
    // Crie uma instância do encoder, exatamente como no seu SecurityConfig
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    String senhaCrua = "12345";

    // Gere a hash
    String senhaHash = encoder.encode(senhaCrua);

    log.info("Senha Crua: {}", senhaCrua);
    log.info("Hash BCrypt: {}", senhaHash);

    // Exemplo de teste (deve ser 'true')
    boolean isMatch = encoder.matches(senhaCrua, senhaHash);
    log.info("Teste de Match: {}", isMatch);
  }
}
