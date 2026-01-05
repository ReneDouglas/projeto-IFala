package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.TokenDataDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioDetalheResponseDTO;
import br.edu.ifpi.ifala.autenticacao.dto.AtualizarUsuarioRequestDTO;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import br.edu.ifpi.ifala.security.JwtUtil;
import br.edu.ifpi.ifala.security.TokenBlacklistService;
import br.edu.ifpi.ifala.notificacao.NotificacaoExternaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de autenticação.
 * 
 * @author Phaola
 * @author Jhonatas G Ribeiro
 */

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UsuarioRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final TokenBlacklistService tokenBlacklistService;
  private final RefreshTokenService refreshTokenService;
  private final NotificacaoExternaService notificacaoExternaService;

  private final AuthenticationManager authenticationManager;

  @Value("${app.frontend.reset-password-url:http://localhost:5173/redefinir-senha}")
  private String resetPasswordUrl;

  public AuthServiceImpl(UsuarioRepository userRepository, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService,
      RefreshTokenService refreshTokenService, NotificacaoExternaService notificacaoExternaService,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.tokenBlacklistService = tokenBlacklistService;
    this.refreshTokenService = refreshTokenService;
    this.notificacaoExternaService = notificacaoExternaService;
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
  public UsuarioResponseDTO registrarUsuario(RegistroRequestDTO registroRequest) {
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

    return new UsuarioResponseDTO(usuario.getNome(), usuario.getEmail(), usuario.getUsername(),
        usuario.getRoles());
  }

  // LÓGICA DE REDEFINIÇÃO DE SENHA (E-MAIL)

  @Override
  @Transactional
  public void sendPasswordReset(Usuario user) {
    logger.info("Iniciando processo de redefinição de senha para {}", user.getEmail());

    // 1. Geração e Salvamento do Token (Responsabilidade de Segurança)
    String token = UUID.randomUUID().toString();
    user.setPasswordResetToken(token);
    user.setPasswordResetExpires(Instant.now().plus(1, ChronoUnit.HOURS));
    userRepository.save(user);

    // 2. Geração do Link (Responsabilidade de Segurança)
    String link = resetPasswordUrl + "/" + token;

    // 3. Delegação do Envio de E-mail (Responsabilidade de Comunicação)
    try {
      notificacaoExternaService.enviarEmailRedefinicaoSenha(user.getEmail(), link);
      logger.info("E-mail de redefinição de senha delegado com sucesso para: {}", user.getEmail());
    } catch (Exception e) {
      logger.error("Falha ao enviar e-mail de redefinição para {}: {}", user.getEmail(),
          e.getMessage(), e);
      // Código 500 Internal Server Error: falha no serviço interno de e-mail.
      throw new EmailServiceException();
    }
  }

  // LÓGICA DE LOGIN

  @Override
  public LoginResponseDTO login(LoginRequestDTO req) {
    String identifier = req.getEmail() != null ? req.getEmail() : req.getUsername();
    logger.info("Tentativa de login recebida para: {}", identifier);

    // 1. Tenta autenticar via Spring Security
    try {
      // Usa o identifier (email ou username) e a senha
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(identifier, req.getPassword()));
      logger.info("Autenticação via Spring Security bem-sucedida para: {}", identifier);

    } catch (org.springframework.security.authentication.BadCredentialsException e) {
      // Tratamento para credenciais inválidas: log reduzido sem stacktrace
      logger.warn("Falha de autenticação: credenciais inválidas para: {}", identifier);
      throw new InvalidCredentialsException(); // 401 Unauthorized
    } catch (AuthenticationException e) {
      logger.warn("Falha de autenticação para {}: {}", identifier, e.getMessage());
      throw new InvalidCredentialsException();
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

      return new LoginResponseDTO(null, null, null, null, true, null,
          "É necessário alterar a senha. Um e-mail de redefinição foi enviado.");
    }

    // 4. Gera e retorna tokens
    TokenDataDTO tokenData = jwtUtil.generateTokenWithUserData(user);
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
    } else if (req.currentPassword() != null && !req.currentPassword().isBlank()) {
      // Mudança de senha com senha atual (usuário logado)
      if (!passwordEncoder.matches(req.currentPassword(), user.getSenha())) {
        logger.warn("Falha ao redefinir senha para {}: Senha atual incorreta.", user.getEmail());
        throw new PasswordMismatchException();
      }
    } else {
      // Solicitação de redefinição de senha (enviar email)
      logger.info("Solicitação de redefinição de senha para: {}", user.getEmail());
      sendPasswordReset(user);

      return new LoginResponseDTO(null, null, null, null, false, null,
          "Email de redefinição enviado com sucesso.");
    }

    // Aplica a nova senha e limpa os campos de token
    user.setSenha(passwordEncoder.encode(req.newPassword()));
    user.setMustChangePassword(false);
    user.setPasswordResetToken(null);
    user.setPasswordResetExpires(null);
    userRepository.save(user);

    logger.info("Senha alterada com sucesso para o usuário: {}", user.getEmail());

    // Gera novo token de acesso após a mudança de senha
    TokenDataDTO tokenData = jwtUtil.generateTokenWithUserData(user);
    String redirect = determineRedirect(user);

    logger.info("Redefinição de senha finalizada. Novo redirecionamento: {}", redirect);

    return new LoginResponseDTO(tokenData.token(), tokenData.issuedAt(), tokenData.expirationTime(),
        null, false, redirect, null);
  }

  // LÓGICA DE REFRESH TOKEN
  @Override
  @Transactional
  public LoginResponseDTO refreshToken(RefreshTokenRequestDTO req) {
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
    TokenDataDTO accessTokenData = jwtUtil.generateTokenWithUserData(user);

    // A criação do novo token DEVE acontecer *APÓS* a deleção do antigo
    // Preserva o expiry absoluto do token antigo para que a rotação não estenda
    // a validade além do tempo originalmente emitido.
    RefreshToken newRefreshToken =
        refreshTokenService.createRefreshToken(user.getEmail(), stored.getDataExpiracao());
    String redirect = determineRedirect(user);

    logger.info("Tokens renovados com sucesso para: {}", user.getEmail());

    return new LoginResponseDTO(accessTokenData.token(), accessTokenData.issuedAt(),
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

  @Override
  public String getEmailByResetToken(String token) {
    if (token == null || token.isBlank()) {
      logger.warn("Token de redefinição vazio ou nulo.");
      throw new InvalidTokenException();
    }

    Optional<Usuario> optUser = userRepository.findByPasswordResetToken(token);
    if (optUser.isEmpty()) {
      logger.warn("Token de redefinição inválido.");
      throw new InvalidTokenException();
    }

    Usuario user = optUser.get();
    if (user.getPasswordResetExpires() == null
        || user.getPasswordResetExpires().isBefore(Instant.now())) {
      logger.warn("Token de redefinição expirado para usuário {}.", user.getEmail());
      throw new TokenExpiredException();
    }

    return user.getEmail();
  }

  @Override
  public Page<UsuarioDetalheResponseDTO> listarUsuario(Pageable pageable, String search,
      String role, Boolean mustChangePassword) {
    logger.info("Filtrando usuários");


    Specification<Usuario> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // filtra Search (Nome, Email ou Username)
      if (search != null && !search.trim().isEmpty()) {
        String term = "%" + search.trim().toLowerCase() + "%";
        predicates.add(cb.or(cb.like(cb.lower(root.get("nome")), term),
            cb.like(cb.lower(root.get("email")), term),
            cb.like(cb.lower(root.get("username")), term)));
      }

      if (role != null && !role.isEmpty()) {
        predicates.add(cb.equal(root.get("role"), role));
      }

      if (mustChangePassword != null) {
        predicates.add(cb.equal(root.get("mustChangePassword"), mustChangePassword));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Page<Usuario> usuariosPage = userRepository.findAll(spec, pageable);

    return usuariosPage.map(this::convertToUsuarioDetalheDTO);
  }

  @Override
  public UsuarioDetalheResponseDTO buscarUsuarioPorId(Long id) {
    logger.info("Buscando usuário com id: {}", id);
    Usuario usuario = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuário com id " + id + " não encontrado."));
    return convertToUsuarioDetalheDTO(usuario);
  }

  @Override
  @Transactional
  public UsuarioDetalheResponseDTO atualizarUsuario(Long id,
      AtualizarUsuarioRequestDTO atualizarUsuarioRequestDTO) {
    logger.info("Atualizando usuário com id");

    Usuario usuario = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
        "Usuário com id " + id + " não encontrado para atualização."));

    // Valida se o novo e-mail
    if (atualizarUsuarioRequestDTO.email() != null
        && !atualizarUsuarioRequestDTO.email().equals(usuario.getEmail())) {
      userRepository.findByEmail(atualizarUsuarioRequestDTO.email()).ifPresent(existingUser -> {
        if (!existingUser.getId().equals(id)) {
          throw new EmailAlreadyExistsException(
              "O e-mail " + atualizarUsuarioRequestDTO.email() + " já está em uso.");
        }
      });
      usuario.setEmail(atualizarUsuarioRequestDTO.email());
    }

    // Valida se o novo username
    if (atualizarUsuarioRequestDTO.username() != null
        && !atualizarUsuarioRequestDTO.username().equals(usuario.getUsername())) {
      userRepository.findByUsername(atualizarUsuarioRequestDTO.username())
          .ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
              throw new UsernameAlreadyExistsException(
                  "O username " + atualizarUsuarioRequestDTO.username() + " já está em uso.");
            }
          });
      usuario.setUsername(atualizarUsuarioRequestDTO.username());
    }

    // Atualiza os outros campos
    usuario.setNome(atualizarUsuarioRequestDTO.nome());
    usuario.setMustChangePassword(atualizarUsuarioRequestDTO.mustChangePassword());

    if (atualizarUsuarioRequestDTO.roles() != null
        && !atualizarUsuarioRequestDTO.roles().isEmpty()) {
      List<Perfis> perfisConvertidos =
          atualizarUsuarioRequestDTO.roles().stream().map(roleString -> {
            try {
              return Perfis.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
              throw new InvalidRoleException("O perfil '" + roleString + "' é inválido.");
            }
          }).toList();
      usuario.setRoles(perfisConvertidos);
    }

    Usuario usuarioAtualizado = userRepository.save(usuario);
    logger.info("Usuário com id atualizado com sucesso.");

    return convertToUsuarioDetalheDTO(usuarioAtualizado);
  }

  /**
   * Converte uma entidade Usuario para seu DTO de detalhe.
   *
   * @param usuario A entidade a ser convertida.
   * @return O DTO com os detalhes do usuário.
   */
  private UsuarioDetalheResponseDTO convertToUsuarioDetalheDTO(Usuario usuario) {
    return new UsuarioDetalheResponseDTO(usuario.getId(), usuario.getNome(), usuario.getUsername(),
        usuario.getEmail(), usuario.getRoles(), usuario.isMustChangePassword());
  }
}
