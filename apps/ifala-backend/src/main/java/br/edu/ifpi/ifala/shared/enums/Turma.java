package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa as turmas disponíveis no sistema. Cada turma possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author luisthedevmagician
 * @author Paixa (refatoração)
 */

public enum Turma {
  UNICA("Única"), A("A"), B("B"), MODULO_I("Módulo I"), MODULO_II("Módulo II"), MODULO_III(
      "Módulo III"), MODULO_IV("Módulo IV"), MODULO_V(
          "Módulo V"), MODULO_VI("Módulo VI"), MODULO_VII("Módulo VII"), MODULO_VIII("Módulo VIII");

  private final String displayName;

  Turma(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
