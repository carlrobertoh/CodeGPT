package ee.carlrobert.codegpt.toolwindow.chat.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class UserMessagePanel extends JPanel {

  public UserMessagePanel(Project project, Message message, Disposable parentDisposable) {
    super(new BorderLayout());
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0),
        JBUI.Borders.empty(12, 8, 8, 8)));
    setBackground(ColorUtil.brighter(getBackground(), 2));
    add(createDisplayNameLabel(), BorderLayout.NORTH);
    add(createResponseBody(project, message, parentDisposable), BorderLayout.SOUTH);
  }

  private ChatMessageResponseBody createResponseBody(
      Project project,
      Message message,
      Disposable parentDisposable) {
    return new ChatMessageResponseBody(project, false, true, parentDisposable)
        .withResponse(message.getPrompt());
  }

  private JBLabel createDisplayNameLabel() {
    return new JBLabel(
        SettingsState.getInstance().getDisplayName(),
        Icons.User,
        SwingConstants.LEADING)
        .setAllowAutoWrapping(true)
        .withFont(JBFont.label().asBold())
        .withBorder(JBUI.Borders.emptyBottom(6));
  }
}
