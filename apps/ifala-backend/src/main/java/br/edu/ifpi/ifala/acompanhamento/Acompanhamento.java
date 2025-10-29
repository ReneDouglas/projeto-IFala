package br.edu.ifpi.ifala.acompanhamento;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import jakarta.persistence.Entity;
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

  private String autor; // pode ser o denunciante ou o administrador
  private String mensagem;

  @ManyToOne
  private Denuncia denuncia; // referência à denúncia

  private LocalDateTime dataEnvio;

  /**
   * Construtor padrão da classe Acompanhamento. Inicializa a data de envio com a data e hora atual.
   */
  public Acompanhamento() {
    this.dataEnvio = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
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
