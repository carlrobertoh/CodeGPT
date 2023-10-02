package ee.carlrobert.codegpt.conversations;

import static java.util.stream.Collectors.toList;

import ee.carlrobert.codegpt.conversations.message.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {

  private UUID localId;
  private List<Message> messages = new ArrayList<>();
  private String clientCode;
  private String model;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  private boolean discardTokenLimit;

  public UUID getLocalId() {
    return localId;
  }

  public void setLocalId(UUID localId) {
    this.localId = localId;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public String getClientCode() {
    return clientCode;
  }

  public void setClientCode(String clientCode) {
    this.clientCode = clientCode;
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }

  public void discardTokenLimits() {
    this.discardTokenLimit = true;
  }

  public boolean isDiscardTokenLimit() {
    return discardTokenLimit;
  }

  public void removeMessage(UUID messageId) {
    setMessages(messages.stream()
        .filter(message -> !message.getId().equals(messageId))
        .collect(toList()));
  }
}
