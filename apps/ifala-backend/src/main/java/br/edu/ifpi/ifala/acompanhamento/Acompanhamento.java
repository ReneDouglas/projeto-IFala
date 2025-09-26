package br.edu.ifpi.ifala.acompanhamento;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * Representa o acompanhamento de uma denúncia no sistema. Esta classe mantém o registro das
 * mensagens e interações relacionadas a uma denúncia.
 *
 * @author Renê Morais
 */
@Entity
public class Acompanhamento {

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

}
