package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os graus de ensino no sistema. Cada grau possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author luisthedevmagician
 */

public enum Grau {
  MEDIO("Médio"), SUPERIOR("Superior");

  private final String displayName;

  Grau(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
