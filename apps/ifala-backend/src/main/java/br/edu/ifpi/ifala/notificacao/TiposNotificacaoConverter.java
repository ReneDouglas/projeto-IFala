package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.SQLException;

/**
 * Conversor JPA para mapear TiposNotificacao para o tipo enum do Postgres usando PGobject.
 * 
 * @author Phaola
 */
@Converter(autoApply = false)
public class TiposNotificacaoConverter implements AttributeConverter<TiposNotificacao, Object> {

  @Override
  public Object convertToDatabaseColumn(TiposNotificacao attribute) {
    if (attribute == null)
      return null;
    return new org.postgresql.util.PGobject() {
      {
        try {
          setType("tipos_notificacao_enum");
          setValue(attribute == TiposNotificacao.NOVA_DENUNCIA ? "nova_denuncia" : "nova_mensagem");
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Override
  public TiposNotificacao convertToEntityAttribute(Object dbData) {
    if (dbData == null)
      return null;
    String value = dbData.toString();
    return switch (value) {
      case "nova_denuncia" -> TiposNotificacao.NOVA_DENUNCIA;
      case "nova_mensagem" -> TiposNotificacao.NOVA_MENSAGEM;
      default -> throw new IllegalArgumentException(
          "Unknown tipos_notificacao_enum value: " + value);
    };
  }
}
