package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.notificacao.Notificacao;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class Denuncia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "A descrição não pode ser vazia")
  @Size(min = 10, max = 5000, message = "A descrição deve ter entre 10 e 5000 caracteres")
  private String descricao;

  @Enumerated
  @NotNull(message = "A categoria não pode ser nula")
  private Categorias categoria;

  @Enumerated
  @NotNull(message = "O status não pode ser nulo")
  @Column(name = "status_denuncia_enum")
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

}
