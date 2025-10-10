package br.edu.ifpi.ifala.shared.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * Classe responsável por manipular exceções globalmente na aplicação. Utiliza a
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções lançadas pelos
 * controladores REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Manipula exceções do tipo AuthException lançadas pelos controladores REST.
   *
   * @param ex a exceção AuthException capturada
   * @return ResponseEntity contendo o erro e o status HTTP UNAUTHORIZED
   */
  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
  }

  /**
   * Manipula exceções do tipo KeycloakAdminException lançadas pelos controladores REST.
   *
   * @param ex a exceção KeycloakAdminException capturada
   * @return ResponseEntity contendo o erro e o status HTTP INTERNAL_SERVER_ERROR
   */
  @ExceptionHandler(KeycloakAdminException.class)
  public ResponseEntity<Map<String, String>> handleKeycloakAdminException(
      KeycloakAdminException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", ex.getMessage()));
  }

}
