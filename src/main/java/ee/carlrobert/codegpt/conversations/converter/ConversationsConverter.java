package ee.carlrobert.codegpt.conversations.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import ee.carlrobert.codegpt.conversations.ConversationsContainer;
import ee.carlrobert.codegpt.util.BaseConverter;

public class ConversationsConverter extends BaseConverter<ConversationsContainer> {

  public ConversationsConverter() {
    super(new TypeReference<>() {});
  }
}
