package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa a tabela enum_cursos no banco de dados.
 */
@Entity
@Table(name = "enum_cursos")
public class CursoEntity extends EnumEntity {

  public CursoEntity() {
    super();
  }

  public CursoEntity(String value, String label) {
    super(value, label);
  }
}
