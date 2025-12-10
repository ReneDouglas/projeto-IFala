package br.edu.ifpi.ifala.acompanhamento;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa o acompanhamento de uma denúncia no sistema. Esta classe mantém o registro das
 * mensagens e interações relacionadas a uma denúncia.
 *
 * @author Renê Morais
 */
@Entity
@Table(name = "acompanhamentos")
public class Acompanhamento implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Perfis autor; // pode ser ADMIN ou ANONIMO

  private String mensagem;

  @ManyToOne
  private Denuncia denuncia; // referência à denúncia

  private LocalDateTime dataEnvio;

  private Boolean visualizado; // indica se a mensagem foi visualizada pelo destinatário

  /**
   * Construtor padrão da classe Acompanhamento. Inicializa a data de envio com a data e hora atual.
   * Por padrão, a mensagem não foi visualizada.
   */
  public Acompanhamento() {
    this.dataEnvio = LocalDateTime.now();
    this.visualizado = false;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Perfis getAutor() {
    return autor;
  }

  public void setAutor(Perfis autor) {
    this.autor = autor;
  }

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public Denuncia getDenuncia() {
    return denuncia;
  }

  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }

  public Boolean getVisualizado() {
    return visualizado;
  }

  public void setVisualizado(Boolean visualizado) {
    this.visualizado = visualizado;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Acompanhamento)) {
      return false;
    }
    Acompanhamento other = (Acompanhamento) o;

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

    return "Acompanhamento{" + "id=[" + id + "]" + ", autor=[" + autor + "]" + ", mensagem=["
        + mensagem + "]" + ", denunciaId=[" + denunciaId + "]" + ", dataEnvio=[" + dataEnvio + "]"
        + "}";
  }
}
