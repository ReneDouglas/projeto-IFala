package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.TipoNotificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para criação e atualização de notificações.
 * Implementado como record, seguindo o padrão adotado no backend do IFala.
 */
public record NotificationRequestDto(

        @NotBlank(message = "O título é obrigatório.")
        String titulo,

        @NotBlank(message = "A mensagem é obrigatória.")
        String mensagem,

        @NotNull(message = "O tipo de notificação é obrigatório.")
        TipoNotificacao tipo

) {}
