package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.AuthResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LogoutRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.PrimeiroAcessoRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.TokenResponseDto;
import br.edu.ifpi.ifala.shared.exceptions.AuthException;
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

  private static final String CREDENCIAIS_TEMP_MSG =
      "Credenciais temporárias validadas. Email enviado para definição de senha definitiva.";

  /**
   * Endpoint para primeiro acesso de usuários. Valida credenciais temporárias antes de enviar email
   * para redefinição de senha definitiva.
   *
   * @param request Credenciais temporárias do usuário (username + senha temporária)
   * @return Resposta da validação e envio do email
   */
  @PostMapping("primeiro-acesso")
  public ResponseEntity<AuthResponseDto> primeiroAcesso(
      @RequestBody PrimeiroAcessoRequestDto request) {
    try {
      // 1. Tenta validar credenciais temporárias.
      TokenResponseDto tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new AuthResponseDto(null, null, false, null, "Credenciais temporárias inválidas."));
      }

      // 2. Se credenciais temporárias válidas, envia email para definir senha definitiva
      authService.sendPasswordResetEmail(request.getUsername());

      return ResponseEntity.ok(new AuthResponseDto(null, null, true, null, CREDENCIAIS_TEMP_MSG));

    } catch (AuthException e) {
      // Verifica se é erro de "conta não totalmente configurada" - isso é esperado no primeiro
      // acesso
      if (e.getMessage().contains("Account is not fully set up")) {
        try {
          // Credenciais válidas mas conta precisa ser configurada - envia email
          authService.sendPasswordResetEmail(request.getUsername());
          return ResponseEntity
              .ok(new AuthResponseDto(null, null, true, null, CREDENCIAIS_TEMP_MSG));
        } catch (AuthException emailError) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponseDto(null, null, false, null, emailError.getMessage()));
        }
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponseDto(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDto(null,
          null, false, null, "Erro interno do servidor: " + e.getMessage()));
    }
  }

  /**
   * Endpoint para autenticação de usuários.
   *
   * @param request Credenciais de login
   * @return Resposta da autenticação com tokens
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
    try {
      // 1. Autentica no Keycloak e obtém os tokens
      TokenResponseDto tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse != null) {
        // 2. URL de redirecionamento padrão - frontend determina dashboard específico
        String redirectUrl = "/dashboard";

        // 3. Retorna ambos os tokens e a URL de redirecionamento
        return ResponseEntity
            .ok(new AuthResponseDto(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(),
                true, redirectUrl, "Login bem-sucedido. Redirecionando para: " + redirectUrl));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponseDto(null, null, false, null, "Credenciais inválidas."));
      }
    } catch (AuthException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponseDto(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDto(null,
          null, false, null, "Erro interno do servidor: " + e.getMessage()));
    }
  }

  /**
   * Endpoint para logout de usuários.
   *
   * @param request Token de refresh para invalidar sessão
   * @return Resposta do logout
   */
  @PostMapping("/logout")
  public ResponseEntity<AuthResponseDto> logout(@RequestBody LogoutRequestDto request) {
    try {
      authService.performKeycloakLogout(request.getRefreshToken());
      return ResponseEntity
          .ok(new AuthResponseDto(null, null, true, "/login", "Logout global bem-sucedido."));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponseDto(null, null,
          false, null, "Erro ao encerrar sessão no servidor: " + e.getMessage()));
    }
  }
}
