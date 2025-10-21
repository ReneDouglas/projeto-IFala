package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa a tabela enum_graus no banco de dados.
 * 
 * @author luisthedevmagician
 */


@Entity
@Table(name = "enum_graus")
public class GrauEntity extends EnumEntity {

  public GrauEntity() {
    super();
  }

  public GrauEntity(String value, String label) {
    super(value, label);
  }
}
