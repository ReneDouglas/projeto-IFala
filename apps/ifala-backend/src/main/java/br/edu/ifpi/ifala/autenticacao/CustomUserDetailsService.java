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
    // Tenta localizar por e-mail primeiro; se não encontrado, tenta por username.
    var usuarioOpt = usuarioRepository.findByEmail(username);
    if (usuarioOpt.isEmpty()) {
      usuarioOpt = usuarioRepository.findByUsername(username);
    }

    Usuario usuario = usuarioOpt
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

    // Retorna UserDetails com o e-mail como username (o token JWT usa e-mail como identificador).
    return User.builder().username(usuario.getEmail()).password(usuario.getSenha())
        .authorities(usuario.getRoles().stream().map(role -> role.name()).toArray(String[]::new))
        .build();
  }
}
