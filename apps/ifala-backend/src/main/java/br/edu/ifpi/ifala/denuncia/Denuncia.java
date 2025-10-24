package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.notificacao.Notificacao;
import br.edu.ifpi.ifala.shared.enums.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Classe que representa uma denúncia no sistema. Esta entidade armazena
 * informações sobre
 * denúncias, incluindo sua descrição, categoria, status e histórico de
 * acompanhamentos.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@Entity
@Table(name = "denuncias")
public class Denuncia implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "A descrição não pode ser vazia")
  @Size(min = 50, max = 5000, message = "A descrição deve ter entre 10 e 5000 caracteres")
  private String descricao;

  @Enumerated(EnumType.STRING)
  @Column(name = "categoria")
  @NotNull(message = "A categoria não pode ser nula")
  private Categorias categoria;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @NotNull(message = "O status não pode ser nulo")
  private Status status;

  @Column(name = "motivo_rejeicao")
  private String motivoRejeicao;

  @Column(name = "token_acompanhamento", unique = true, updatable = false, nullable = false)
  private UUID tokenAcompanhamento;

  @Column(name = "criado_em", updatable = false, nullable = false)
  private LocalDateTime criadoEm;

  @OneToMany(mappedBy = "denuncia")
  private Set<Acompanhamento> acompanhamentos = new HashSet<>();

  @OneToMany(mappedBy = "denuncia")
  private Set<Notificacao> notificacoes = new HashSet<>();

  private String alteradoPor;
  private LocalDateTime alteradoEm;

  @Transient // para não ser persistido no banco de dados
  private String recaptchaToken;

  @Column(name = "deseja_se_identificar")
  private boolean desejaSeIdentificar;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "denunciante_id", referencedColumnName = "id", unique = true)
  private Denunciante denunciante;

  // @Column(name = "nome_completo")
  // private String nomeCompleto;

  // @Column(name = "email")
  // private String email;

  // @Enumerated(EnumType.STRING)
  // @Column(name = "grau")
  // private Grau grau;

  // @Enumerated(EnumType.STRING)
  // @Column(name = "curso")
  // private Curso curso;

  // @Enumerated(EnumType.STRING)
  // @Column(name = "turma")
  // private Turma turma;

  /**
   * Construtor padrão que inicializa uma nova denúncia. Define um token de
   * acompanhamento único,
   * status inicial como RECEBIDO e a data/hora de criação.
   */

  public Denuncia() {
    this.tokenAcompanhamento = UUID.randomUUID();
    this.status = Status.RECEBIDO;
    this.criadoEm = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public Categorias getCategoria() {
    return categoria;
  }

  public void setCategoria(Categorias categoria) {
    this.categoria = categoria;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMotivoRejeicao() {
    return motivoRejeicao;
  }

  public void setMotivoRejeicao(String motivoRejeicao) {
    this.motivoRejeicao = motivoRejeicao;
  }

  public UUID getTokenAcompanhamento() {
    return tokenAcompanhamento;
  }

  public void setTokenAcompanhamento(UUID tokenAcompanhamento) {
    this.tokenAcompanhamento = tokenAcompanhamento;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }

  public Set<Acompanhamento> getAcompanhamentos() {
    return acompanhamentos;
  }

  public void setAcompanhamentos(Set<Acompanhamento> acompanhamentos) {
    this.acompanhamentos = acompanhamentos;
  }

  public Set<Notificacao> getNotificacoes() {
    return notificacoes;
  }

  public void setNotificacoes(Set<Notificacao> notificacoes) {
    this.notificacoes = notificacoes;
  }

  public String getAlteradoPor() {
    return alteradoPor;
  }

  public void setAlteradoPor(String alteradoPor) {
    this.alteradoPor = alteradoPor;
  }

  public LocalDateTime getAlteradoEm() {
    return alteradoEm;
  }

  public void setAlteradoEm(LocalDateTime alteradoEm) {
    this.alteradoEm = alteradoEm;
  }

  public String getRecaptchaToken() {
    return recaptchaToken;
  }

  public void setRecaptchaToken(String recaptchaToken) {
    this.recaptchaToken = recaptchaToken;
  }

  public boolean isDesejaSeIdentificar() {
    return desejaSeIdentificar;
  }

  public void setDesejaSeIdentificar(boolean desejaSeIdentificar) {
    this.desejaSeIdentificar = desejaSeIdentificar;
  }

  public Denunciante getDenunciante() {
    return denunciante;
  }

  public void setDenunciante(Denunciante denunciante) {
    this.denunciante = denunciante;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Denuncia)) {
      return false;
    }
    Denuncia other = (Denuncia) o;
    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Denuncia{" + "id=[" + id + "]" + ", descricao=[" + descricao + "]" + ", categoria=["
        + categoria + "]" + ", status=[" + status + "]" + ", motivoRejeicao=[" + motivoRejeicao
        + "]" + ", tokenAcompanhamento=[" + tokenAcompanhamento + "]" + ", criadoEm=[" + criadoEm
        + "]" + ", alteradoPor=[" + alteradoPor + "]" + ", alteradoEm=[" + alteradoEm + "]" + "}";
  }

}
