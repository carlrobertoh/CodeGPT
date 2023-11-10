package ee.carlrobert.codegpt.toolwindow.chat.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class UserMessagePanel extends JPanel {

  public UserMessagePanel(Project project, Message message, Disposable parentDisposable) {
    super(new BorderLayout());
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0),
        JBUI.Borders.empty(12, 8, 8, 8)));
    add(createDisplayNameWrapper(), BorderLayout.NORTH);
    add(new ChatMessageResponseBody(
            project,
            UIUtil.getPanelBackground(),
            false,
            true,
            parentDisposable).withResponse(message.getPrompt()),
        BorderLayout.SOUTH);
  }

  private JPanel createDisplayNameWrapper() {
    return JBUI.Panels.simplePanel()
        .withBorder(JBUI.Borders.emptyBottom(6))
        .addToLeft(new JBLabel(SettingsState.getInstance().getDisplayName())
            .setAllowAutoWrapping(true)
            .withFont(JBFont.label().asBold()));
  }
}
