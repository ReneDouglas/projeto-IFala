package br.edu.ifpi.ifala.shared.exceptions;

// Imports combinados de ambas as versões
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Arrays; // Adicionado, pois era usado pelo handler HttpMessageNotReadableException

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Import das dependências específicas do seu código
import br.edu.ifpi.ifala.autenticacao.CookieService;

/**
 * Classe responsável por manipular exceções globalmente na aplicação. Utiliza a
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções lançadas pelos
 * controladores REST.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private final CookieService cookieService;

  public GlobalExceptionHandler(CookieService cookieService) {
    this.cookieService = cookieService;
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
    errors.put("violations",
        ex.getConstraintViolations().stream()
            .map(cv -> Map.of("field", cv.getPropertyPath().toString(), "message", cv.getMessage()))
            .collect(Collectors.toList()));
    log.warn("Erro de violação de constraint na entidade: {}", errors);
    return errors;
  }

  /**
   * Manipula exceções de validação de argumentos de método (DTOs). (Versão da 'development'
   * escolhida por conter log)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    log.warn("Erro de validação nos dados da requisição (DTO): {}", errors);
    return errors;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Erro ao ler/parsear a requisição JSON: {}", ex.getMessage());
    String specificMessage = "Erro no formato da requisição JSON.";
    Throwable cause = ex.getCause();
    if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ifx) {
      if (ifx.getTargetType() != null && ifx.getTargetType().isEnum()) {
        specificMessage =
            String.format("Valor inválido '%s' para o campo '%s'. Valores aceitos: %s",
                ifx.getValue(), ifx.getPath().get(ifx.getPath().size() - 1).getFieldName(),
                Arrays.toString(ifx.getTargetType().getEnumConstants()));
      }
    } else if (cause instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException mie) {
      if (!mie.getPath().isEmpty()) {
        specificMessage = String.format(
            "Tipo de dado inválido para o campo '%s'. Verifique a documentação da API.",
            mie.getPath().get(mie.getPath().size() - 1).getFieldName());
      }
    }
    return Map.of("error", specificMessage);
  }

  /**
   * Manipula AutenticacaoException (exceptions de negócio/autenticação definidas pela aplicação) e
   * converte o statusCode interno para uma resposta HTTP.
   */
  @ExceptionHandler(AutenticacaoException.class)
  public ResponseEntity<Map<String, String>> handleAutenticacaoException(AutenticacaoException ex) {
    // Usando o logger estático da classe
    log.warn("AutenticacaoException (status={}): {}", ex.getStatusCode(), ex.getMessage());
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }

  /**
   * Handler específico para RefreshTokenException: devolve o status apropriado e adiciona um
   * Set-Cookie para forçar remoção do refresh token no cliente.
   */
  @ExceptionHandler(RefreshTokenException.class)
  public ResponseEntity<Map<String, String>> handleRefreshTokenException(RefreshTokenException ex) {
    // Usando o logger estático da classe
    log.warn("RefreshTokenException (status={}): {}", ex.getStatusCode(), ex.getMessage());
    ResponseCookie logoutCookie = cookieService.createLogoutCookie();
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(ex.getStatusCode())
        .header(HttpHeaders.SET_COOKIE, logoutCookie.toString()).body(body);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleGenericException(Exception ex) {
    // Loga a exceção inteira (stack trace), essencial para erros inesperados
    log.error("Erro inesperado na aplicação: {}", ex.getMessage(), ex);
    return Map.of("error", "Ocorreu um erro interno inesperado no servidor.");
  }
}
