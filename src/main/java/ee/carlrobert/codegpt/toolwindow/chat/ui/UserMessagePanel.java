package ee.carlrobert.codegpt.toolwindow.chat.ui;

import com.intellij.icons.AllIcons.General;
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
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class UserMessagePanel extends JPanel {

  private final JPanel headerPanel = new JPanel(new BorderLayout());

  public UserMessagePanel(Project project, Message message, Disposable parentDisposable) {
    super(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.add(createDisplayNameLabel(), BorderLayout.LINE_START);
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0),
        JBUI.Borders.empty(12, 8, 8, 8)));
    setBackground(ColorUtil.brighter(getBackground(), 2));
    add(headerPanel, BorderLayout.NORTH);

    var referencedFilePaths = message.getReferencedFilePaths();
    if (referencedFilePaths != null && !referencedFilePaths.isEmpty()) {
      headerPanel.add(getContextHelpIcon(referencedFilePaths), BorderLayout.LINE_END);
      add(createResponseBody(
          project,
          message.getUserMessage(),
          parentDisposable), BorderLayout.SOUTH);
    } else {
      add(createResponseBody(project, message.getPrompt(), parentDisposable), BorderLayout.SOUTH);
    }
  }

  private JBLabel getContextHelpIcon(List<String> checkedFiles) {
    var iconLabel = new JBLabel("Referenced files", General.ContextHelp, SwingConstants.RIGHT);
    iconLabel.setToolTipText(
        "<html>"
            + checkedFiles.stream()
            .map(item -> String.format("<p style=\"margin: 0;\">%s</p>", item))
            .collect(Collectors.joining())
            + "</html>");
    return iconLabel;
  }

  private ChatMessageResponseBody createResponseBody(
      Project project,
      String prompt,
      Disposable parentDisposable) {
    return new ChatMessageResponseBody(project, false, true, parentDisposable).withResponse(prompt);
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
