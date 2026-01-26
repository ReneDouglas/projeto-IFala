package br.edu.ifpi.ifala.notificacao.enums;

/**
 * Enum que define os tipos de notificações disponíveis no sistema. NOVA_DENUNCIA - Notificação para
 * denúncias recebidas NOVA_MENSAGEM - Notificação para mensagens recebidas
 * 
 * @author Renê Morais
 * 
 * @author luisthedevmagician
 */
public enum TiposNotificacao {
  NOVA_DENUNCIA("Nova Denúncia"), NOVA_MENSAGEM("Nova Mensagem"), NOTIFICACAO_RECEBIMENTO(
      "Confirmação de Recebimento"), NOTIFICACAO_STATUS("Alteração de Situação");

  private final String displayName;

  TiposNotificacao(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
