package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa a tabela enum_turmas no banco de dados.
 */
@Entity
@Table(name = "enum_turmas")
public class TurmaEntity extends EnumEntity {

  public TurmaEntity() {
    super();
  }

  public TurmaEntity(String value, String label) {
    super(value, label);
  }
}
