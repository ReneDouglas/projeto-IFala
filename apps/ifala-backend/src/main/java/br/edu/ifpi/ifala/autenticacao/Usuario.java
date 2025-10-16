package br.edu.ifpi.ifala.autenticacao;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import br.edu.ifpi.ifala.autenticacao.enums.Authority;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  // Campo usado como identificador de login (chave principal no login)
  @Column(unique = true, nullable = false)
  private String email;

  // A senha hasheada (obrigatória para o Spring Security)
  @Column(nullable = false)
  private String password;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  // Campo para o "primeiro acesso" - deve ser trocado
  @Column(name = "must_change_password", nullable = false)
  private boolean mustChangePassword = true;

  // Campos para redefinição de senha
  @Column(name = "password_reset_token")
  private String passwordResetToken;

  @Column(name = "password_reset_expires")
  private Instant passwordResetExpires;

  // Mapeamento das autoridades (usando ElementCollection/ENUM como no seu script)
  @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "users_authorities", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  private List<Authority> roles;

  // --- IMPLEMENTAÇÃO DA INTERFACE USERDETAILS ---

  // 1. OBRIGATÓRIO: Retorna a senha do usuário
  @Override
  public String getPassword() {
    // Implementação explícita do método abstrato
    return this.password;
  }

  // 2. OBRIGATÓRIO: Retorna as autoridades/papéis do usuário
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Converte a lista de ENUMs para a estrutura exigida pelo Spring Security
    return roles.stream()
        // Adiciona o prefixo padrão 'ROLE_' (boa prática)
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .collect(Collectors.toList());
  }

  // 3. OBRIGATÓRIO: Retorna o identificador principal para o login.
  // Conforme seu requisito, estamos usando o EMAIL como o nome de usuário (login).
  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  // --- MÉTODOS DE NEGÓCIO ADICIONAIS ---

  // getRoles() é chamado no seu AuthController e é fornecido pelo @Data, mas aqui está para
  // clareza:
  public List<Authority> getRoles() {
    return roles;
  }

  // getEmail() é chamado no seu PasswordResetServiceImpl e é fornecido pelo @Data
  public String getEmail() {
    return email;
  }

  public boolean isMustChangePassword() {
    return this.mustChangePassword;
  }

  public void setMustChangePassword(boolean mustChangePassword) {
    this.mustChangePassword = mustChangePassword;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordResetToken() {
    return this.passwordResetToken;
  }

  public void setPasswordResetToken(String token) {
    this.passwordResetToken = token;
  }

  public Instant getPasswordResetExpires() {
    return this.passwordResetExpires;
  }

  public void setPasswordResetExpires(Instant expires) {
    this.passwordResetExpires = expires;
  }

  // getPasswordResetToken() / setPasswordResetToken() e getPasswordResetExpires() /
  // setPasswordResetExpires()
  // também são fornecidos pelo @Data.

}
