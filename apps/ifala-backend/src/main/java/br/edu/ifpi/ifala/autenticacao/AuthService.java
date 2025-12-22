package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.AtualizarUsuarioRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDTO;
import br.edu.ifpi.ifala.shared.exceptions.InvalidTokenException;
import br.edu.ifpi.ifala.shared.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioDetalheResponseDTO;

public interface AuthService {

  /**
   * Registra um novo usuário no sistema.
   *
   * @param registroRequest dados do usuário a ser registrado.
   * @return DTO com informações não-sensíveis do usuário criado.
   * @throws IllegalArgumentException se o e-mail ou username já estiverem em uso.
   * 
   * @author Phaola
   * @author Jhonatas G Ribeiro
   */
  UsuarioResponseDTO registrarUsuario(RegistroRequestDTO registroRequest);

  /**
   * Lista todos os usuários de forma paginada para a área de gerenciamento.
   *
   * @param pageable Objeto contendo as informações de paginação (página, tamanho, ordenação).
   * @return Uma página (`Page`).
   */
  Page<UsuarioDetalheResponseDTO> listarUsuario(Pageable pageable);

  /**
   * Busca um usuário específico pelo seu ID para a área de gerenciamento.
   *
   * @param id O ID do usuário a ser buscado.
   * @return O {@link UsuarioDetalheResponseDTO} com os detalhes do usuário encontrado.
   */
  UsuarioDetalheResponseDTO buscarUsuarioPorId(Long id);

  /**
   * Atualiza os dados de um usuário existente.
   *
   * @param id O ID do usuário a ser atualizado.
   * @param registroRequest DTO com os novos dados do usuário.
   * @return O {@link UsuarioDetalheResponseDTO} com os dados atualizados.
   */
  UsuarioDetalheResponseDTO atualizarUsuario(Long id,
      AtualizarUsuarioRequestDTO atualizarUsuarioRequestDTO);

  /**
   * Gera um token de redefinição de senha e envia um e-mail ao usuário. * @param user A entidade do
   * usuário para quem o e-mail será enviado.
   */
  void sendPasswordReset(Usuario user);

  /**
   * Realiza o processo de login e gera os tokens. * @param req O DTO de requisição de login.
   * 
   * @return O DTO de resposta de login contendo tokens e redirecionamento.
   */
  LoginResponseDTO login(LoginRequestDTO req);

  /**
   * Altera a senha do usuário. * @param req O DTO de requisição de mudança de senha (via senha
   * atual ou token).
   * 
   * @return O DTO de resposta de login com novos tokens após a mudança de senha.
   */
  LoginResponseDTO changePassword(MudarSenhaRequestDTO req);

  /**
   * Obtém o email do usuário pelo token de redefinição de senha.
   * 
   * @param token O token de redefinição de senha.
   * @return O email do usuário associado ao token.
   * @throws InvalidTokenException se o token for inválido.
   * @throws TokenExpiredException se o token estiver expirado.
   */
  String getEmailByResetToken(String token);

  /**
   * Gera um novo Access Token usando um Refresh Token válido. * @param req O DTO de requisição de
   * Refresh Token.
   * 
   * @return O DTO de resposta de login com novos tokens.
   */
  LoginResponseDTO refreshToken(RefreshTokenRequestDTO req);

  /**
   * Realiza o processo de logout e blacklists o token de acesso. * @param request A requisição HTTP
   * para extrair o token do header.
   */
  void logout(HttpServletRequest request);
}
