package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.security.JwtUtil;
import br.edu.ifpi.ifala.security.TokenBlacklistService;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.RefreshTokenRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.TokenDataDto;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDto;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final UsuarioRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final RedefinirSenhaService passwordResetService;
  private final TokenBlacklistService tokenBlacklistService;
  private final RegistroService autenticacaoService;


  public AuthController(UsuarioRepository userRepository, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, RedefinirSenhaService passwordResetService,
      TokenBlacklistService tokenBlacklistService, RegistroService autenticacaoService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.passwordResetService = passwordResetService;
    this.tokenBlacklistService = tokenBlacklistService;
    this.autenticacaoService = autenticacaoService;
  }


  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
    String identifier = req.email() != null ? req.email() : req.username();
    logger.info("Tentativa de login recebida para: {}", identifier);

    var userOpt = req.email() != null ? userRepository.findByEmail(req.email())
        : userRepository.findByUsername(req.username());

    if (userOpt.isEmpty()) {
      logger.warn("Falha de login: Usuário não encontrado para: {}", identifier);
      return ResponseEntity.status(401).build();
    }

    var user = userOpt.get();
    logger.debug("Usuário encontrado: {}. Verificando senha...", user.getNome());

    if (!passwordEncoder.matches(req.password(), user.getSenha())) {
      logger.warn("Falha de login para o usuário {}: Senha incorreta.", user.getEmail());
      return ResponseEntity.status(401).build();
    }

    logger.info("Login bem-sucedido para o usuário: {}", user.getEmail());

    if (user.isMustChangePassword()) {
      logger.info("Usuário {} deve alterar a senha. Enviando e-mail de redefinição.",
          user.getEmail());

      passwordResetService.sendPasswordReset(user);

      return ResponseEntity.ok(new LoginResponseDto(null, null, null, null, true, null,
          "É necessário alterar a senha. Um e-mail de redefinição foi enviado."));
    }

    TokenDataDto tokenData = jwtUtil.generateToken(user.getEmail());
    TokenDataDto refreshTokenData = jwtUtil.generateToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Login finalizado para {}. Redirecionamento: {}", user.getEmail(), redirect);

    return ResponseEntity.ok(new LoginResponseDto(tokenData.token(), tokenData.issuedAt(),
        tokenData.expirationTime(), refreshTokenData.token(), false, redirect, null));
  }

  @PostMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDto> changePassword(
      @Valid @RequestBody MudarSenhaRequestDto req) {

    logger.info("Tentativa de mudança de senha recebida para o e-mail: {}", req.email());

    var userOpt = userRepository.findByEmail(req.email());
    if (userOpt.isEmpty()) {
      logger.warn("Falha ao redefinir senha: Usuário não encontrado para o e-mail: {}",
          req.email());
      return ResponseEntity.status(404).body(new LoginResponseDto(null, null, null, null, false,
          null, "Usuário não encontrado para o e-mail informado."));
    }

    var user = userOpt.get();
    logger.debug("Usuário {} encontrado. Verificando credenciais para redefinição...",
        user.getEmail());

    // Se um token foi fornecido, valida o token enviado por e-mail
    if (req.token() != null && !req.token().isBlank()) {
      String token = req.token();
      if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
        logger.warn("Token inválido para usuário {}.", user.getEmail());
        return ResponseEntity.status(401).body(new LoginResponseDto(null, null, null, null, false,
            null, "Token inválido para redefinição de senha."));
      }
      if (user.getPasswordResetExpires() == null
          || user.getPasswordResetExpires().isBefore(java.time.Instant.now())) {
        logger.warn("Token expirado para usuário {}.", user.getEmail());
        return ResponseEntity.status(401).body(new LoginResponseDto(null, null, null, null, false,
            null, "Token expirado para redefinição de senha."));
      }
      // token válido -> prosseguir com alteração
    } else {
      // fallback: checa senha atual
      if (!passwordEncoder.matches(req.currentPassword(), user.getSenha())) {
        logger.warn("Falha ao redefinir senha para {}: Senha atual incorreta.", user.getEmail());
        return ResponseEntity.status(401).body(
            new LoginResponseDto(null, null, null, null, false, null, "Senha atual incorreta."));
      }
    }

    user.setSenha(passwordEncoder.encode(req.newPassword()));
    user.setMustChangePassword(false);
    // limpa token e expiry após redefinição bem-sucedida
    user.setPasswordResetToken(null);
    user.setPasswordResetExpires(null);
    userRepository.save(user);

    logger.info("Senha alterada com sucesso para o usuário: {}", user.getEmail());

    TokenDataDto tokenData = jwtUtil.generateToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Redefinição de senha finalizada. Novo redirecionamento: {}", redirect);

    TokenDataDto refreshTokenData = jwtUtil.generateToken(user.getEmail());
    return ResponseEntity.ok(new LoginResponseDto(tokenData.token(), tokenData.issuedAt(),
        tokenData.expirationTime(), refreshTokenData.token(), false, redirect, null));
  }

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

  @PostMapping("/admin/registrar-usuario")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequestDto registroRequest) {
    try {
      UsuarioResponseDto usuarioResponse = autenticacaoService.registrarUsuario(registroRequest);
      return ResponseEntity.status(201).body(usuarioResponse);
    } catch (IllegalArgumentException e) {
      logger.warn("Falha ao registrar usuário: {}", e.getMessage());
      return ResponseEntity.status(409).body(e.getMessage());
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto req) {
    String oldRefreshToken = req.token();
    logger.info("Tentativa de refresh de token recebida");

    // Verifica se o refresh token está na blacklist
    if (tokenBlacklistService.isBlacklisted(oldRefreshToken)) {
      logger.warn("Tentativa de refresh com token na blacklist");
      return ResponseEntity.status(401).body("Refresh token inválido ou expirado.");
    }

    // Valida se o refresh token pode ser usado para refresh
    if (!jwtUtil.canRefreshToken(oldRefreshToken)) {
      logger.warn("Refresh token inválido para refresh");
      return ResponseEntity.status(401).body("Refresh token inválido.");
    }

    try {
      // Extrai o username do refresh token (mesmo que expirado)
      String username = jwtUtil.extractUsernameFromExpiredToken(oldRefreshToken);
      logger.debug("Refresh token para usuário: {}", username);

      // Busca o usuário no banco
      var userOpt = userRepository.findByEmail(username);
      if (userOpt.isEmpty()) {
        logger.warn("Usuário não encontrado para refresh: {}", username);
        return ResponseEntity.status(401).body("Usuário não encontrado.");
      }

      var user = userOpt.get();

      // Gera novo access token
      TokenDataDto accessTokenData = jwtUtil.generateToken(user.getEmail());
      TokenDataDto refreshTokenData = jwtUtil.generateToken(user.getEmail());
      String redirect = determineRedirect(user);

      logger.info("Tokens renovados com sucesso para: {}", user.getEmail());

      // Adiciona o refresh token antigo à blacklist para evitar reuso
      try {
        var claims = jwtUtil.extractClaims(oldRefreshToken);
        java.util.Date exp = claims.getExpiration();
        java.time.Instant expiresAt = exp == null ? java.time.Instant.now() : exp.toInstant();
        tokenBlacklistService.blacklistToken(oldRefreshToken, expiresAt);
      } catch (io.jsonwebtoken.ExpiredJwtException e) {
        // Token já expirado, mas ainda adiciona à blacklist para segurança
        tokenBlacklistService.blacklistToken(oldRefreshToken,
            e.getClaims().getExpiration().toInstant());
      }

      return ResponseEntity.ok(new LoginResponseDto(accessTokenData.token(),
          accessTokenData.issuedAt(), accessTokenData.expirationTime(), refreshTokenData.token(),
          false, redirect, "Tokens renovados com sucesso."));

    } catch (Exception e) {
      logger.error("Erro ao processar refresh token", e);
      return ResponseEntity.status(500).body("Erro ao processar refresh token.");
    }
  }

  @PostMapping("/sair")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("Header Authorization ausente ou inválido.");
    }
    String token = header.substring(7);
    try {
      var claims = jwtUtil.extractClaims(token);
      java.util.Date exp = claims.getExpiration();
      java.time.Instant expiresAt = exp == null ? java.time.Instant.now() : exp.toInstant();
      tokenBlacklistService.blacklistToken(token, expiresAt);
      return ResponseEntity.ok("Logout realizado com sucesso.");
    } catch (Exception e) {
      return ResponseEntity.status(400).body("Token inválido.");
    }
  }
}
