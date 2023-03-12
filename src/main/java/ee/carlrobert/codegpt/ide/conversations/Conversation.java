package ee.carlrobert.codegpt.ide.conversations;

import ee.carlrobert.codegpt.client.BaseModel;
import ee.carlrobert.codegpt.client.ClientCode;
import ee.carlrobert.codegpt.ide.conversations.message.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {

  private UUID id;
  private String parentMessageId;
  private String unofficialClientConversationId;
  private List<Message> messages = new ArrayList<>();
  private ClientCode clientCode;
  private BaseModel model;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getParentMessageId() {
    return parentMessageId;
  }

  public void setParentMessageId(String parentMessageId) {
    this.parentMessageId = parentMessageId;
  }

  public String getUnofficialClientConversationId() {
    return unofficialClientConversationId;
  }

  public void setUnofficialClientConversationId(String unofficialClientConversationId) {
    this.unofficialClientConversationId = unofficialClientConversationId;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public ClientCode getClientCode() {
    return clientCode;
  }

  public void setClientCode(ClientCode clientCode) {
    this.clientCode = clientCode;
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public BaseModel getModel() {
    return model;
  }

  public void setModel(BaseModel model) {
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
}
