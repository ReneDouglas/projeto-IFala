package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.security.JwtUtil;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.MudarSenhaRequestDto;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final UsuarioRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final RedefinirSenhaService passwordResetService;
  private final br.edu.ifpi.ifala.security.TokenBlacklistService tokenBlacklistService;
  private final JdbcTemplate jdbcTemplate;

  public AuthController(UsuarioRepository userRepository, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, RedefinirSenhaService passwordResetService,
      br.edu.ifpi.ifala.security.TokenBlacklistService tokenBlacklistService,
      JdbcTemplate jdbcTemplate) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.passwordResetService = passwordResetService;
    this.tokenBlacklistService = tokenBlacklistService;
    this.jdbcTemplate = jdbcTemplate;
  }


  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {
    logger.info("Tentativa de login recebida para o e-mail: {}", req.getEmail());

    var userOpt = userRepository.findByEmail(req.getEmail());
    if (userOpt.isEmpty()) {
      logger.warn("Falha de login: Usuário não encontrado para o e-mail: {}", req.getEmail());
      return ResponseEntity.status(401).build();
    }

    var user = userOpt.get();
    logger.debug("Usuário encontrado: {}. Verificando senha...", user.getNome());

    if (!passwordEncoder.matches(req.getPassword(), user.getSenha())) {
      logger.warn("Falha de login para o usuário {}: Senha incorreta.", user.getEmail());
      return ResponseEntity.status(401).build();
    }

    logger.info("Login bem-sucedido para o usuário: {}", user.getEmail());

    if (user.isMustChangePassword()) {
      logger.info("Usuário {} deve alterar a senha. Enviando e-mail de redefinição.",
          user.getEmail());

      passwordResetService.sendPasswordReset(user);

      return ResponseEntity.ok(new LoginResponseDto(null, true, null,
          "É necessário alterar a senha. Um e-mail de redefinição foi enviado."));
    }

    String token = jwtUtil.generateToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Login finalizado para {}. Redirecionamento: {}", user.getEmail(), redirect);

    return ResponseEntity.ok(new LoginResponseDto(token, false, redirect, null));
  }

  @PostMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDto> changePassword(@RequestBody MudarSenhaRequestDto req) {

    logger.info("Tentativa de mudança de senha recebida para o e-mail: {}", req.getEmail());

    var userOpt = userRepository.findByEmail(req.getEmail());
    if (userOpt.isEmpty()) {
      logger.warn("Falha ao redefinir senha: Usuário não encontrado para o e-mail: {}",
          req.getEmail());
      return ResponseEntity.status(404).body(new LoginResponseDto(null, false, null,
          "Usuário não encontrado para o e-mail informado."));
    }

    var user = userOpt.get();
    logger.debug("Usuário {} encontrado. Verificando credenciais para redefinição...",
        user.getEmail());

    // Se um token foi fornecido, valida o token enviado por e-mail
    if (req.getToken() != null && !req.getToken().isBlank()) {
      String token = req.getToken();
      if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
        logger.warn("Token inválido para usuário {}.", user.getEmail());
        return ResponseEntity.status(401).body(
            new LoginResponseDto(null, false, null, "Token inválido para redefinição de senha."));
      }
      if (user.getPasswordResetExpires() == null
          || user.getPasswordResetExpires().isBefore(java.time.Instant.now())) {
        logger.warn("Token expirado para usuário {}.", user.getEmail());
        return ResponseEntity.status(401).body(
            new LoginResponseDto(null, false, null, "Token expirado para redefinição de senha."));
      }
      // token válido -> prosseguir com alteração
    } else {
      // fallback: checa senha atual
      if (!passwordEncoder.matches(req.getCurrentPassword(), user.getSenha())) {
        logger.warn("Falha ao redefinir senha para {}: Senha atual incorreta.", user.getEmail());
        return ResponseEntity.status(401)
            .body(new LoginResponseDto(null, false, null, "Senha atual incorreta."));
      }
    }

    user.setSenha(passwordEncoder.encode(req.getNewPassword()));
    user.setMustChangePassword(false);
    user.setPasswordResetToken(null);
    user.setPasswordResetExpires(null);
    userRepository.save(user);

    logger.info("Senha alterada com sucesso para o usuário: {}", user.getEmail());

    String token = jwtUtil.generateToken(user.getEmail());
    String redirect = determineRedirect(user);

    logger.info("Redefinição de senha finalizada. Novo redirecionamento: {}", redirect);

    return ResponseEntity.ok(new LoginResponseDto(token, false, redirect, null));
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
    logger.info("Tentativa de registro de usuário: {}", registroRequest.email());

    // 1. Checa por conflito de E-mail
    if (userRepository.findByEmail(registroRequest.email()).isPresent()) {
      logger.warn("Falha ao registrar usuário: E-mail já em uso: {}", registroRequest.email());
      return ResponseEntity.status(409)
          .body("E-mail já cadastrado. Utilize outro e-mail ou recupere a senha.");
    }

    Usuario usuario = new Usuario();
    usuario.setNome(registroRequest.nome());
    usuario.setEmail(registroRequest.email());
    usuario.setSenha(passwordEncoder.encode(registroRequest.senha()));
    usuario.setMustChangePassword(true);

    var roles = registroRequest.roles();

    usuario.setRoles(null);
    userRepository.save(usuario);

    if (roles != null && !roles.isEmpty()) {
      final String sql =
          "INSERT INTO usuarios_perfil (usuarios_id, perfil) VALUES (?, ?::perfis_enum) ON CONFLICT (usuarios_id, perfil) DO NOTHING";
      for (Perfis perfil : roles) {
        final String perfilValue = perfil.name().toLowerCase();
        jdbcTemplate.update(connection -> {
          var ps = connection.prepareStatement(sql);
          ps.setLong(1, usuario.getId());
          ps.setString(2, perfilValue);
          return ps;
        });
      }
      usuario.setRoles(roles);
    }
    logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());
    return ResponseEntity.status(201).body(usuario);
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
