package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import com.fasterxml.jackson.annotation.JsonProperty; // IMPORTAR
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para criar uma nova denúncia.
 * essa classe é usada para transferir dados entre a camada de apresentação
 * e a camada de serviço ao criar uma nova denúncia.
 *
 * @author Jhonatas G Ribeiro
 */

public class CriarDenunciaDto {

  private String desejaSeIdentificar;
  private DadosDeIdentificacaoDto dadosDeIdentificacao;

  @JsonProperty("descricaoDetalhada")
  @NotBlank(message = "A descrição não pode ser vazia")
  @Size(min = 50, max = 5000, message = "A descrição deve ter entre 50 e 5000 caracteres")
  private String descricao;

  @JsonProperty("categoriaDaDenuncia")
  @NotBlank(message = "A categoria não pode ser nula")
  private String categoria;

  @JsonProperty("g-recaptcha-response")
  private String recaptchaToken;

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public String getRecaptchaToken() {
    return recaptchaToken;
  }

  public void setRecaptchaToken(String recaptchaToken) {
    this.recaptchaToken = recaptchaToken;
  }

  public String getDesejaSeIdentificar() {
    return desejaSeIdentificar;
  }

  public void setDesejaSeIdentificar(String desejaSeIdentificar) {
    this.desejaSeIdentificar = desejaSeIdentificar;
  }

  public DadosDeIdentificacaoDto getDadosDeIdentificacao() {
    return dadosDeIdentificacao;
  }

  public void setDadosDeIdentificacao(DadosDeIdentificacaoDto dadosDeIdentificacao) {
    this.dadosDeIdentificacao = dadosDeIdentificacao;
  }
}