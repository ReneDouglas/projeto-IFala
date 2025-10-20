package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.dto.RegistroRequestDto;
import br.edu.ifpi.ifala.autenticacao.dto.UsuarioResponseDto;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de autenticação que encapsula a lógica de negócio relacionada a usuários e autenticação.
 */
@Service
public class RegistroService {
  private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

  private final UsuarioRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegistroService(UsuarioRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registra um novo usuário no sistema. O Hibernate automaticamente gerencia a
   * relação @ElementCollection e insere os perfis na tabela usuarios_perfil.
   *
   * @param registroRequest dados do usuário a ser registrado
   * @return DTO com informações não-sensíveis do usuário criado
   * @throws IllegalArgumentException se o e-mail já estiver em uso
   */
  @Transactional
  public UsuarioResponseDto registrarUsuario(RegistroRequestDto registroRequest) {
    logger.info("Tentativa de registro de usuário: {}", registroRequest.email());

    // Valida se o e-mail já está em uso
    if (userRepository.findByEmail(registroRequest.email()).isPresent()) {
      logger.warn("Falha ao registrar usuário: E-mail já em uso: {}", registroRequest.email());
      throw new IllegalArgumentException(
          "E-mail já cadastrado. Utilize outro e-mail ou recupere a senha.");
    }

    // 1. Converte a lista de Strings (ex: "admin") para o Enum Java (ex: Perfis.ADMIN)
    List<Perfis> perfisConvertidos = registroRequest.roles().stream().map(roleString -> {
      try {
        // Usa toUpperCase() pois o Enum Java (Perfis) é em caixa alta
        return Perfis.valueOf(roleString.toUpperCase());
      } catch (IllegalArgumentException e) {
        logger.error("Perfil inválido recebido: {}", roleString);
        throw new IllegalArgumentException("Perfil(s) fornecido(s) é(são) inválido(s).");
      }
    }).toList();

    // Cria a entidade Usuario
    Usuario usuario = new Usuario();
    usuario.setNome(registroRequest.nome());
    usuario.setEmail(registroRequest.email());
    usuario.setSenha(passwordEncoder.encode(registroRequest.senha()));
    usuario.setMustChangePassword(true);
    usuario.setRoles(perfisConvertidos);
    usuario.setUsername(registroRequest.username());

    usuario = userRepository.save(usuario);

    logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());

    return new UsuarioResponseDto(usuario.getNome(), usuario.getEmail(), usuario.getUsername(),
        usuario.getRoles());
  }
}
