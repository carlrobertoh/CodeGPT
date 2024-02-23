package ee.carlrobert.codegpt.conversations.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.util.BaseConverter;

public class ConversationConverter extends BaseConverter<Conversation> {

  public ConversationConverter() {
    super(new TypeReference<>() {});
  }
}
