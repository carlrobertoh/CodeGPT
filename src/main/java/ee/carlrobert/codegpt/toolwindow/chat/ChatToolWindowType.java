package ee.carlrobert.codegpt.toolwindow.chat;

public enum ChatToolWindowType {
  CODEGPT_CHAT("CodeGPT Chat"),
  CODEGPT_CHAT_WITHOUT_PERSONA("CodeGPT Chat without Persona"),
  CODEGPT_CHAT_WITH_PERSONA("CodeGPT Chat with Persona");

  private final String name;

  ChatToolWindowType(String name) {
    this.name = name;
  }
}
