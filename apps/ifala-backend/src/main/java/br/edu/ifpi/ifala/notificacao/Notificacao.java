package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma notificação no sistema.
 */
@Entity
@Table(name = "notificacao")
public class Notificacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;

  private String mensagem;

  @Enumerated(EnumType.STRING)
  private TiposNotificacao tipo;

  @Column(name = "criado_em", updatable = false, nullable = false)
  private LocalDateTime criadoEm;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "denuncia_id")
  private Denuncia denuncia;

  public Notificacao() {
    // Default constructor for JPA
  }

  @PrePersist
  protected void onCreate() {
    if (this.criadoEm == null) {
      this.criadoEm = LocalDateTime.now();
    }
  }

  /**
   * Retorna o identificador da notificação.
   *
   * @return id da notificação
   */
  public Long getId() {
    return id;
  }

  /**
   * Define o identificador da notificação.
   *
   * @param id identificador a ser definido
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retorna o título da notificação.
   *
   * @return título
   */
  public String getTitulo() {
    return titulo;
  }

  /**
   * Define o título da notificação.
   *
   * @param titulo título a ser definido
   */
  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  /**
   * Retorna a mensagem da notificação.
   *
   * @return mensagem
   */
  public String getMensagem() {
    return mensagem;
  }

  /**
   * Define a mensagem da notificação.
   *
   * @param mensagem mensagem a ser definida
   */
  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  /**
   * Retorna o tipo da notificação.
   *
   * @return tipo de notificação
   */
  public TiposNotificacao getTipo() {
    return tipo;
  }

  /**
   * Define o tipo da notificação.
   *
   * @param tipo tipo a ser definido
   */
  public void setTipo(TiposNotificacao tipo) {
    this.tipo = tipo;
  }

  /**
   * Retorna a data/hora de criação da notificação.
   *
   * @return data/hora de criação
   */
  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  // Intencionalmente sem setter para criadoEm porque o valor é gerenciado pelo JPA (@PrePersist)

  /**
   * Retorna a denúncia relacionada a esta notificação (se houver).
   *
   * @return denúncia relacionada ou null
   */
  public Denuncia getDenuncia() {
    return denuncia;
  }

  /**
   * Define a denúncia relacionada a esta notificação.
   *
   * @param denuncia denúncia a ser associada
   */
  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }
}
