package br.edu.ifpi.ifala.autenticacao.converters;

import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.SQLException;

@Converter(autoApply = true)
public class PerfilConverter implements AttributeConverter<Perfis, Object> {

  @Override
  public Object convertToDatabaseColumn(Perfis attribute) {
    return attribute == null ? null : new org.postgresql.util.PGobject() {
      {
        try {
          setType("perfis_enum");
          setValue(attribute.getValue());
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Override
  public Perfis convertToEntityAttribute(Object dbData) {
    if (dbData == null) {
      return null;
    }
    String value = dbData.toString();
    return Perfis.fromValue(value);
  }
}
