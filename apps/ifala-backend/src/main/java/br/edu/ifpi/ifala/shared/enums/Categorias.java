package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa as categorias possíveis para uma denúncia no
 * sistema. Cada categoria
 * possui uma descrição associada para facilitar a identificação.
 *
 * @author Renê Morais
 */
public enum Categorias {
  celular("Porte de Celular"),
  drogas("Uso ou Porte de Drogas"),
  bullying("Bullying e Assédio"),
  violencia("Violência Física ou Verbal"),
  vandalismo("Vandalismo e Danos ao Patrimônio"),
  academico("Questões Acadêmicas (Fraude, Plágio, etc.)"),
  outros("Outros");

  private String descricao;

  Categorias(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
