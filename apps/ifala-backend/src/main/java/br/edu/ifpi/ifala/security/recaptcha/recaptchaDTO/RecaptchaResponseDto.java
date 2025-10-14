package br.edu.ifpi.ifala.security.recaptcha.recaptchaDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa a resposta do serviço reCAPTCHA do Google.
 * Esta classe é usada para mapear a resposta JSON retornada pela API do
 * reCAPTCHA.
 *
 * @author Jhonatas G Ribeiro
 */
public class RecaptchaResponseDto {

  private boolean success;
  @JsonProperty("error-codes") // mapeia o campo JSON "error-codes" para a variável errorCodes

  private String[] errorCodes;

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
}