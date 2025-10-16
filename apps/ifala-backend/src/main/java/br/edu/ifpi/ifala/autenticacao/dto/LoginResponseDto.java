package br.edu.ifpi.ifala.autenticacao.dto;

public class LoginResponseDto {
  private String token;
  private boolean passwordChangeRequired;
  private String redirect;

  public LoginResponseDto() {}

  public LoginResponseDto(String token, boolean passwordChangeRequired, String redirect) {
    this.token = token;
    this.passwordChangeRequired = passwordChangeRequired;
    this.redirect = redirect;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isPasswordChangeRequired() {
    return passwordChangeRequired;
  }

  public void setPasswordChangeRequired(boolean passwordChangeRequired) {
    this.passwordChangeRequired = passwordChangeRequired;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }
}
