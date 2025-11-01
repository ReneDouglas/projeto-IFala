package br.edu.ifpi.ifala.notificacao.enums;

/**
 * Enum que define os tipos possíveis de notificação no sistema.
 */
public enum TiposNotificacao {
    /**
     * Notificação interna do sistema (dentro da plataforma).
     */
    INTERNO,

    /**
     * Notificação externa (por email, etc).
     */
    EXTERNO,

    /**
     * Notificação de alerta/urgente.
     */
    ALERTA
}