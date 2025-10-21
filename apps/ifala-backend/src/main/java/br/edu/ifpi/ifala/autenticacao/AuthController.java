package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de Autenticação responsável por receber requisições HTTP e delegar a lógica de
 * negócio para o AuthService.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/admin/registrar-usuario")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequestDto registroRequest) {
    try {
      logger.info("Requisição de registro recebida para e-mail: {}", registroRequest.email());
      UsuarioResponseDto usuarioResponse = authService.registrarUsuario(registroRequest);
      return ResponseEntity.status(201).body(usuarioResponse);
    } catch (AutenticacaoException e) {
      logger.warn("Falha ao registrar usuário: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }
  }

  /**
   * Endpoint para login e obtenção de tokens de acesso/refresh.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
    try {
      logger.info("Tentativa de login para identificador: {}",
          req.email() != null ? req.email() : req.username());
      LoginResponseDto response = authService.login(req);
      return ResponseEntity.ok(response);
    } catch (AutenticacaoException e) {
      logger.warn("Falha no login: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode())
          .body(new LoginResponseDto(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para redefinir a senha (via token de e-mail) ou mudar a senha (via senha atual).
   */
  @PostMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDto> changePassword(
      @Valid @RequestBody MudarSenhaRequestDto req) {
    try {
      logger.info("Tentativa de redefinição/mudança de senha para o e-mail: {}", req.email());
      LoginResponseDto response = authService.changePassword(req);
      return ResponseEntity.ok(response);
    } catch (AutenticacaoException e) {
      logger.warn("Falha ao mudar/redefinir senha: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode())
          .body(new LoginResponseDto(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para obter um novo Access Token usando o Refresh Token.
   */
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDto> refreshToken(
      @Valid @RequestBody RefreshTokenRequestDto req) {
    try {
      logger.info("Tentativa de refresh de token recebida");
      LoginResponseDto response = authService.refreshToken(req);
      return ResponseEntity.ok(response);
    } catch (AutenticacaoException e) {
      logger.warn("Falha no refresh de token: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode())
          .body(new LoginResponseDto(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para invalidar o Access Token (logout)
   */
  @PostMapping("/sair")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    try {
      authService.logout(request);
      return ResponseEntity.ok("Logout realizado com sucesso.");
    } catch (AutenticacaoException e) {
      logger.warn("Falha no logout: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }
  }
}
