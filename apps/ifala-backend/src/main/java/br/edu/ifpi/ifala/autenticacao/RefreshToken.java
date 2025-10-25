package br.edu.ifpi.ifala.autenticacao;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // O token (UUID ou string aleatória) que será enviado no cookie
  @Column(nullable = false, unique = true)
  private String token;

  @OneToOne
  @JoinColumn(name = "usuario_id", referencedColumnName = "id")
  private Usuario usuario;

  @Column(nullable = false)
  private Instant dataExpiracao;

  // Construtores, Getters e Setters

  public RefreshToken() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Instant getDataExpiracao() {
    return dataExpiracao;
  }

  public void setDataExpiracao(Instant dataExpiracao) {
    this.dataExpiracao = dataExpiracao;
  }
}
