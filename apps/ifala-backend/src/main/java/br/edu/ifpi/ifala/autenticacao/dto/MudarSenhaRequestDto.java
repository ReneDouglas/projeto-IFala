package br.edu.ifpi.ifala.autenticacao.dto;

import java.io.Serializable;
import java.util.Objects;

// DTO para receber os dados de mudança de senha
public class MudarSenhaRequestDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String email;
  private String currentPassword;
  private String newPassword;
  private String token;

  public MudarSenhaRequestDto() {}

  public MudarSenhaRequestDto(String email, String currentPassword, String newPassword) {
    this.email = email;
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  public MudarSenhaRequestDto(String email, String token, String newPassword, boolean fromToken) {
    this.email = email;
    this.token = token;
    this.newPassword = newPassword;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MudarSenhaRequestDto that = (MudarSenhaRequestDto) o;
    return Objects.equals(email, that.email) && Objects.equals(token, that.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, token);
  }

  @Override
  public String toString() {
    return "MudarSenhaRequestDto{" + "email='" + email + '\'' + ", token='" + token + '\'' + '}';
  }
}
