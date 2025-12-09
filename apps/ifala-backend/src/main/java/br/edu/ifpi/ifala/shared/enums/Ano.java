package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os anos disponíveis no sistema para ensino médio. Cada ano possui uma
 * descrição associada para facilitar a identificação.
 *
 * @author Paixa
 */

public enum Ano {
  PRIMEIRO_ANO("1º Ano"), SEGUNDO_ANO("2º Ano"), TERCEIRO_ANO("3º Ano");

  private final String displayName;

  Ano(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
