package br.edu.ifpi.ifala.autenticacao.dto;

import java.io.Serializable;
import java.util.Objects;

public class LoginResponseDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String token;
  private boolean passwordChangeRequired;
  private String redirect;
  private String message;

  public LoginResponseDto() {}

  public LoginResponseDto(String token, boolean passwordChangeRequired, String redirect,
      String message) {
    this.token = token;
    this.passwordChangeRequired = passwordChangeRequired;
    this.redirect = redirect;
    this.message = message;
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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    LoginResponseDto that = (LoginResponseDto) o;
    return passwordChangeRequired == that.passwordChangeRequired
        && Objects.equals(token, that.token) && Objects.equals(redirect, that.redirect)
        && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, passwordChangeRequired, redirect, message);
  }

  @Override
  public String toString() {
    return "LoginResponseDto{" + "token='" + token + '\'' + ", passwordChangeRequired="
        + passwordChangeRequired + ", redirect='" + redirect + '\'' + ", message='" + message + '\''
        + '}';
  }
}
