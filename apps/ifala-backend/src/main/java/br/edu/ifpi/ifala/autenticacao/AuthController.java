package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de Autenticação responsável por receber requisições HTTP e
 * delegar a lógica de
 * negócio para o AuthService.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;
  private final CookieService cookieService;

  public AuthController(AuthService authService, CookieService cookieService) {
    this.authService = authService;
    this.cookieService = cookieService;
  }

  @PostMapping("/admin/registrar-usuario")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequestDTO registroRequest) {
    try {
      logger.info("Requisição de registro recebida para e-mail: {}", registroRequest.email());
      UsuarioResponseDTO usuarioResponse = authService.registrarUsuario(registroRequest);
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
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req) {
    try {
      logger.info("Tentativa de login para identificador: {}",
          req.email() != null ? req.email() : req.username());
      LoginResponseDTO response = authService.login(req);

      // Cria cookie HttpOnly de refresh
      ResponseCookie cookie = cookieService.createRefreshTokenCookie(response.refreshToken());
      // Log do Set-Cookie enviado (mascarado) para auxiliar debug em testes
      try {
        String masked = response.refreshToken() != null && response.refreshToken().length() > 8
            ? response.refreshToken().substring(0, 8) + "..."
            : response.refreshToken();
        logger.info("Enviando Set-Cookie de refresh token: {} (len={})", masked,
            response.refreshToken() != null ? response.refreshToken().length() : 0);
      } catch (Exception e) {
        logger.debug("Erro ao mascarar refresh token para log: {}", e.getMessage());
      }
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    } catch (AutenticacaoException e) {
      logger.warn("Falha no login: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode())
          .body(new LoginResponseDTO(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para redefinir a senha (via token de e-mail) ou mudar a senha (via
   * senha atual).
   */
  @PostMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDTO> changePassword(
      @Valid @RequestBody MudarSenhaRequestDTO req) {
    try {
      logger.info("Tentativa de redefinição/mudança de senha para o e-mail: {}", req.email());
      LoginResponseDTO response = authService.changePassword(req);
      return ResponseEntity.ok(response);
    } catch (AutenticacaoException e) {
      logger.warn("Falha ao mudar/redefinir senha: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode())
          .body(new LoginResponseDTO(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para obter um novo Access Token usando o Refresh Token.
   */
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      @RequestBody(required = false) RefreshTokenRequestDTO body) {

    // 1. Verifica a presença do Refresh Token no cookie
    if (refreshToken == null || refreshToken.isEmpty()) {
      // Se não veio cookie, tenta aceitar o token no corpo como fallback
      if (body != null && body.token() != null && !body.token().isEmpty()) {
        refreshToken = body.token();
        logger.warn(
            "Refresh token não encontrado no cookie — usando token recebido no corpo da requisição (fallback). Consider verificar CORS/credentials no frontend.");
      } else {
        logger.warn("Tentativa de refresh sem refresh token no cookie.");
        // Retorna 401 com um corpo JSON informando o erro e instruindo o cliente a
        // fazer login novamente
        ResponseCookie logoutCookie = cookieService.createLogoutCookie();
        return ResponseEntity.status(401)
            .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
            .body(new LoginResponseDTO(null, null, null, null, false, null,
                "Refresh token não encontrado no cookie."));
      }
    }

    // Log com máscara (não imprime token completo) para ajudar no debug
    try {
      String masked = refreshToken.length() > 8 ? refreshToken.substring(0, 8) + "..." : refreshToken;
      logger.info("Refresh token cookie recebido: {} (len={})", masked, refreshToken.length());
    } catch (Exception e) {
      logger.debug("Erro ao mascarar refresh token para log: {}", e.getMessage());
    }

    try {
      // 2. Chama a lógica de negócio do Service
      RefreshTokenRequestDTO effectiveReq = new RefreshTokenRequestDTO(refreshToken);
      LoginResponseDTO response = authService.refreshToken(effectiveReq);

      // 3. Cria um novo cookie com o Refresh Token rotacionado
      ResponseCookie newRefreshTokenCookie = cookieService.createRefreshTokenCookie(response.refreshToken());

      // 4. Retorna a resposta com o novo Access Token no corpo e o novo Refresh Token
      // no Header
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
          .body(response);

    } catch (AutenticacaoException e) {
      // 5. Captura exceções (Token Inválido, Expirado, etc.)
      logger.warn("Falha no refresh de token: {}", e.getMessage());

      // Cria um cookie de logout para forçar o cliente a remover o token inválido
      ResponseCookie logoutCookie = cookieService.createLogoutCookie();

      // Retorna o status code da exceção (e.g., 401) e o cookie de logout
      return ResponseEntity.status(e.getStatusCode())
          .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
          // Retorna um DTO de login, mas com campos nulos e apenas a mensagem de erro
          .body(new LoginResponseDTO(null, null, null, null, false, null, e.getMessage()));
    }
  }

  /**
   * Endpoint para invalidar o Access Token (logout)
   */
  @PostMapping("/sair")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    try {
      authService.logout(request);
      // Limpa o cookie de refresh
      ResponseCookie cookie = cookieService.createLogoutCookie();
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body("Logout realizado com sucesso.");
    } catch (AutenticacaoException e) {
      logger.warn("Falha no logout: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }
  }
}
