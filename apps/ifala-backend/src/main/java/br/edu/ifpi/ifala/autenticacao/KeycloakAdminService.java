package br.edu.ifpi.ifala.autenticacao;

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
import br.edu.ifpi.ifala.shared.exception.KeycloakAdminException;

import java.util.List;
import java.util.Map;

/**
 * Serviço para integração com Keycloak Admin API.
 * 
 * @author Sistema AvaliaIF
 */
@Service
public class KeycloakAdminService {

  @Value("${keycloak.auth-server-url}")
  private String keycloakServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.admin.username}")
  private String admin;

  @Value("${keycloak.admin.password}")
  private String adminPassword;

  private final RestTemplate restTemplate;

  /**
   * Construtor que injeta RestTemplate.
   *
   * @param restTemplate cliente HTTP para requisições
   */
  public KeycloakAdminService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Obtém token de administrador do Keycloak.
   *
   * @return Token de acesso de administrador
   * @throws KeycloakAdminException se não conseguir obter o token
   */
  public String getAdminToken() throws KeycloakAdminException {
    try {
      final String tokenEndpoint =
          keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
      params.add("grant_type", "password");
      params.add("client_id", "admin-cli");
      params.add("username", admin);
      params.add("password", adminPassword);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

      @SuppressWarnings("rawtypes")
      ResponseEntity<Map> response =
          restTemplate.exchange(tokenEndpoint, HttpMethod.POST, request, Map.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenResponse = response.getBody();
        return (String) tokenResponse.get("access_token");
      }

      throw new KeycloakAdminException("Falha ao obter token de administrador");
    } catch (HttpClientErrorException e) {
      throw new KeycloakAdminException("Erro na autenticação de administrador: " + e.getMessage());
    } catch (Exception e) {
      throw new KeycloakAdminException(
          "Erro interno ao obter token de administrador: " + e.getMessage());
    }
  }

  /**
   * Busca usuário por username ou email no Keycloak.
   *
   * @param usernameOrEmail Nome de usuário ou email a ser buscado
   * @return ID do usuário se encontrado
   * @throws KeycloakAdminException se usuário não encontrado ou erro na busca
   */
  private String findUserByUsername(String usernameOrEmail) throws KeycloakAdminException {
    try {
      String adminToken = getAdminToken();

      // Primeiro tenta buscar por username
      String userId = searchUserByParameter("username", usernameOrEmail, adminToken);
      if (userId != null) {
        return userId;
      }

      // Se não encontrou, tenta buscar por email
      userId = searchUserByParameter("email", usernameOrEmail, adminToken);
      if (userId != null) {
        return userId;
      }

      throw new KeycloakAdminException("Usuário não encontrado: " + usernameOrEmail);
    } catch (KeycloakAdminException e) {
      throw e; // Re-throw KeycloakAdminException
    } catch (Exception e) {
      throw new KeycloakAdminException("Erro interno ao buscar usuário: " + e.getMessage());
    }
  }

  /**
   * Busca usuário por um parâmetro específico (username ou email).
   *
   * @param parameter Nome do parâmetro (username ou email)
   * @param value Valor a ser buscado
   * @param adminToken Token de administrador
   * @return ID do usuário se encontrado, null caso contrário
   */
  private String searchUserByParameter(String parameter, String value, String adminToken) {
    try {
      final String usersEndpoint =
          keycloakServerUrl + "/admin/realms/" + realm + "/users?" + parameter + "=" + value;

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(adminToken);

      HttpEntity<String> request = new HttpEntity<>(headers);

      @SuppressWarnings("rawtypes")
      ResponseEntity<List> response =
          restTemplate.exchange(usersEndpoint, HttpMethod.GET, request, List.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = response.getBody();
        if (users != null && !users.isEmpty()) {
          Map<String, Object> user = users.get(0);
          return (String) user.get("id");
        }
      }

      return null; // Usuário não encontrado com este parâmetro
    } catch (Exception e) {
      System.err.println("Erro ao buscar usuário por " + parameter + ": " + e.getMessage());
      return null;
    }
  }

  /**
   * Envia email de redefinição de senha para o usuário.
   *
   * @param username Nome de usuário que receberá o email
   * @throws KeycloakAdminException se falhar ao enviar o email
   */
  public void sendPasswordResetEmail(String username) throws KeycloakAdminException {
    try {
      String adminToken = getAdminToken();
      String userId = findUserByUsername(username);

      final String resetPasswordEndpoint = keycloakServerUrl + "/admin/realms/" + realm + "/users/"
          + userId + "/execute-actions-email";

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(adminToken);
      headers.setContentType(MediaType.APPLICATION_JSON);


      List<String> actions = List.of("UPDATE_PASSWORD");

      HttpEntity<List<String>> request = new HttpEntity<>(actions, headers);

      ResponseEntity<Void> response =
          restTemplate.exchange(resetPasswordEndpoint, HttpMethod.PUT, request, Void.class);

      if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
        throw new KeycloakAdminException("Falha ao enviar email de redefinição de senha");
      }

    } catch (HttpClientErrorException e) {
      throw new KeycloakAdminException("Erro ao enviar email de redefinição: " + e.getMessage());
    } catch (Exception e) {
      throw new KeycloakAdminException("Erro interno ao enviar email: " + e.getMessage());
    }
  }

  /**
   * Redefine a senha de um usuário no Keycloak.
   *
   * @param username
   * @param newPassword
   * @throws KeycloakAdminException se falhar ao redefinir a senha
   */
  public void resetUserPassword(String username, String newPassword) throws KeycloakAdminException {
    try {
      String adminToken = getAdminToken();
      String userId = findUserByUsername(username);

      final String resetPasswordEndpoint =
          keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(adminToken);
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Corpo da requisição para redefinir senha
      Map<String, Object> passwordData =
          Map.of("type", "password", "value", newPassword, "temporary", false);

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(passwordData, headers);

      ResponseEntity<Void> response =
          restTemplate.exchange(resetPasswordEndpoint, HttpMethod.PUT, request, Void.class);

      if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
        throw new KeycloakAdminException("Falha ao redefinir senha");
      }

    } catch (HttpClientErrorException e) {
      throw new KeycloakAdminException("Erro ao redefinir senha: " + e.getMessage());
    } catch (Exception e) {
      throw new KeycloakAdminException("Erro interno ao redefinir senha: " + e.getMessage());
    }
  }
}
