package br.edu.ifpi.ifala.autenticacao.dto;

import java.io.Serializable;
import java.util.Objects;

public class LoginRequestDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String email;
  private String password;

  public LoginRequestDto() {}

  public LoginRequestDto(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
