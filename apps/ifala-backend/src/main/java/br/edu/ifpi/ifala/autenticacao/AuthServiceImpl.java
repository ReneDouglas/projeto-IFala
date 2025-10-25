package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.TokenDataDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Exceção personalizada para lidar com erros de autenticação e autorização,
 * permitindo retornar
 * códigos de status HTTP específicos.
 */
class AutenticacaoException extends RuntimeException {
  private final int statusCode;

  public AutenticacaoException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}

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
   * Determina o caminho de redirecionamento do usuário após o login, baseado em
   * seus perfis.
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
  public UsuarioResponseDTO registrarUsuario(RegistroRequestDTO registroRequest) {
    logger.info("Tentativa de registro de usuário: {}", registroRequest.email());

    // Valida se o e-mail já está em uso
    if (userRepository.findByEmail(registroRequest.email()).isPresent()) {
      logger.warn("Falha ao registrar usuário: E-mail já em uso: {}", registroRequest.email());
      // Código 409 Conflict: o recurso (e-mail) já existe.
      throw new AutenticacaoException(
          "E-mail já cadastrado. Utilize outro e-mail ou recupere a senha.", 409);
    }

    // Valida se o username já está em uso
    if (registroRequest.username() != null
        && userRepository.findByUsername(registroRequest.username()).isPresent()) {
      logger.warn("Falha ao registrar usuário: Username já em uso: {}", registroRequest.username());
      throw new AutenticacaoException("Username já cadastrado. Por favor, escolha outro.", 409);
    }

    // 1. Converte as roles de String para o Enum Perfis
    List<Perfis> perfisConvertidos = registroRequest.roles().stream().map(roleString -> {
      try {
        return Perfis.valueOf(roleString.toUpperCase());
      } catch (IllegalArgumentException e) {
        logger.error("Perfil inválido recebido: {}", roleString);
        // Código 400 Bad Request: dados da requisição inválidos.
        throw new AutenticacaoException("Perfil(s) fornecido(s) é(são) inválido(s).", 400);
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

    return new UsuarioResponseDTO(usuario.getNome(), usuario.getEmail(), usuario.getUsername(),
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
      throw new AutenticacaoException(
          "Falha ao enviar e-mail de redefinição de senha. Verifique as configurações de e-mail.",
          500);
    }
  }

  // LÓGICA DE LOGIN

  @Override
  public LoginResponseDTO login(LoginRequestDTO req) {
    String identifier = req.email() != null ? req.email() : req.username();
    logger.info("Tentativa de login recebida para: {}", identifier);

    // 1. Tenta autenticar via Spring Security
    try {
      // Usa o identifier (email ou username) e a senha
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(identifier, req.password()));
      logger.info("Autenticação via Spring Security bem-sucedida para: {}", identifier);

    } catch (AuthenticationException e) {
      logger.warn("Falha de autenticação (credenciais inválidas) para: {}", identifier, e);
      // Mapeia a exceção do Spring Security para a sua exceção personalizada
      throw new AutenticacaoException("Usuário ou senha inválidos.", 401); // 401 Unauthorized
    }

    // 2. Busca o usuário no repositório para a lógica de negócio
    // (mustChangePassword, roles, etc.)
    Optional<Usuario> userOpt = req.email() != null ? userRepository.findByEmail(req.email())
        : userRepository.findByUsername(req.username());

    if (userOpt.isEmpty()) {
      logger.error("Usuário autenticado não encontrado no DB! Email/Username: {}", identifier);
      throw new AutenticacaoException("Erro interno de autenticação.", 500);
    }

    Usuario user = userOpt.get();

    logger.info("Login bem-sucedido para o usuário: {}", user.getEmail());

    // 3. Se a senha precisar ser trocada, força a redefinição
    if (user.isMustChangePassword()) {
      logger.info("Usuário {} deve alterar a senha. Chamando lógica de redefinição.",
          user.getEmail());
      sendPasswordReset(user);

      return new LoginResponseDTO(null, null, null, null, true, null,
          "É necessário alterar a senha. Um e-mail de redefinição foi enviado.");
    }

    // 4. Gera e retorna tokens
    TokenDataDTO tokenData = jwtUtil.generateToken(user.getEmail());
    // Cria e persiste um refresh token rotativo (UUID) no banco
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Login finalizado para {}. Redirecionamento: {}", user.getEmail(), redirect);

    return new LoginResponseDTO(tokenData.token(), tokenData.issuedAt(), tokenData.expirationTime(),
        refreshToken.getToken(), false, redirect, null);
  }

  // LÓGICA DE MUDANÇA DE SENHA

  @Override
  @Transactional
  public LoginResponseDTO changePassword(MudarSenhaRequestDTO req) {
    logger.info("Tentativa de mudança de senha recebida para o e-mail: {}", req.email());

    Optional<Usuario> userOpt = userRepository.findByEmail(req.email());
    if (userOpt.isEmpty()) {
      logger.warn("Falha ao redefinir senha: Usuário não encontrado para o e-mail: {}",
          req.email());
      throw new AutenticacaoException("Usuário não encontrado para o e-mail informado.", 404);
    }

    Usuario user = userOpt.get();
    logger.debug("Usuário {} encontrado. Verificando credenciais para redefinição...",
        user.getEmail());

    // Decide se a mudança é via Token (redefinição) ou Senha Atual (pós-login)
    if (req.token() != null && !req.token().isBlank()) {
      String token = req.token();
      if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
        logger.warn("Token inválido para usuário {}.", user.getEmail());
        throw new AutenticacaoException("Token inválido para redefinição de senha.", 401);
      }
      if (user.getPasswordResetExpires() == null
          || user.getPasswordResetExpires().isBefore(Instant.now())) {
        logger.warn("Token expirado para usuário {}.", user.getEmail());
        throw new AutenticacaoException("Token expirado para redefinição de senha.", 401);
      }
    } else {
      if (!passwordEncoder.matches(req.currentPassword(), user.getSenha())) {
        logger.warn("Falha ao redefinir senha para {}: Senha atual incorreta.", user.getEmail());
        throw new AutenticacaoException("Senha atual incorreta.", 401);
      }
    }

    // Aplica a nova senha e limpa os campos de token
    user.setSenha(passwordEncoder.encode(req.newPassword()));
    user.setMustChangePassword(false);
    user.setPasswordResetToken(null);
    user.setPasswordResetExpires(null);
    userRepository.save(user);

    logger.info("Senha alterada com sucesso para o usuário: {}", user.getEmail());

    // Gera novos tokens de acesso após a mudança de senha
    TokenDataDTO tokenData = jwtUtil.generateToken(user.getEmail());
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Redefinição de senha finalizada. Novo redirecionamento: {}", redirect);

    return new LoginResponseDTO(tokenData.token(), tokenData.issuedAt(), tokenData.expirationTime(),
        refreshToken.getToken(), false, redirect, null);
  }

  // LÓGICA DE REFRESH TOKEN

  @Override
  public LoginResponseDTO refreshToken(RefreshTokenRequestDTO req) {
    String oldRefreshToken = req.token();
    logger.info("Tentativa de refresh de token recebida (fluxo opaco DB)");

    // Busca o refresh token no banco
    Optional<RefreshToken> existing = refreshTokenService.findByToken(oldRefreshToken);
    if (existing.isEmpty()) {
      logger.warn("Refresh token não encontrado no banco: {}", oldRefreshToken);
      throw new AutenticacaoException("Refresh token inválido.", 401);
    }

    RefreshToken stored = existing.get();

    // Verifica expiração do refresh token
    try {
      refreshTokenService.verifyExpiration(stored);
    } catch (RuntimeException e) {
      logger.warn("Refresh token expirado: {}", oldRefreshToken);
      // garante remoção do token expirado
      try {
        refreshTokenService.deleteByToken(oldRefreshToken);
      } catch (Exception ignored) {
      }
      throw new AutenticacaoException("Refresh token expirado. Faça login novamente.", 401);
    }

    // Usuário associado ao refresh token
    Usuario user = stored.getUsuario();

    try {
      // Gera novo access token (JWT) e novo refresh token (rotativo, salvo no DB)
      TokenDataDTO accessTokenData = jwtUtil.generateToken(user.getEmail());
      RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());
      String redirect = determineRedirect(user);

      logger.info("Tokens renovados com sucesso para: {}", user.getEmail());

      // Remove o refresh token antigo do banco
      try {
        refreshTokenService.deleteByToken(oldRefreshToken);
      } catch (Exception ignored) {
      }

      return new LoginResponseDTO(accessTokenData.token(), accessTokenData.issuedAt(),
          accessTokenData.expirationTime(), newRefreshToken.getToken(), false, redirect,
          "Tokens renovados com sucesso.");
    } catch (Exception e) {
      logger.error("Erro ao processar refresh token opaco", e);
      throw new AutenticacaoException("Erro ao processar refresh token.", 500);
    }
  }

  // LÓGICA DE LOGOUT

  @Override
  public void logout(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new AutenticacaoException("Header Authorization ausente ou inválido.", 400);
    }
    String token = header.substring(7);
    try {
      // Extrai a data de expiração e coloca o token na blacklist
      Instant expiresAt = jwtUtil.extractClaims(token).getExpiration().toInstant();
      tokenBlacklistService.blacklistToken(token, expiresAt);
      logger.info("Token de acesso colocado na blacklist para logout.");
      // Também tenta remover o refresh token vindo via cookie (se houver)
      if (request.getCookies() != null) {
        for (jakarta.servlet.http.Cookie c : request.getCookies()) {
          if ("refreshToken".equals(c.getName()) && c.getValue() != null
              && !c.getValue().isBlank()) {
            try {
              refreshTokenService.deleteByToken(c.getValue());
            } catch (Exception e) {
              logger.debug("Falha ao deletar refresh token do cookie durante logout: {}",
                  e.getMessage());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("Erro ao processar logout/blacklist de token", e);
      throw new AutenticacaoException("Token inválido.", 400);
    }
  }
}
