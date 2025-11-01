package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.TokenDataDto;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDto;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import br.edu.ifpi.ifala.security.JwtUtil;
import br.edu.ifpi.ifala.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.edu.ifpi.ifala.shared.exceptions.RefreshTokenException;
import br.edu.ifpi.ifala.shared.exceptions.EmailAlreadyExistsException;
import br.edu.ifpi.ifala.shared.exceptions.UsernameAlreadyExistsException;
import br.edu.ifpi.ifala.shared.exceptions.InvalidRoleException;
import br.edu.ifpi.ifala.shared.exceptions.EmailServiceException;
import br.edu.ifpi.ifala.shared.exceptions.InvalidCredentialsException;
import br.edu.ifpi.ifala.shared.exceptions.InternalAuthException;
import br.edu.ifpi.ifala.shared.exceptions.UserNotFoundException;
import br.edu.ifpi.ifala.shared.exceptions.InvalidTokenException;
import br.edu.ifpi.ifala.shared.exceptions.TokenExpiredException;
import br.edu.ifpi.ifala.shared.exceptions.PasswordMismatchException;
import br.edu.ifpi.ifala.shared.exceptions.MissingAuthorizationHeaderException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de autenticação.
 * 
 * @author Phaola
 */

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UsuarioRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final JavaMailSender mailSender;

  private final AuthenticationManager authenticationManager;

  @Value("${app.frontend.reset-password-url:http://localhost:3000/reset-password}")
  private String resetPasswordUrl;

  public AuthServiceImpl(UsuarioRepository userRepository, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService,
      RefreshTokenService refreshTokenService, JavaMailSender mailSender,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.tokenBlacklistService = tokenBlacklistService;
    this.refreshTokenService = refreshTokenService;
    this.mailSender = mailSender;
    this.authenticationManager = authenticationManager;
  }

  /**
   * Determina o caminho de redirecionamento do usuário após o login, baseado em seus perfis.
   */
  private String determineRedirect(Usuario user) {
    String rolesList = user.getRoles().stream().map(Perfis::name).collect(Collectors.joining(", "));

    logger.debug("Perfis do usuário {}: [{}]", user.getEmail(), rolesList);

    String determinedPath = user.getRoles().stream().map(Enum::name).map(String::toLowerCase)
        .filter(r -> r.equals("admin")).findFirst().map(r -> switch (r) {
          case "admin" -> "/admin/dashboard";
          default -> "/";
        }).orElse("/");
    logger.info("Redirecionamento determinado para {}: {}", user.getEmail(), determinedPath);

    return determinedPath;
  }

  // LÓGICA DE REGISTRO

  @Override
  @Transactional
  public UsuarioResponseDto registrarUsuario(RegistroRequestDto registroRequest) {
    logger.info("Tentativa de registro de usuário: {}", registroRequest.email());

    // Valida se o e-mail já está em uso
    if (userRepository.findByEmail(registroRequest.email()).isPresent()) {
      logger.warn("Falha ao registrar usuário: E-mail já em uso: {}", registroRequest.email());
      // Código 409 Conflict: o recurso (e-mail) já existe.
      throw new EmailAlreadyExistsException();
    }

    // Valida se o username já está em uso
    if (registroRequest.username() != null
        && userRepository.findByUsername(registroRequest.username()).isPresent()) {
      logger.warn("Falha ao registrar usuário: Username já em uso: {}", registroRequest.username());
      throw new UsernameAlreadyExistsException();
    }

    // 1. Converte as roles de String para o Enum Perfis
    List<Perfis> perfisConvertidos = registroRequest.roles().stream().map(roleString -> {
      try {
        return Perfis.valueOf(roleString.toUpperCase());
      } catch (IllegalArgumentException e) {
        logger.error("Perfil inválido recebido: {}", roleString);
        // Código 400 Bad Request: dados da requisição inválidos.
        throw new InvalidRoleException();
      }
    }).toList();

    // Cria e configura a entidade Usuario
    Usuario usuario = new Usuario();
    usuario.setNome(registroRequest.nome());
    usuario.setEmail(registroRequest.email());
    usuario.setSenha(passwordEncoder.encode(registroRequest.senha()));
    // Novo usuário deve ser forçado a trocar a senha no primeiro login
    usuario.setMustChangePassword(true);
    usuario.setRoles(perfisConvertidos);
    usuario.setUsername(registroRequest.username());

    usuario = userRepository.save(usuario);

    logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());

  return new UsuarioResponseDto(usuario.getNome(), usuario.getEmail(), usuario.getUsername(),
    usuario.getRoles());
  }

  // LÓGICA DE REDEFINIÇÃO DE SENHA (E-MAIL)

  @Override
  @Transactional
  public void sendPasswordReset(Usuario user) {
    logger.info("Iniciando envio de e-mail de redefinição para {}", user.getEmail());

    // 1. Geração e Salvamento do Token
    String token = UUID.randomUUID().toString();
    user.setPasswordResetToken(token);
    user.setPasswordResetExpires(Instant.now().plus(1, ChronoUnit.HOURS));
    userRepository.save(user);

    // 2. Geração do Link
    String link = resetPasswordUrl + "?token=" + token + "&email=" + user.getEmail();

    // 3. Envio do E-mail
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(user.getEmail());
    msg.setSubject("Redefinição de senha - IFala");
    msg.setText("Olá,\n\nAcesse o link abaixo para redefinir sua senha:\n" + link
        + "\n\nSe não solicitou, ignore esta mensagem.");

    try {
      mailSender.send(msg);
      logger.info("E-mail de redefinição de senha enviado com sucesso para: {}", user.getEmail());
    } catch (Exception e) {
      logger.error("Falha ao enviar e-mail de redefinição para {}: {}", user.getEmail(),
          e.getMessage(), e);
      // Código 500 Internal Server Error: falha no serviço interno de e-mail.
      throw new EmailServiceException();
    }
  }

  // LÓGICA DE LOGIN

  @Override
  public LoginResponseDto login(LoginRequestDto req) {
    String identifier = req.getEmail() != null ? req.getEmail() : req.getUsername();
    logger.info("Tentativa de login recebida para: {}", identifier);

    // 1. Tenta autenticar via Spring Security
    try {
      // Usa o identifier (email ou username) e a senha
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(identifier, req.getPassword()));
      logger.info("Autenticação via Spring Security bem-sucedida para: {}", identifier);

    } catch (AuthenticationException e) {
      logger.warn("Falha de autenticação (credenciais inválidas) para: {}", identifier, e);
      // Mapeia a exceção do Spring Security para a sua exceção personalizada
      throw new InvalidCredentialsException(); // 401 Unauthorized
    }

    // 2. Busca o usuário no repositório para a lógica de negócio
    // (mustChangePassword, roles, etc.)
    Optional<Usuario> userOpt = req.getEmail() != null ? userRepository.findByEmail(req.getEmail())
        : userRepository.findByUsername(req.getUsername());

    if (userOpt.isEmpty()) {
      logger.error("Usuário autenticado não encontrado no DB! Email/Username: {}", identifier);
      throw new InternalAuthException();
    }

    Usuario user = userOpt.get();

    logger.info("Login bem-sucedido para o usuário: {}", user.getEmail());

    // 3. Se a senha precisar ser trocada, força a redefinição
  if (user.isMustChangePassword()) {
      logger.info("Usuário {} deve alterar a senha. Chamando lógica de redefinição.",
          user.getEmail());
    sendPasswordReset(user);

    return new LoginResponseDto(null, null, null, null, true, null,
      "É necessário alterar a senha. Um e-mail de redefinição foi enviado.");
    }

    // 4. Gera e retorna tokens
  TokenDataDto tokenData = jwtUtil.generateToken(user.getEmail());
    // Cria e persiste um refresh token rotativo (UUID) no banco
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Login finalizado para {}. Redirecionamento: {}", user.getEmail(), redirect);

  return new LoginResponseDto(tokenData.token(), tokenData.issuedAt(), tokenData.expirationTime(),
    refreshToken.getToken(), false, redirect, null);
  }

  // LÓGICA DE MUDANÇA DE SENHA

  @Override
  @Transactional
  public LoginResponseDto changePassword(MudarSenhaRequestDto req) {
    logger.info("Tentativa de mudança de senha recebida para o e-mail: {}", req.email());

    Optional<Usuario> userOpt = userRepository.findByEmail(req.email());
    if (userOpt.isEmpty()) {
      logger.warn("Falha ao redefinir senha: Usuário não encontrado para o e-mail: {}",
          req.email());
      throw new UserNotFoundException();
    }

    Usuario user = userOpt.get();
    logger.debug("Usuário {} encontrado. Verificando credenciais para redefinição...",
        user.getEmail());

    // Decide se a mudança é via Token (redefinição) ou Senha Atual (pós-login)
    if (req.token() != null && !req.token().isBlank()) {
      String token = req.token();
      if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
        logger.warn("Token inválido para usuário {}.", user.getEmail());
        throw new InvalidTokenException();
      }
      if (user.getPasswordResetExpires() == null
          || user.getPasswordResetExpires().isBefore(Instant.now())) {
        logger.warn("Token expirado para usuário {}.", user.getEmail());
        throw new TokenExpiredException();
      }
    } else {
      if (!passwordEncoder.matches(req.currentPassword(), user.getSenha())) {
        logger.warn("Falha ao redefinir senha para {}: Senha atual incorreta.", user.getEmail());
        throw new PasswordMismatchException();
      }
    }

    // Aplica a nova senha e limpa os campos de token
    user.setSenha(passwordEncoder.encode(req.newPassword()));
    user.setMustChangePassword(false);
    user.setPasswordResetToken(null);
    user.setPasswordResetExpires(null);
    userRepository.save(user);

    logger.info("Senha alterada com sucesso para o usuário: {}", user.getEmail());

    // Gera novo token de acesso após a mudança de senha
  TokenDataDto tokenData = jwtUtil.generateToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Redefinição de senha finalizada. Novo redirecionamento: {}", redirect);

  return new LoginResponseDto(tokenData.token(), tokenData.issuedAt(), tokenData.expirationTime(),
    null, false, redirect, null);
  }

  // LÓGICA DE REFRESH TOKEN
  @Override
  @Transactional
  public LoginResponseDto refreshToken(RefreshTokenRequestDto req) {
    String oldRefreshToken = req.token();
    logger.info("Tentativa de refresh de token recebida (fluxo opaco DB)");

    // 1. Busca e valida o refresh token
    RefreshToken stored = refreshTokenService.findByToken(oldRefreshToken).orElseThrow(() -> {
      logger.warn("Refresh token não encontrado no banco: {}", oldRefreshToken);
      // Lança RefreshTokenException para garantir que o GlobalExceptionHandler envie
      // o cookie de
      // logout
      return new RefreshTokenException("Refresh token inválido. Faça login novamente.", 401);
    });

    // 2. Verifica expiração (o serviço deve lançar uma RuntimeException se
    // expirado)
    // Se o verifyExpiration lançar exceção, o @Transactional fará o rollback.
    refreshTokenService.verifyExpiration(stored);
    try {
      refreshTokenService.deleteByToken(oldRefreshToken);
      logger.info("Refresh token antigo removido com sucesso: {}", oldRefreshToken);
    } catch (RefreshTokenException e) {
      logger.warn("Delete de refresh token falhou (token inválido/já usado): {}", oldRefreshToken);
      throw e;
    } catch (Exception e) {
      logger.error("Falha irrecuperável ao deletar refresh token antigo: {}", oldRefreshToken, e);
      throw new RefreshTokenException("Erro de segurança ao processar token. Faça login novamente.",
          500);
    }

    // 4. Gera novo access token e NOVO refresh token
    Usuario user = stored.getUsuario();
  TokenDataDto accessTokenData = jwtUtil.generateToken(user.getEmail());

    // A criação do novo token DEVE acontecer *APÓS* a deleção do antigo
    // Preserva o expiry absoluto do token antigo para que a rotação não estenda
    // a validade além do tempo originalmente emitido.
    RefreshToken newRefreshToken =
        refreshTokenService.createRefreshToken(user.getEmail(), stored.getDataExpiracao());
    String redirect = determineRedirect(user);

    logger.info("Tokens renovados com sucesso para: {}", user.getEmail());

  return new LoginResponseDto(accessTokenData.token(), accessTokenData.issuedAt(),
    accessTokenData.expirationTime(), newRefreshToken.getToken(), false, redirect,
    "Tokens renovados com sucesso.");
  }

  // LÓGICA DE LOGOUT

  @Override
  public void logout(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new MissingAuthorizationHeaderException();
    }
    String token = header.substring(7);

    try {
      // Extrai a data de expiração e coloca o token na blacklist
      Instant expiresAt = jwtUtil.extractClaims(token).getExpiration().toInstant();
      tokenBlacklistService.blacklistToken(token, expiresAt);
      logger.info("Token de acesso colocado na blacklist para logout.");
    } catch (Exception e) {
      logger.warn("Falha ao processar e colocar JWT na blacklist: {}", e.getMessage());
      // Continua o fluxo, pois o principal no logout é revogar a sessão (refresh
      // token)
    }

    // Revoga o refresh token vindo via cookie (se houver)
    if (request.getCookies() != null) {
      for (jakarta.servlet.http.Cookie c : request.getCookies()) {
        if ("refreshToken".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
          try {
            refreshTokenService.deleteByToken(c.getValue());
            logger.info("Refresh token do cookie revogado com sucesso.");
          } catch (Exception e) {
            logger.debug("Falha ao deletar refresh token do cookie durante logout: {}",
                e.getMessage());
          }
          // A revogação deve ocorrer, mas o erro não impede o logout
        }
      }
    }
  }
}
