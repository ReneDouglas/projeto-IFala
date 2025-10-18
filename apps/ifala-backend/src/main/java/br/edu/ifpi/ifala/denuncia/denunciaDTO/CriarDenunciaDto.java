package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para criar uma nova denúncia.
 * Esta classe é usada para transferir dados entre a camada de apresentação
 * e a camada de serviço ao criar uma nova denúncia.
 *
 * @author Jhonatas G Ribeiro
 */

public class CriarDenunciaDto {

  @NotBlank(message = "A descrição não pode ser vazia")
  @Size(min = 50, max = 5000, message = "A descrição deve ter entre 50 e 5000 caracteres")
  private String descricao;

  @NotNull(message = "A categoria não pode ser nula")
  private Categorias categoria;

  // @NotBlank(message = "O token do ReCaptcha é obrigatório.")
  // A SER USADO DEPOIS QUE O RECAPTCHA ESTIVER FUNCIONANDO EM PRODUÇÃO ---
  private String recaptchaToken;

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

  public String getRecaptchaToken() {
    return recaptchaToken;
  }

  public void setRecaptchaToken(String recaptchaToken) {
    this.recaptchaToken = recaptchaToken;
  }
}