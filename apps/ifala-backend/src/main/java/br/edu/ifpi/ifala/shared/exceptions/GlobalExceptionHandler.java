package br.edu.ifpi.ifala.shared.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Classe responsável por manipular exceções globalmente na aplicação. Utiliza a
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções
 * lançadas pelos
 * controladores REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(KeycloakAdminException.class)
  public ResponseEntity<Map<String, String>> handleKeycloakAdminException(
      KeycloakAdminException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", ex.getMessage()));
  }

  /**
   * Manipula exceções de validação de argumentos de método.
   * 
   * @param ex A exceção capturada.
   * @return Um mapa contendo os campos inválidos e as mensagens de erro.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
