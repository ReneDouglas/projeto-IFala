package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
public record CriarDenunciaDto(

    Boolean desejaSeIdentificar,

    @Schema(
        description = "Dados de identificação (obrigatório se desejaSeIdentificar for true)") @Valid DadosDeIdentificacaoDto dadosDeIdentificacao,

    @JsonProperty("descricaoDetalhada") @Schema(description = "Descrição detalhada da denúncia.",
        minLength = 50, maxLength = 5000, requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(
            message = "A descrição não pode ser vazia") @Size(min = 50, max = 5000,
                message = "A descrição deve ter entre 50 e 5000 caracteres") String descricao,

    @JsonProperty("categoriaDaDenuncia") @Schema(description = "Categoria da denúncia.",
        example = "BULLYING", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(
            message = "A categoria não pode ser nula") Categorias categoria,

    @JsonProperty("recaptchaToken") @Schema(
        description = "Token do Google ReCaptcha v3") String recaptchaToken) {
}
