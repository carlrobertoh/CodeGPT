package ee.carlrobert.codegpt.conversations;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import ee.carlrobert.codegpt.conversations.converter.ConversationConverter;
import ee.carlrobert.codegpt.conversations.converter.ConversationsConverter;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.openai.client.ClientCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.conversations.ConversationsState",
    storages = @Storage("ChatGPTConversations.xml")
)
public class ConversationsState implements PersistentStateComponent<ConversationsState> {

  @OptionTag(converter = ConversationsConverter.class)
  public ConversationsContainer conversationsContainer = new ConversationsContainer();

  @OptionTag(converter = ConversationConverter.class)
  public Conversation currentConversation;

  public static ConversationsState getInstance() {
    return ApplicationManager.getApplication().getService(ConversationsState.class);
  }

  @Nullable
  @Override
  public ConversationsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ConversationsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void setCurrentConversation(@Nullable Conversation conversation) {
    this.currentConversation = conversation;
  }

  public static @Nullable Conversation getCurrentConversation() {
    return getInstance().currentConversation;
  }

  public List<Conversation> getSortedConversations() {
    return conversationsContainer
        .getConversationsMapping()
        .values()
        .stream()
        .flatMap(List::stream)
        .sorted(Comparator.comparing(Conversation::getUpdatedOn).reversed())
        .collect(Collectors.toList());
  }

  public Conversation createConversation(ClientCode clientCode) {
    var settings = SettingsState.getInstance();
    var conversation = new Conversation();
    conversation.setId(UUID.randomUUID());
    conversation.setClientCode(clientCode);
    conversation.setModel(settings.isChatCompletionOptionSelected ?
        settings.chatCompletionBaseModel :
        settings.textCompletionBaseModel);
    conversation.setCreatedOn(LocalDateTime.now());
    conversation.setUpdatedOn(LocalDateTime.now());
    return conversation;
  }

  public void addConversation(Conversation conversation) {
    var conversationsMapping = conversationsContainer.getConversationsMapping();
    var conversations = conversationsMapping.get(conversation.getClientCode());
    if (conversations == null) {
      conversations = new ArrayList<>();
    }
    conversations.add(conversation);
    conversationsMapping.put(conversation.getClientCode(), conversations);
  }

  public void saveConversation(Conversation conversation) {
    conversation.setUpdatedOn(LocalDateTime.now());
    var iterator = conversationsContainer.getConversationsMapping()
        .get(conversation.getClientCode())
        .listIterator();
    while (iterator.hasNext()) {
      var next = iterator.next();
      if (next.getId().equals(conversation.getId())) {
        iterator.set(conversation);
      }
    }
    setCurrentConversation(conversation);
  }

  public Conversation startConversation() {
    var currentClientCode = SettingsState.getInstance().isChatCompletionOptionSelected ?
        ClientCode.CHAT_COMPLETION : ClientCode.TEXT_COMPLETION;
    var conversation = createConversation(currentClientCode);
    setCurrentConversation(conversation);
    addConversation(conversation);
    return conversation;
  }

  public void clearAll() {
    conversationsContainer.getConversationsMapping().clear();
    currentConversation = null;
  }

  public Optional<Conversation> getNextConversation() {
    return tryGetNextOrPreviousConversation(true);
  }

  public Optional<Conversation> getPreviousConversation() {
    return tryGetNextOrPreviousConversation(false);
  }

  private Optional<Conversation> tryGetNextOrPreviousConversation(boolean isNext) {
    if (currentConversation != null) {
      var sortedConversations = getSortedConversations();
      for (int i = 0; i < sortedConversations.size(); i++) {
        var conversation = sortedConversations.get(i);
        if (conversation != null && conversation.getId().equals(currentConversation.getId())) {
          var nextIndex = isNext ? i + 1 : i - 1;
          if (isNext ? nextIndex < sortedConversations.size() : nextIndex != -1) {
            return Optional.of(sortedConversations.get(nextIndex));
          }
        }
      }
    }
    return Optional.empty();
  }

  public void deleteSelectedConversation() {
    var nextConversation = getNextConversation();
    if (nextConversation.isEmpty()) {
      nextConversation = getPreviousConversation();
    }

    var iterator = conversationsContainer.getConversationsMapping()
        .get(currentConversation.getClientCode())
        .listIterator();
    while (iterator.hasNext()) {
      var next = iterator.next();
      if (next.getId().equals(currentConversation.getId())) {
        iterator.remove();
        break;
      }
    }

    nextConversation.ifPresent(this::setCurrentConversation);
  }
}
