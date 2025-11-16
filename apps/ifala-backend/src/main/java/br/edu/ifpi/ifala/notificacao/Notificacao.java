package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.notificacao.enums.TipoNotificacao;
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
 * Entidade que representa uma notificação no sistema IFala.
 */
@Entity
@Table(name = "notificacao")
public class Notificacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String titulo;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String mensagem;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoNotificacao tipo;

  @Column(name = "criado_em", nullable = false)
  private LocalDateTime criadoEm;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "denuncia_id")
  private Denuncia denuncia;

  /**
   * Define a data de criação da notificação antes de persistir.
   */
  @PrePersist
  public void prePersist() {
    if (criadoEm == null) {
      criadoEm = LocalDateTime.now();
    }
  }

  // Getters e Setters

  /**
   * Retorna o ID da notificação.
   *
   * @return ID da notificação
   */
  public Long getId() {
    return id;
  }

  /**
   * Define o ID da notificação.
   *
   * @param id o ID a ser definido
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retorna o título da notificação.
   *
   * @return título da notificação
   */
  public String getTitulo() {
    return titulo;
  }

  /**
   * Define o título da notificação.
   *
   * @param titulo o título a ser definido
   */
  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  /**
   * Retorna a mensagem da notificação.
   *
   * @return mensagem da notificação
   */
  public String getMensagem() {
    return mensagem;
  }

  /**
   * Define a mensagem da notificação.
   *
   * @param mensagem a mensagem a ser definida
   */
  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  /**
   * Retorna o tipo da notificação.
   *
   * @return tipo da notificação
   */
  public TipoNotificacao getTipo() {
    return tipo;
  }

  /**
   * Define o tipo da notificação.
   *
   * @param tipo o tipo a ser definido
   */
  public void setTipo(TipoNotificacao tipo) {
    this.tipo = tipo;
  }

  /**
   * Retorna a data de criação da notificação.
   *
   * @return data de criação
   */
  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  /**
   * Define a data de criação da notificação.
   *
   * @param criadoEm a data a ser definida
   */
  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }

  /**
   * Retorna a denúncia associada à notificação.
   *
   * @return denúncia associada
   */
  public Denuncia getDenuncia() {
    return denuncia;
  }

  /**
   * Define a denúncia associada à notificação.
   *
   * @param denuncia a denúncia a ser definida
   */
  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }
}
