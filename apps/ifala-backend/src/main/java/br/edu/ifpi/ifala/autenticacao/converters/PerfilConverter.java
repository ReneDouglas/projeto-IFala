package br.edu.ifpi.ifala.autenticacao.converters;

import br.edu.ifpi.ifala.shared.enums.Perfis;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PerfilConverter implements AttributeConverter<Perfis, String> {

  @Override
  public String convertToDatabaseColumn(Perfis attribute) {
    return attribute == null ? null : attribute.name().toLowerCase();
  }

  @Override
  public Perfis convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Perfis.valueOf(dbData.toUpperCase());
  }
}
