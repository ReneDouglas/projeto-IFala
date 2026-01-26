package br.edu.ifpi.ifala.denunciaFixada;

import br.edu.ifpi.ifala.autenticacao.Usuario;
import br.edu.ifpi.ifala.denuncia.Denuncia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa a associação entre um usuário e uma denúncia fixada.
 * Permite que usuários
 * administrativos marquem denúncias importantes para facilitar o acesso rápido.
 *
 * @author Guilherme Alves
 */
@Entity
@Table(name = "denuncia_fixada")
public class DenunciaFixada implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne
  @JoinColumn(name = "denuncia_id", nullable = false)
  private Denuncia denuncia;

  @Column(name = "fixada_em", nullable = false)
  private LocalDateTime fixadaEm;

  public DenunciaFixada() {
    this.fixadaEm = LocalDateTime.now();
  }

  public DenunciaFixada(Usuario usuario, Denuncia denuncia) {
    this();
    this.usuario = usuario;
    this.denuncia = denuncia;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Denuncia getDenuncia() {
    return denuncia;
  }

  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }

  public LocalDateTime getFixadaEm() {
    return fixadaEm;
  }

  public void setFixadaEm(LocalDateTime fixadaEm) {
    this.fixadaEm = fixadaEm;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DenunciaFixada that = (DenunciaFixada) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "DenunciaFixada{" + "id=" + id + ", usuarioId="
        + (usuario != null ? usuario.getId() : null) + ", denunciaId="
        + (denuncia != null ? denuncia.getId() : null) + ", fixadaEm=" + fixadaEm + '}';
  }
}
