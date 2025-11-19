package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.persistence.Convert;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe que representa uma notificação no sistema. Esta entidade armazena informações sobre
 * notificações enviadas aos usuários, incluindo seu conteúdo, tipo, denúncia relacionada e status
 * de leitura.
 *
 * @author Renê Morais
 * @author Phaola
 */
@Entity
@Table(name = "notificacoes")
public class Notificacao implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String conteudo;
  @Convert(converter = TiposNotificacaoConverter.class)
  @Column(name = "tipo", columnDefinition = "tipos_notificacao_enum")
  @JdbcTypeCode(Types.OTHER)
  private TiposNotificacao tipo;

  @ManyToOne
  private Denuncia denuncia;

  private Boolean lida;

  @Column(name = "lida_por")
  private String lidaPor;

  @Column(name = "data_envio")
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

  public String getLidaPor() {
    return lidaPor;
  }

  public void setLidaPor(String lidaPor) {
    this.lidaPor = lidaPor;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Notificacao)) {
      return false;
    }
    Notificacao other = (Notificacao) o;
    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    // O campo 'denuncia' foi trocado por 'denunciaId' para evitar
    // um loop infinito de logs (StackOverflowError).
    Long denunciaId = (denuncia != null) ? denuncia.getId() : null;

    return "Notificacao{" + "id=[" + id + "]" + ", conteudo=[" + conteudo + "]" + ", tipo=[" + tipo
        + "]" + ", denunciaId=[" + denunciaId + "]" + ", lida=[" + lida + "]" + ", lidaPor=["
        + lidaPor + "]" + ", dataEnvio=[" + dataEnvio + "]" + "}";
  }

}
