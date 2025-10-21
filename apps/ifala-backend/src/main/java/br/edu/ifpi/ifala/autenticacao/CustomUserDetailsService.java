package br.edu.ifpi.ifala.autenticacao;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

    return User.builder().username(usuario.getEmail()).password(usuario.getSenha())
        .authorities(usuario.getRoles().stream().map(role -> role.name()).toArray(String[]::new))
        .build();
  }
}
