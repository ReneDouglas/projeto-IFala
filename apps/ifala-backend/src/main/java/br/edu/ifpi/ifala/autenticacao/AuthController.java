package br.edu.ifpi.ifala.autenticacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelos endpoints de autenticação.
 * 
 * @author Sistema AvaliaIF
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  /**
   * Endpoint para primeiro acesso de usuários. Valida credenciais temporárias antes de enviar email
   * para redefinição de senha definitiva.
   * 
   * @param request Credenciais temporárias do usuário (username + senha temporária)
   * @return Resposta da validação e envio do email
   */
  @PostMapping("primeiro-acesso")
  public ResponseEntity<AuthResponse> primeiroAcesso(@RequestBody PrimeiroAcessoRequest request) {
    try {
      // 1. Tenta validar credenciais temporárias
      AuthService.TokenResponse tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponse(null, null, false, null, "Credenciais temporárias inválidas."));
      }

      // 2. Se credenciais temporárias válidas, envia email para definir senha definitiva
      authService.sendPasswordResetEmail(request.getUsername());

      return ResponseEntity.ok(new AuthResponse(null, null, true, null,
          "Credenciais temporárias validadas. Email enviado para definição de senha definitiva."));

    } catch (AuthException e) {
      // Verifica se é erro de "conta não totalmente configurada" - isso é esperado no primeiro
      // acesso
      if (e.getMessage().contains("Account is not fully set up")) {
        try {
          // Credenciais válidas mas conta precisa ser configurada - envia email
          authService.sendPasswordResetEmail(request.getUsername());
          return ResponseEntity.ok(new AuthResponse(null, null, true, null,
              "Credenciais temporárias validadas. Email enviado para definição de senha definitiva."));
        } catch (AuthException emailError) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponse(null, null, false, null, emailError.getMessage()));
        }
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponse(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new AuthResponse(null, null, false, null, "Erro interno do servidor: " + e.getMessage()));
    }
  }

  /**
   * Endpoint para autenticação de usuários.
   * 
   * @param request Credenciais de login
   * @return Resposta da autenticação com tokens
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    try {
      // 1. Autentica no Keycloak e obtém os tokens
      AuthService.TokenResponse tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse != null) {
        // 2. URL de redirecionamento padrão - frontend determina dashboard específico
        String redirectUrl = "/dashboard";

        // 3. Retorna ambos os tokens e a URL de redirecionamento
        return ResponseEntity
            .ok(new AuthResponse(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(),
                true, redirectUrl, "Login bem-sucedido. Redirecionando para: " + redirectUrl));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponse(null, null, false, null, "Credenciais inválidas."));
      }
    } catch (AuthException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponse(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          new AuthResponse(null, null, false, null, "Erro interno do servidor: " + e.getMessage()));
    }
  }

  /**
   * Endpoint para logout de usuários.
   * 
   * @param request Token de refresh para invalidar sessão
   * @return Resposta do logout
   */
  @PostMapping("/logout")
  public ResponseEntity<AuthResponse> logout(@RequestBody LogoutRequest request) {
    try {
      authService.performKeycloakLogout(request.getRefreshToken());
      return ResponseEntity
          .ok(new AuthResponse(null, null, true, "/login", "Logout global bem-sucedido."));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null, null, false,
          null, "Erro ao encerrar sessão no servidor: " + e.getMessage()));
    }
  }


  /**
   * DTO para requisições de primeiro acesso (username e senha temporária).
   */
  public static class PrimeiroAcessoRequest {
    private String username;
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  /**
   * DTO para requisições de login (email e senha).
   */
  public static class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Boolean success;
    private String redirectUrl;
    private String message;

    public AuthResponse(String accessToken, String refreshToken, Boolean success,
        String redirectUrl, String message) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.success = success;
      this.redirectUrl = redirectUrl;
      this.message = message;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }

    public Boolean getSuccess() {
      return success;
    }

    public String getRedirectUrl() {
      return redirectUrl;
    }

    public String getMessage() {
      return message;
    }
  }

  public static class LogoutRequest {
    private String refreshToken;

    public String getRefreshToken() {
      return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }
  }

  public static class AuthException extends Exception {
    public AuthException(String message) {
      super(message);
    }

    public AuthException(String message, Throwable cause) {
      super(message, cause);
    }
  }

}
