package br.edu.ifpi.ifala.autenticacao;

import br.edu.ifpi.ifala.autenticacao.converters.PerfilConverter;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import java.io.Serializable;
import java.sql.Types;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade que representa um usuário no sistema.
 * 
 * @author Phaola
 */

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nome", nullable = false)
  private String nome;

  @Column(name = "username", unique = true)
  private String username;

  @Email
  @NotBlank
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank
  @Column(name = "senha", nullable = false)
  private String senha;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "must_change_password", nullable = false)
  private boolean mustChangePassword = true;

  @Column(name = "password_reset_token")
  private String passwordResetToken;

  @Column(name = "password_reset_expires")
  private Instant passwordResetExpires;

  @Column(name = "receber_notificacoes", nullable = false)
  private boolean receberNotificacoes = true;

  @ElementCollection(targetClass = Perfis.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "usuarios_perfil", joinColumns = @JoinColumn(name = "usuarios_id"))
  @Column(name = "perfil", columnDefinition = "perfis_enum")
  @JdbcTypeCode(Types.OTHER)
  @Convert(converter = PerfilConverter.class)
  private List<Perfis> roles;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isMustChangePassword() {
    return mustChangePassword;
  }

  public void setMustChangePassword(boolean mustChangePassword) {
    this.mustChangePassword = mustChangePassword;
  }

  public String getPasswordResetToken() {
    return passwordResetToken;
  }

  public void setPasswordResetToken(String passwordResetToken) {
    this.passwordResetToken = passwordResetToken;
  }

  public Instant getPasswordResetExpires() {
    return passwordResetExpires;
  }

  public void setPasswordResetExpires(Instant passwordResetExpires) {
    this.passwordResetExpires = passwordResetExpires;
  }

  public List<Perfis> getRoles() {
    return roles;
  }

  public void setRoles(List<Perfis> roles) {
    this.roles = roles;
  }

  public boolean isReceberNotificacoes() {
    return receberNotificacoes;
  }

  public void setReceberNotificacoes(boolean receberNotificacoes) {
    this.receberNotificacoes = receberNotificacoes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Usuario))
      return false;
    Usuario usuario = (Usuario) o;

    return id != null && id.equals(usuario.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Usuario{" + "id=" + id + ", nome='" + nome + '\'' + ", email='" + email + '\'' + '}';
  }

}
