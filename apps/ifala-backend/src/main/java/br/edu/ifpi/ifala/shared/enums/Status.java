package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os possíveis status de uma denúncia no sistema. Cada status possui uma
 * descrição associada para facilitar a identificação.
 *
 * @author Renê Morais
 * 
 * @author luisthedevmagician
 */
public enum Status {
  RECEBIDO("Recebido"), EM_ANALISE("Em Análise"), AGUARDANDO("Aguardando Informações"), RESOLVIDO(
      "Resolvido"), REJEITADO("Rejeitado");

  private final String displayName;

  Status(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
