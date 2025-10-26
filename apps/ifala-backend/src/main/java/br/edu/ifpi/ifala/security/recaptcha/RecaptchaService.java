package br.edu.ifpi.ifala.security.recaptcha;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO.RecaptchaResponseDto;

/**
 * Serviço para validação do reCAPTCHA do Google. Esta classe utiliza o
 * RestClient para enviar
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

  public Boolean validarToken(String token, String actionEsperada, double scoreMinimo) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("secret", recaptchaConfig.getSecret());
    formData.add("response", token);

    try {
      RecaptchaResponseDto dto = restClient.post()
          .uri(recaptchaConfig.getUrl())
          .body(formData)
          .retrieve()
          .body(RecaptchaResponseDto.class);

      if (dto == null) {
        return false;
      }
      return dto.isSuccess() && dto.getAction() != null && dto.getAction().equalsIgnoreCase(actionEsperada)
          && dto.getScore() >= scoreMinimo;

    } catch (Exception e) {
      return false;
    }
  }
}
