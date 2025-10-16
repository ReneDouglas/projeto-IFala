package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.security.JwtUtil;
import br.edu.ifpi.ifala.autenticacao.dto.LoginResponseDto;
import br.edu.ifpi.ifala.autenticacao.dto.LoginRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.ChangePasswordRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final PasswordResetService passwordResetService;

  public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, PasswordResetService passwordResetService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.passwordResetService = passwordResetService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {
    // Busca o usuário pelo e-mail
    var userOpt = userRepository.findByEmail(req.getEmail());
    if (userOpt.isEmpty())
      return ResponseEntity.status(401).build();

    var user = userOpt.get();
    // Verifica a senha atual
    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      return ResponseEntity.status(401).build();
    }

    // isMustChangePassword() é um getter gerado pelo @Data/Lombok na classe User
    if (user.isMustChangePassword()) {
      // Envia email de redefinição de senha no primeiro acesso
      passwordResetService.sendPasswordReset(user);
      // Não emite JWT ainda, instrui frontend a forçar mudança
      return ResponseEntity.ok(new LoginResponseDto(null, true, null));
    }

    String token = jwtUtil.generateToken(user.getUsername());
    String redirect = determineRedirect(user);
    return ResponseEntity.ok(new LoginResponseDto(token, false, redirect));
  }

  @PostMapping("/redefinir-senha")
  public ResponseEntity<LoginResponseDto> changePassword(
      @RequestBody ChangePasswordRequestDto req) {

    var userOpt = userRepository.findByEmail(req.getEmail());
    if (userOpt.isEmpty())
      return ResponseEntity.status(404).build();

    var user = userOpt.get();
    // Verifica a senha atual
    if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
      return ResponseEntity.status(401).build();
    }

    // Atualiza a senha e o flag de mudança
    // setPassword() e setMustChangePassword() são setters gerados pelo @Data/Lombok
    user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    user.setMustChangePassword(false);
    userRepository.save(user);

    String token = jwtUtil.generateToken(user.getUsername());
    String redirect = determineRedirect(user);

    return ResponseEntity.ok(new LoginResponseDto(token, false, redirect));
  }

  // Seção determineRedirect
  private String determineRedirect(Usuario user) {
    // getRoles() é um getter gerado pelo @Data/Lombok na classe User
    return user.getRoles().stream().map(Enum::name).map(String::toLowerCase)
        .filter(r -> r.equals("admin") || r.equals("gestor_institucional")).findFirst()
        .map(r -> switch (r) {
          case "admin" -> "/admin/dashboard";
          case "gestor_institucional" -> "/gestor/home";
          default -> "/";
        }).orElse("/");
  }
}
