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

  /**
   * Construtor do serviço de validação do reCAPTCHA.
   *
   * @param recaptchaConfig configuração contendo a URL e a chave secreta do reCAPTCHA
   */
  public RecaptchaService(RecaptchaConfig recaptchaConfig) {
    this.restClient = RestClient.create();
    this.recaptchaConfig = recaptchaConfig;
  }

  /**
   * Valida o token do reCAPTCHA verificando a ação esperada e o score mínimo.
   *
   * @param token o token do reCAPTCHA recebido do cliente
   * @param actionEsperada a ação esperada que deve corresponder à ação do token
   * @param scoreMinimo o score mínimo aceitável (0.0 a 1.0)
   * @return true se o token for válido, a ação corresponder e o score for maior ou igual ao mínimo;
   *         false caso contrário
   */
  public Boolean validarToken(String token, String actionEsperada, double scoreMinimo) {
    log.info("🔍 Iniciando validação do reCAPTCHA...");
    log.debug("Token recebido (primeiros 50 chars): {}",
        token != null ? token.substring(0, Math.min(50, token.length())) : "null");
    log.debug("Action esperada: '{}', Score mínimo: {}", actionEsperada, scoreMinimo);

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("secret", recaptchaConfig.getSecret());
    formData.add("response", token);

    try {
      log.debug("📤 Enviando requisição para: {}", recaptchaConfig.getUrl());
      RecaptchaResponseDto dto = restClient.post().uri(recaptchaConfig.getUrl()).body(formData)
          .retrieve().body(RecaptchaResponseDto.class);

      if (dto == null) {
        log.warn("⚠️ Falha na validação do reCAPTCHA: resposta nula.");
        return false;
      }

      // Log detalhado da resposta do Google
      log.info("📥 Resposta do Google reCAPTCHA:");
      log.info("   - success: {}", dto.isSuccess());
      log.info("   - action: '{}'", dto.getAction());
      log.info("   - score: {}", dto.getScore());
      log.info("   - errorCodes: {}",
          dto.getErrorCodes() != null ? String.join(", ", dto.getErrorCodes()) : "nenhum");

      // Verificação específica para detectar uso de chaves v2 com código v3
      if (dto.isSuccess() && dto.getAction() == null && dto.getScore() == 0.0) {
        log.error("❌ ERRO CRÍTICO: Resposta indica chave reCAPTCHA v2 sendo usada com código v3!");
        log.error("   Action e Score são null/0.0. Verifique:");
        log.error("   1. Se a chave no Google Console está realmente configurada como v3");
        log.error("   2. Se você está usando a Site Key correta (não a Secret Key)");
        log.error("   3. Se o domínio está autorizado no Google Console");
        return false;
      }

      boolean isSuccess = dto.isSuccess() && dto.getAction() != null
          && dto.getAction().equalsIgnoreCase(actionEsperada) && dto.getScore() >= scoreMinimo;
      if (isSuccess) {
        log.info("✅ reCAPTCHA validado com sucesso: ação '{}' com score {}.", dto.getAction(),
            dto.getScore());
      } else {
        log.warn("⚠️ Falha na validação do reCAPTCHA:");
        log.warn("   - Ação esperada: '{}', recebida: '{}'", actionEsperada, dto.getAction());
        log.warn("   - Score esperado: >= {}, recebido: {}", scoreMinimo, dto.getScore());
        log.warn("   - Success: {}", dto.isSuccess());
      }
      return isSuccess;

    } catch (Exception e) {
      log.error("❌ Erro ao validar o reCAPTCHA: {}", e.getMessage());
      return false;
    }
  }
}
