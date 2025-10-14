package br.edu.ifpi.ifala.autenticacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ifpi.ifala.autenticacao.dto.TokenResponseDTO;
import br.edu.ifpi.ifala.shared.exceptions.AuthException;
import br.edu.ifpi.ifala.autenticacao.dto.AuthResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LogoutRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.PrimeiroAcessoRequestDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável pelos endpoints de autenticação.
 * 
 * @author Sistema AvaliaIF
 */
@Slf4j
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
  public ResponseEntity<AuthResponseDTO> primeiroAcesso(
      @RequestBody PrimeiroAcessoRequestDTO request) {
    try {
      // 1. Tenta validar credenciais temporárias
      TokenResponseDTO tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new AuthResponseDTO(null, null, false, null, "Credenciais temporárias inválidas."));
      }

      // 2. Se credenciais temporárias válidas, envia email para definir senha definitiva
      authService.sendPasswordResetEmail(request.getUsername());

      return ResponseEntity.ok(new AuthResponseDTO(null, null, true, null,
          "Credenciais temporárias validadas. Email enviado para definição de senha definitiva."));

    } catch (AuthException e) {
      // Verifica se é erro de "conta não totalmente configurada" - isso é esperado no primeiro
      // acesso
      if (e.getMessage().contains("Account is not fully set up")) {
        try {
          // Credenciais válidas mas conta precisa ser configurada - envia email
          authService.sendPasswordResetEmail(request.getUsername());
          return ResponseEntity.ok(new AuthResponseDTO(null, null, true, null,
              "Credenciais temporárias validadas. Email enviado para definição de senha definitiva."));
        } catch (AuthException emailError) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new AuthResponseDTO(null, null, false, null, emailError.getMessage()));
        }
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponseDTO(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDTO(null,
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
  public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
    try {
      // 1. Autentica no Keycloak e obtém os tokens
      TokenResponseDTO tokenResponse =
          authService.authenticateUser(request.getUsername(), request.getPassword());

      if (tokenResponse != null) {
        // 2. URL de redirecionamento padrão - frontend determina dashboard específico
        String redirectUrl = "/dashboard";

        // 3. Retorna ambos os tokens e a URL de redirecionamento
        return ResponseEntity
            .ok(new AuthResponseDTO(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(),
                true, redirectUrl, "Login bem-sucedido. Redirecionando para: " + redirectUrl));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponseDTO(null, null, false, null, "Credenciais inválidas."));
      }
    } catch (AuthException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new AuthResponseDTO(null, null, false, null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDTO(null,
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
  public ResponseEntity<AuthResponseDTO> logout(@RequestBody LogoutRequestDTO request) {
    try {
      authService.performKeycloakLogout(request.getRefreshToken());
      return ResponseEntity
          .ok(new AuthResponseDTO(null, null, true, "/login", "Logout global bem-sucedido."));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponseDTO(null, null,
          false, null, "Erro ao encerrar sessão no servidor: " + e.getMessage()));
    }
  }
}
