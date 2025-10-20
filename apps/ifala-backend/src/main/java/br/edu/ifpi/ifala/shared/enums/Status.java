package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os possíveis status de uma denúncia no sistema.
 * Cada status possui uma
 * descrição associada para facilitar a identificação.
 *
 * @author Renê Morais
 */
public enum Status {
  recebido("Recebido"),
  em_analise("Em Análise"),
  aguardando("Aguardando mais informações"),
  resolvido("Resolvido"),
  rejeitado("Rejeitado");

  private String descricao;

  Status(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
