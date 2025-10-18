package br.edu.ifpi.ifala.security.recaptcha;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO.RecaptchaResponseDto;
import reactor.core.publisher.Mono;

/**
 * Serviço para validação do reCAPTCHA do Google. Esta classe utiliza o WebClient para enviar
 * requisições ao serviço reCAPTCHA e validar tokens.
 *
 * @author Jhonatas G Ribeiro
 */
@Service
public class RecaptchaService {

  private final WebClient webClient;
  private final RecaptchaConfig recaptchaConfig;

  /**
   * É preciso criar um Bean para o WebClient se for usá-lo desta forma (no construtor), através de
   * uma classe @Component ou @Configuration. Irei desabilitar, pois está causando erro. Recomendo
   * utilizar RestClient.
   */
  public RecaptchaService(
      /* WebClient.Builder webClientBuilder, */RecaptchaConfig recaptchaConfig) {
    this.webClient = WebClient.builder().baseUrl(recaptchaConfig.getUrl()).build();
    this.recaptchaConfig = recaptchaConfig;
  }

  public Mono<Boolean> validarToken(String token) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("secret", recaptchaConfig.getSecret());
    formData.add("response", token);

    return this.webClient.post().bodyValue(formData).retrieve()
        .bodyToMono(RecaptchaResponseDto.class).map(RecaptchaResponseDto::isSuccess)
        .onErrorReturn(false); // Erro de comunicação retorna falso
  }
}
