package ee.carlrobert.codegpt.state.conversations;

import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.openai.client.ClientCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {

  private UUID id;
  private List<Message> messages = new ArrayList<>();
  private ClientCode clientCode;
  private String model;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;

  public   String SYSTEM_INIT_MESSAGE ;

  public Conversation() {
    this.SYSTEM_INIT_MESSAGE = SYSTEM_INIT_DEFAULT_MESSAGE;
  }

  public static String SYSTEM_INIT_DEFAULT_MESSAGE ="You are ChatGPT, a large language model trained by OpenAI. " +
          "Answer as concisely as possible. " +
          "Include code language in markdown snippets whenever possible.";
  public String getSYSTEM_INIT_MESSAGE(){
    if(this.SYSTEM_INIT_MESSAGE.equals("")){
      this.SYSTEM_INIT_MESSAGE = SYSTEM_INIT_DEFAULT_MESSAGE;
    }
    return     this.SYSTEM_INIT_MESSAGE;
  }
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
}
