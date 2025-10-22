package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os cursos oferecidos no sistema. Cada curso possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author luisthedevmagician
 */

public enum Curso {
  ADMINISTRACAO("Administração"), AGROPECUARIA("Agropecuária"), INFORMATICA("Informática"),
  MEIO_AMBIENTE("Meio Ambiente"), ANALISE_DESENVOLVIMENTO_SISTEMAS("Análise e Desenvolvimento de Sistemas"),
  LICENCIATURA_MATEMATICA("Licenciatura em Matemática"), LICENCIATURA_FISICA("Licenciatura em Física"),
  GESTAO_AMBIENTAL("Gestão Ambiental");

  private final String displayName;

  Curso(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
