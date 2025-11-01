package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

  /**
   * Registra um novo usuário no sistema.
   *
   * @param registroRequest dados do usuário a ser registrado.
   * @return DTO com informações não-sensíveis do usuário criado.
   * @throws IllegalArgumentException se o e-mail ou username já estiverem em uso.
   * 
   * @author Phaola
   */
  UsuarioResponseDto registrarUsuario(RegistroRequestDto registroRequest);

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
  LoginResponseDto login(LoginRequestDto req);

  /**
   * Altera a senha do usuário. * @param req O DTO de requisição de mudança de senha (via senha
   * atual ou token).
   * 
   * @return O DTO de resposta de login com novos tokens após a mudança de senha.
   */
  LoginResponseDto changePassword(MudarSenhaRequestDto req);

  /**
   * Gera um novo Access Token usando um Refresh Token válido. * @param req O DTO de requisição de
   * Refresh Token.
   * 
   * @return O DTO de resposta de login com novos tokens.
   */
  LoginResponseDto refreshToken(RefreshTokenRequestDto req);

  /**
   * Realiza o processo de logout e blacklists o token de acesso. * @param request A requisição HTTP
   * para extrair o token do header.
   */
  void logout(HttpServletRequest request);
}
