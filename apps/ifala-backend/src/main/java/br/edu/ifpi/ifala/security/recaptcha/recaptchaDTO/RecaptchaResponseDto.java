package br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa a resposta do serviço reCAPTCHA do Google. Esta classe é usada
 * para mapear a resposta
 * JSON retornada pela API do reCAPTCHA.
 *
 * @author Jhonatas G Ribeiro
 */

@Schema(name = "Resposta do ReCaptcha", description = "Representa a resposta da validação do token ReCaptcha junto ao serviço do Google.")
public class RecaptchaResponseDto {

  @Schema(description = "Indica se a validação do token ReCaptcha foi bem-sucedida.", example = "true")
  private boolean success;

  @Schema(description = "Lista de códigos de erro retornados pelo Google em caso de falha na validação.", example = "[\"invalid-input-response\", \"timeout-or-duplicate\"]")
  @JsonProperty("error-codes") // mapeia o campo JSON "error-codes" para a variável errorCodes
  private String[] errorCodes;

  @Schema(description = "A pontuação para esta requisição (0.0 - 1.0).", example = "0.9")
  private double score;

  @Schema(description = "A ação associada a esta requisição.", example = "denuncia")
  private String action;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String[] getErrorCodes() {
    return errorCodes;
  }

  public void setErrorCodes(String[] errorCodes) {
    this.errorCodes = errorCodes;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
