package br.edu.ifpi.ifala.security.recaptcha;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO.RecaptchaResponseDto;

/**
 * Serviço para validação do reCAPTCHA do Google. Esta classe utiliza o WebClient para enviar
 * requisições ao serviço reCAPTCHA e validar tokens.
 *
 * @author Jhonatas G Ribeiro
 */
@Service
public class RecaptchaService {

  private final RestClient restClient;
  private final RecaptchaConfig recaptchaConfig;

  public RecaptchaService(RecaptchaConfig recaptchaConfig) {
    this.restClient = RestClient.create();
    this.recaptchaConfig = recaptchaConfig;
  }

  public Boolean validarToken(String token) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("secret", recaptchaConfig.getSecret());
    formData.add("response", token);

    return false; // A SER IMPLEMENTADO DEPOIS QUE O RECAPTCHA ESTIVER FUNCIONANDO EM PRODUÇÃO
  }
}
