package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para criar uma nova denúncia. Esta classe é usada para transferir
 * dados entre a camada de apresentação e a camada de serviço ao criar uma nova denúncia.
 *
 * @author Jhonatas G Ribeiro
 */

@Schema(name = "Criar Denúncia",
    description = "Dados necessários para registrar uma nova denúncia.")
public class CriarDenunciaDto {

  @Schema(
      description = "Descrição detalhada dos fatos ocorridos, com o máximo de informações possível.",
      example = "No dia 15/10, por volta das 10h da manhã, presenciei um ato de vandalismo na cantina, onde as mesas foram riscadas...",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "A descrição não pode ser vazia")
  @Size(min = 50, max = 5000, message = "A descrição deve ter entre 50 e 5000 caracteres")
  private String descricao;

  @Schema(description = "Categoria na qual a denúncia se enquadra.", example = "VANDALISMO",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "A categoria não pode ser nula")
  private Categorias categoria;

  // @NotBlank(message = "O token do ReCaptcha é obrigatório.")
  // A SER USADO DEPOIS QUE O RECAPTCHA ESTIVER FUNCIONANDO EM PRODUÇÃO ---
  @Schema(
      description = "Token gerado pelo Google ReCaptcha para verificar que a requisição não é de um robô.",
      example = "03AFY_a... (token longo)")
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
