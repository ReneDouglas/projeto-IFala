package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Classe que representa uma notificação no sistema. Esta entidade armazena informações sobre
 * notificações enviadas aos usuários, incluindo seu conteúdo, tipo, denúncia relacionada e status
 * de leitura.
 *
 * @author Renê Morais
 */
@Entity
public class Notificacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String conteudo;
  private TiposNotificacao tipo;

  @ManyToOne
  private Denuncia denuncia;

  private Boolean lida;
  private LocalDateTime dataEnvio;

  /**
   * Construtor padrão da classe Notificacao.
   */
  public Notificacao() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getConteudo() {
    return conteudo;
  }

  public void setConteudo(String conteudo) {
    this.conteudo = conteudo;
  }

  public TiposNotificacao getTipo() {
    return tipo;
  }

  public void setTipo(TiposNotificacao tipo) {
    this.tipo = tipo;
  }

  public Denuncia getDenuncia() {
    return denuncia;
  }

  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }

  public Boolean getLida() {
    return lida;
  }

  public void setLida(Boolean lida) {
    this.lida = lida;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }

}
