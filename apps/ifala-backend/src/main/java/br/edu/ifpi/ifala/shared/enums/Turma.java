package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa as turmas disponíveis no sistema. Cada turma possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author luisthedevmagician
 */

public enum Turma {
  ANO1_A("1 Ano A"), ANO1_B("1 Ano B"), ANO2_A("2 Ano A"), ANO2_B("2 Ano B"), ANO3_A("3 Ano A"), ANO3_B("3 Ano B"),
  MODULO_I("Módulo I"), MODULO_II("Módulo II"), MODULO_III("Módulo III"), MODULO_IV("Módulo IV"),
  MODULO_V("Módulo V"), MODULO_VI("Módulo VI"), MODULO_VII("Módulo VII"), MODULO_VIII("Módulo VIII");

  private final String displayName;

  Turma(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
