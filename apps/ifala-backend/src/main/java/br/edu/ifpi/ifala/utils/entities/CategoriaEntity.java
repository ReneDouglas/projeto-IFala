package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade JPA que representa a tabela enum_categorias no banco de dados.
 */
@Entity
@Table(name = "enum_categorias")
public class CategoriaEntity extends EnumEntity {

  public CategoriaEntity() {
    super();
  }

  public CategoriaEntity(String value, String label) {
    super(value, label);
  }
}
