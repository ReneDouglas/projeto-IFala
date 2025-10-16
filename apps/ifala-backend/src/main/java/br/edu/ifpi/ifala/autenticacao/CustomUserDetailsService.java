package br.edu.ifpi.ifala.autenticacao;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository usuarioRepository;

  public CustomUserDetailsService(UserRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Busca a entidade Usuario pelo email
    Usuario usuario = usuarioRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

    // Converte a entidade em UserDetails do Spring Security
    return User.builder().username(usuario.getEmail()) // login será o email
        .password(usuario.getPassword()) // senha hasheada
        .authorities(
            usuario.getRoles().stream().map(role -> "ROLE_" + role.name()).toArray(String[]::new))
        .build();
  }
}
