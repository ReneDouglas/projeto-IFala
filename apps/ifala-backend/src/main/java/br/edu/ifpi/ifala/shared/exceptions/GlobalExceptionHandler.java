package br.edu.ifpi.ifala.shared.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe responsável por manipular exceções globalmente na aplicação. Utiliza a
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções
 * lançadas pelos
 * controladores REST.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
    log.error("Erro de autenticação: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(KeycloakAdminException.class)
  public ResponseEntity<Map<String, String>> handleKeycloakAdminException(
      KeycloakAdminException ex) {
    log.error("Erro de administração do Keycloak: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.warn("Recurso não encontrado: {}", ex.getMessage());
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("Argumento ilegal/inválido na requisição: {}", ex.getMessage());
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, Object> errors = new HashMap<>();
    errors.put("error", "Erro de validação nos dados da entidade.");
    errors.put("violations", ex.getConstraintViolations().stream()
        .map(cv -> Map.of(
            "field", cv.getPropertyPath().toString(),
            "message", cv.getMessage()))
        .collect(Collectors.toList()));
    log.warn("Erro de violação de constraint na entidade: {}", errors);
    return errors;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    log.warn("Erro de validação nos dados da requisição (DTO): {}", errors);
    return errors;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleGenericException(Exception ex) {
    log.error("Erro inesperado na aplicação: {}", ex.getMessage(), ex);
    return Map.of("error", "Ocorreu um erro interno inesperado no servidor.");
  }

}
