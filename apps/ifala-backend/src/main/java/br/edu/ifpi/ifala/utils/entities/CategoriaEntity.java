package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Entidade JPA que representa a tabela enum_categorias no banco de dados.
 * 
 * @author luisthedevmagician
 */

@Entity
@Table(name = "enum_categorias")
public class CategoriaEntity extends EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public CategoriaEntity() {
    super();
  }

  public CategoriaEntity(String value, String label) {
    super(value, label);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CategoriaEntity)) {
      return false;
    }
    CategoriaEntity other = (CategoriaEntity) o;

    // getter 'getId()' pois o campo 'id' está na superclasse EnumEntity
    return getId() != null && getId().equals(other.getId());
  }

  // Adaptado para herança, usando getters
  @Override
  public String toString() {

    return "CategoriaEntity{" + "id=[" + getId() + "]" + ", value=[" + getValue() + "]"
        + ", label=[" + getLabel() + "]" + "}";
  }

}
