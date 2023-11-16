package ee.carlrobert.codegpt.toolwindow.chat.contextual;

@FunctionalInterface
public interface EditorActionEvent {

  void handleAction(String prompt);
}
