package br.edu.ifpi.ifala.security.recaptcha;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO.RecaptchaResponseDto;

/**
 * Serviço para validação do reCAPTCHA do Google. Esta classe utiliza o RestClient para enviar
 * requisições ao serviço reCAPTCHA e validar tokens.
 *
 * @author Jhonatas G Ribeiro
 */
@Service
public class RecaptchaService {

  private final RestClient restClient;
  private final RecaptchaConfig recaptchaConfig;
  private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);

  public RecaptchaService(RecaptchaConfig recaptchaConfig) {
    this.restClient = RestClient.create();
    this.recaptchaConfig = recaptchaConfig;
  }

  public Boolean validarToken(String token, String actionEsperada, double scoreMinimo) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("secret", recaptchaConfig.getSecret());
    formData.add("response", token);

    try {
      RecaptchaResponseDto dto = restClient.post().uri(recaptchaConfig.getUrl()).body(formData)
          .retrieve().body(RecaptchaResponseDto.class);

      if (dto == null) {
        log.warn("Falha na validação do reCAPTCHA: resposta nula.");
        return false;
      }

      boolean isSuccess = dto.isSuccess() && dto.getAction() != null
          && dto.getAction().equalsIgnoreCase(actionEsperada) && dto.getScore() >= scoreMinimo;
      if (isSuccess) {
        log.info("reCAPTCHA validado com sucesso: ação '{}' com score {}.", dto.getAction(),
            dto.getScore());
      } else {
        log.warn(
            "Falha na validação do reCAPTCHA: ação esperada '{}', ação recebida '{}', score recebido {}.",
            actionEsperada, dto.getAction(), dto.getScore());
      }
      return isSuccess;

    } catch (Exception e) {
      log.error("Erro ao validar o reCAPTCHA: {}", e.getMessage());
      return false;
    }
  }
}
