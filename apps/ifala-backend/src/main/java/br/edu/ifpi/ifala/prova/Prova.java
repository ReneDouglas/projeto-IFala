package br.edu.ifpi.ifala.prova;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe que representa uma prova/evidência anexada a uma denúncia. Armazena os metadados dos
 * arquivos enviados como evidência.
 *
 * @author Guilherme Alves
 */
@Entity
@Table(name = "provas")
public class Prova implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "denuncia_id", nullable = false)
  @NotNull(message = "A denúncia associada não pode ser nula")
  private Denuncia denuncia;

  @Column(name = "nome_arquivo", nullable = false, length = 255)
  @NotBlank(message = "O nome do arquivo não pode ser vazio")
  private String nomeArquivo;

  @Column(name = "caminho_arquivo", nullable = false, length = 500)
  @NotBlank(message = "O caminho do arquivo não pode ser vazio")
  private String caminhoArquivo;

  @Column(name = "tamanho_bytes", nullable = false)
  @NotNull(message = "O tamanho do arquivo não pode ser nulo")
  @Positive(message = "O tamanho do arquivo deve ser positivo")
  private Long tamanhoBytes;

  @Column(name = "tipo_mime", nullable = false, length = 100)
  @NotBlank(message = "O tipo MIME não pode ser vazio")
  private String tipoMime;

  @Column(name = "criado_em", nullable = false, updatable = false)
  private LocalDateTime criadoEm;


  public Prova() {
    this.criadoEm = LocalDateTime.now();
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Denuncia getDenuncia() {
    return denuncia;
  }

  public void setDenuncia(Denuncia denuncia) {
    this.denuncia = denuncia;
  }

  public String getNomeArquivo() {
    return nomeArquivo;
  }

  public void setNomeArquivo(String nomeArquivo) {
    this.nomeArquivo = nomeArquivo;
  }

  public String getCaminhoArquivo() {
    return caminhoArquivo;
  }

  public void setCaminhoArquivo(String caminhoArquivo) {
    this.caminhoArquivo = caminhoArquivo;
  }

  public Long getTamanhoBytes() {
    return tamanhoBytes;
  }

  public void setTamanhoBytes(Long tamanhoBytes) {
    this.tamanhoBytes = tamanhoBytes;
  }

  public String getTipoMime() {
    return tipoMime;
  }

  public void setTipoMime(String tipoMime) {
    this.tipoMime = tipoMime;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Prova other = (Prova) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
}
