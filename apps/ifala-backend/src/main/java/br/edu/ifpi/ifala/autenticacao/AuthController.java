package br.edu.ifpi.ifala.autenticacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsável pelos endpoints de autenticação.
 * 
 * @author Sistema AvaliaIF
 */

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação",
    description = "Endpoints para login, logout e primeiro acesso de usuários.")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
  @Operation(summary = "Realiza o fluxo de primeiro acesso",
      description = "Valida credenciais temporárias de um usuário e dispara o envio de e-mail para definição de senha definitiva.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Credenciais validadas e e-mail enviado com sucesso",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "401", description = "Credenciais temporárias inválidas",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "400",
          description = "Erro na requisição ou falha ao enviar o e-mail",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class)))})

  public ResponseEntity<AuthResponseDTO> primeiroAcesso(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Credenciais temporárias do usuário (username + senha).", required = true,
          content = @Content(schema = @Schema(
              implementation = PrimeiroAcessoRequestDTO.class))) @RequestBody PrimeiroAcessoRequestDTO request) {
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
  @Operation(summary = "Autentica um usuário",
      description = "Realiza o login de um usuário com credenciais definitivas e retorna os tokens de acesso e refresh.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class)))})

  public ResponseEntity<AuthResponseDTO> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Credenciais de login (username + senha).", required = true,
          content = @Content(schema = @Schema(
              implementation = LoginRequestDTO.class))) @RequestBody LoginRequestDTO request) {
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
  @Operation(summary = "Realiza o logout do usuário",
      description = "Invalida a sessão do usuário no provedor de identidade (Keycloak) usando o refresh token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Logout bem-sucedido",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "400",
          description = "Erro ao realizar o logout (ex: token inválido)",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AuthResponseDTO.class)))})


  public ResponseEntity<AuthResponseDTO> logout(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Refresh token do usuário para invalidar a sessão.", required = true,
          content = @Content(schema = @Schema(
              implementation = LogoutRequestDTO.class))) @RequestBody LogoutRequestDTO request) {
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
