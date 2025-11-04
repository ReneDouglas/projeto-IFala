package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.PasswordResetTokenCheckDTO;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.Instant;
import java.util.Optional;
import br.edu.ifpi.ifala.shared.exceptions.RefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de Autenticação responsável por receber requisições HTTP e delegar a lógica de
 * negócio para o AuthService.
 * 
 * @author Phaola
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;
  private final CookieService cookieService;
  private final UsuarioRepository usuarioRepository;

  public AuthController(AuthService authService, CookieService cookieService,
      UsuarioRepository usuarioRepository) {
    this.authService = authService;
    this.cookieService = cookieService;
    this.usuarioRepository = usuarioRepository;
  }

  @PostMapping("/admin/registrar-usuario")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequestDTO registroRequest) {
    logger.info("Requisição de registro recebida para e-mail: {}", registroRequest.email());
    UsuarioResponseDTO usuarioResponse = authService.registrarUsuario(registroRequest);
    return ResponseEntity.status(201).body(usuarioResponse);
  }

  /**
   * Endpoint para login e obtenção de tokens de acesso/refresh.
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req) {
    logger.info("Tentativa de login para identificador: {}",
        req.getEmail() != null ? req.getEmail() : req.getUsername());
    LoginResponseDTO response = authService.login(req);

    // Cria cookie HttpOnly de refresh
    ResponseCookie cookie = cookieService.createRefreshTokenCookie(response.refreshToken());
    try {
      String masked = response.refreshToken() != null && response.refreshToken().length() > 8
          ? response.refreshToken().substring(0, 8) + "..."
          : response.refreshToken();
      logger.info("Enviando Set-Cookie de refresh token: {} (len={})", masked,
          response.refreshToken() != null ? response.refreshToken().length() : 0);
    } catch (Exception e) {
      logger.debug("Erro ao mascarar refresh token para log: {}", e.getMessage());
    }
    LoginResponseDTO sanitized =
        new LoginResponseDTO(response.token(), response.issuedAt(), response.expirationTime(), null,
            response.passwordChangeRequired(), response.redirect(), response.message());
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(sanitized);
  }

  /**
   * Endpoint para redefinir a senha (via token de e-mail) ou mudar a senha (via senha atual).
   */
  @PutMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDTO> changePassword(
      @Valid @RequestBody MudarSenhaRequestDTO req) {
    logger.info("Tentativa de redefinição/mudança de senha para o e-mail: {}", req.email());
    LoginResponseDTO response = authService.changePassword(req);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/redefinir-senha/{token:.+}")
  public ResponseEntity<PasswordResetTokenCheckDTO> validateResetToken(@PathVariable String token) {
    logger.info("Validação de token de redefinição de senha: {}", token);

    Optional<Usuario> userOpt = usuarioRepository.findByPasswordResetToken(token);
    if (userOpt.isEmpty()) {
      logger.warn("Token de redefinição não encontrado: {}", token);
      return ResponseEntity.notFound().build();
    }

    Usuario user = userOpt.get();
    if (user.getPasswordResetExpires() == null
        || user.getPasswordResetExpires().isBefore(Instant.now())) {
      logger.warn("Token expirado ou inválido para usuário {}", user.getEmail());
      return ResponseEntity.notFound().build();
    }

    PasswordResetTokenCheckDTO dto =
        new PasswordResetTokenCheckDTO(user.getEmail(), user.getUsername());
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/redefinir-senha")
  public ResponseEntity<?> requestPasswordReset(
      @Valid @RequestBody br.edu.ifpi.ifala.autenticacao.dto.PasswordResetRequestDTO body) {
    String email = body.email();
    logger.info("Solicitação de redefinição de senha para e-mail: {}", email);

    Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);
    if (userOpt.isPresent()) {
      try {
        authService.sendPasswordReset(userOpt.get());
      } catch (Exception e) {
        // Não vazamos detalhes; apenas logamos internamente
        logger.error("Falha ao enviar e-mail de redefinição para {}: {}", email, e.getMessage());
      }
    }

    // Resposta genérica para evitar revelar se o e-mail está cadastrado
    return ResponseEntity
        .ok("Se um usuário com este e-mail existe, um link de redefinição foi enviado.");
  }


  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refreshToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      @RequestBody(required = false) RefreshTokenRequestDTO body) {
    // 1. Tenta obter o token do cookie (padrão de segurança)
    String tokenToUse = refreshToken;

    if (tokenToUse == null || tokenToUse.isEmpty()) {
      if (body != null && body.token() != null && !body.token().isEmpty()) {
        tokenToUse = body.token();
        logger.warn("Refresh token não encontrado no cookie - usando fallback do corpo.");
      } else {
        logger.warn("Tentativa de refresh sem refresh token.");
        ResponseCookie logoutCookie = cookieService.createLogoutCookie();
        return ResponseEntity.status(401).header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
            .body(new LoginResponseDTO(null, null, null, null, false, null,
                "Refresh token não fornecido. Faça login novamente."));
      }
    }

    try {
      LoginResponseDTO response = authService.refreshToken(new RefreshTokenRequestDTO(tokenToUse));

      // Atualiza o cookie HttpOnly com o novo refresh token (roteamento seguro)
      ResponseCookie cookie = cookieService.createRefreshTokenCookie(response.refreshToken());
      LoginResponseDTO sanitized =
          new LoginResponseDTO(response.token(), response.issuedAt(), response.expirationTime(),
              null, response.passwordChangeRequired(), response.redirect(), response.message());

      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(sanitized);

    } catch (RefreshTokenException e) {
      logger.warn("Falha no refresh token para {}. Motivo: {}", tokenToUse, e.getMessage());
      ResponseCookie logoutCookie = cookieService.createLogoutCookie();

      String errorMessage = "Sessão expirada ou token inválido. " + e.getMessage();

      return ResponseEntity.status(401).header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
          .body(new LoginResponseDTO(null, null, null, null, false, null, errorMessage));

    } catch (Exception e) {
      logger.error("Erro interno inesperado durante o refresh de token.", e);
      ResponseCookie logoutCookie = cookieService.createLogoutCookie();
      return ResponseEntity.status(500).header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
          .body(new LoginResponseDTO(null, null, null, null, false, null,
              "Erro interno ao renovar a sessão."));
    }
  }

  /**
   * Endpoint para invalidar o Access Token (logout)
   */
  @PostMapping("/sair")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    authService.logout(request);
    // Limpa o cookie de refresh
    ResponseCookie cookie = cookieService.createLogoutCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body("Logout realizado com sucesso.");
  }
}
