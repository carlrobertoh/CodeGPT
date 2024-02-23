package ee.carlrobert.codegpt.util;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

public class MapConverter extends BaseConverter<Map<String, Object>> {

  public MapConverter() {
    super(new TypeReference<>() {});
  }
}
