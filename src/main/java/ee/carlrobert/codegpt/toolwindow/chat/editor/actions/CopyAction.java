package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class CopyAction extends TrackableAction {

  private final @NotNull Editor toolwindowEditor;

  public CopyAction(@NotNull Editor toolwindowEditor) {
    super(
        CodeGPTBundle.get("shared.copy"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.copy.description"),
        Actions.Copy,
        ActionType.COPY_CODE);
    this.toolwindowEditor = toolwindowEditor;
  }

  @Override
  public void handleAction(@NotNull AnActionEvent event) {
    StringSelection stringSelection = new StringSelection(toolwindowEditor.getDocument().getText());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

    var mouseEvent = (MouseEvent) event.getInputEvent();
    if (mouseEvent != null) {
      var locationOnScreen = mouseEvent.getLocationOnScreen();
      locationOnScreen.y = locationOnScreen.y - 16;

      OverlayUtil.showInfoBalloon(
          CodeGPTBundle.get("toolwindow.chat.editor.action.copy.success"),
          locationOnScreen);
    }
  }
}
