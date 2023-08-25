package ee.carlrobert.codegpt.toolwindow.chat.standard;

import java.awt.Point;

@FunctionalInterface
public interface EditorActionEvent {
  void handleAction(EditorAction action, Point locationOnScreen);
}
