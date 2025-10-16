package br.edu.ifpi.ifala.autenticacao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import br.edu.ifpi.ifala.autenticacao.dto.TokenResponseDTO;
import br.edu.ifpi.ifala.shared.exceptions.AuthException;
import br.edu.ifpi.ifala.shared.exceptions.KeycloakAdminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Serviço de autenticação que integra com Keycloak.
 * 
 * @author Sistema AvaliaIF
 */

@Service
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  @Value("${keycloak.auth-server-url}")
  private String keycloakServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.resource}")
  private String clientId;

  private final RestTemplate restTemplate;
  private final KeycloakAdminService keycloakAdminService;

  /**
   * Construtor com injeção de dependências.
   *
   * @param restTemplate Cliente HTTP para requisições
   * @param keycloakAdminService Serviço de administração do Keycloak
   */
  public AuthService(RestTemplate restTemplate, KeycloakAdminService keycloakAdminService) {
    this.restTemplate = restTemplate;
    this.keycloakAdminService = keycloakAdminService;
  }

  /**
   * Autentica um usuário no Keycloak.
   *
   * @param username
   * @param password
   * @return TokenResponse com access_token e refresh_token se autenticação bem-sucedida, null caso
   *         contrário
   * @throws AuthException se falhar na autenticação
   */
  public TokenResponseDTO authenticateUser(String username, String password) throws AuthException {
    try {
      final String tokenEndpoint =
          keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("grant_type", "password");
      params.add("client_id", clientId);
      params.add("username", username);
      params.add("password", password);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

      @SuppressWarnings("rawtypes")
      ResponseEntity<Map> response =
          restTemplate.exchange(tokenEndpoint, HttpMethod.POST, request, Map.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenResponse = response.getBody();

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");

        return new TokenResponseDTO(accessToken, refreshToken);
      }

      return null;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        throw new AuthException("Credenciais inválidas");
      }
      throw new AuthException("Erro na autenticação: " + e.getMessage());
    } catch (Exception e) {
      throw new AuthException("Erro interno na autenticação: " + e.getMessage());
    }
  }

  /**
   * Envia email de redefinição de senha através do Keycloak Admin API.
   *
   * @param username Nome de usuário (email)
   */
  public void sendPasswordResetEmail(String username) throws AuthException {
    try {
      System.out.println("Tentando enviar email de redefinição para: " + username);
      keycloakAdminService.sendPasswordResetEmail(username);
      System.out.println("Email enviado com sucesso para: " + username);
    } catch (KeycloakAdminException e) {
      System.err.println("Erro ao enviar email para " + username + ": " + e.getMessage());
      throw new AuthException("Erro ao enviar email de redefinição de senha: " + e.getMessage());
    }
  }

  /**
   * Realiza logout completo no Keycloak invalidando o refresh token.
   *
   * @param refreshToken Token de refresh para invalidar a sessão
   * @throws AuthException se falhar ao fazer logout
   */
  public void performKeycloakLogout(String refreshToken) throws AuthException {
    try {
      final String logoutEndpoint =
          keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("client_id", clientId);
      params.add("refresh_token", refreshToken);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

      ResponseEntity<Void> response =
          restTemplate.exchange(logoutEndpoint, HttpMethod.POST, request, Void.class);

      if (response.getStatusCode() != HttpStatus.NO_CONTENT
          && response.getStatusCode() != HttpStatus.OK) {
        throw new AuthException("Falha ao realizar logout no Keycloak");
      }

      System.out.println("Logout realizado com sucesso no Keycloak");

    } catch (HttpClientErrorException e) {
      System.err.println(
          "Erro HTTP ao fazer logout: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
      throw new AuthException("Erro ao realizar logout: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Erro interno ao fazer logout: " + e.getMessage());
      throw new AuthException("Erro interno ao realizar logout: " + e.getMessage());
    }
  }
}
