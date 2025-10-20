package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa a tabela enum_status no banco de dados.
 */
@Entity
@Table(name = "enum_status")
public class StatusEntity extends EnumEntity {

  public StatusEntity() {
    super();
  }

  public StatusEntity(String value, String label) {
    super(value, label);
  }
}
