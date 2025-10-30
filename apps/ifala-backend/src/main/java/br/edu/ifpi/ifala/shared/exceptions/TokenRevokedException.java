package br.edu.ifpi.ifala.shared.exceptions;

public class TokenRevokedException extends RuntimeException {

  public TokenRevokedException(String message) {
    super(message);
  }

  public TokenRevokedException(String message, Throwable cause) {
    super(message, cause);
  }
}
