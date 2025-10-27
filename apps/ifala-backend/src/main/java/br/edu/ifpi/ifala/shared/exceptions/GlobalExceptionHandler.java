package br.edu.ifpi.ifala.shared.exceptions;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import br.edu.ifpi.ifala.autenticacao.CookieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe responsável por manipular exceções globalmente na aplicação. Utiliza a
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções lançadas pelos
 * anotação @RestControllerAdvice do Spring para interceptar e tratar exceções lançadas pelos
 * controladores REST.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
  private final CookieService cookieService;

  public GlobalExceptionHandler(CookieService cookieService) {
    this.cookieService = cookieService;
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



  // Opcional: Trata exceções genéricas que você não capturou
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    Map<String, String> responseBody = Map.of("error", "Ocorreu um erro interno no servidor.");

    return new ResponseEntity<>(responseBody, status);
  }

  /**
   * Manipula AutenticacaoException (exceptions de negócio/autenticação definidas pela aplicação) e
   * converte o statusCode interno para uma resposta HTTP.
   */
  @ExceptionHandler(AutenticacaoException.class)
  public ResponseEntity<Map<String, String>> handleAutenticacaoException(AutenticacaoException ex) {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    logger.warn("AutenticacaoException (status={}): {}", ex.getStatusCode(), ex.getMessage());
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
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    logger.warn("RefreshTokenException (status={}): {}", ex.getStatusCode(), ex.getMessage());
    ResponseCookie logoutCookie = cookieService.createLogoutCookie();
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(ex.getStatusCode())
        .header(HttpHeaders.SET_COOKIE, logoutCookie.toString()).body(body);
  }

}
