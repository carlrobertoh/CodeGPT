package ee.carlrobert.codegpt.filter;

import com.intellij.codeInsight.hints.presentation.InputHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class CodeGPTPresentation implements EditorCustomElementRenderer, InputHandler {

  private final Editor editor;

  private final Project project;

  private final int startOffset;


  public CodeGPTPresentation(Editor editor, Project project, int starOffset) {
    this.editor = editor;
    this.project = project;
    this.startOffset = starOffset;
  }

  public void mouseClicked(@NotNull MouseEvent mouseEvent, @NotNull Point point) {
    int line = this.editor.getDocument().getLineNumber(this.startOffset);
    String errorInformation = this.editor.getDocument()
        .getText(new TextRange(this.startOffset, this.editor.getDocument().getLineEndOffset(line)));
    String prompt = "Provide recommendations for the error message: {{errorInformation}}";
    var message = new Message(prompt.replace("{{errorInformation}}", errorInformation));
    SwingUtilities.invokeLater(
        () -> project.getService(StandardChatToolWindowContentManager.class).sendMessage(message));
  }

  public void mouseExited() {
    ((EditorImpl) this.editor).setCustomCursor(this,
        Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }

  public void mouseMoved(@NotNull MouseEvent mouseEvent, @NotNull Point point) {
    ((EditorImpl) this.editor).setCustomCursor(this,
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  public int calcWidthInPixels(@NotNull Inlay inlay) {
    return Icons.Default.getIconWidth();
  }

  public void paint(@NotNull Inlay inlay, @NotNull Graphics g, @NotNull Rectangle r,
      @NotNull TextAttributes textAttributes) {
    Icon consoleIcon = Icons.Default;
    int curX = r.x + r.width / 2 - consoleIcon.getIconWidth() / 2;
    int curY = r.y + r.height / 2 - consoleIcon.getIconHeight() / 2;
    consoleIcon.paintIcon(inlay.getEditor().getComponent(), g, curX, curY);
  }
}
