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
import ee.carlrobert.codegpt.settings.GeneralSettings;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class UserMessagePanel extends JPanel {

  public UserMessagePanel(Project project, Message message, Disposable parentDisposable) {
    super(new BorderLayout());
    var headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.add(createDisplayNameLabel(), BorderLayout.LINE_START);
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0),
        JBUI.Borders.empty(12, 8, 8, 8)));
    setBackground(ColorUtil.brighter(getBackground(), 2));
    add(headerPanel, BorderLayout.NORTH);

    var referencedFilePaths = message.getReferencedFilePaths();
    if (referencedFilePaths != null && !referencedFilePaths.isEmpty()) {
      add(new SelectedFilesAccordion(project, referencedFilePaths), BorderLayout.CENTER);
      add(createResponseBody(
          project,
          message.getUserMessage(),
          parentDisposable), BorderLayout.SOUTH);
    } else {
      add(createResponseBody(project, message.getPrompt(), parentDisposable), BorderLayout.SOUTH);
    }
  }

  public void displayImage(String imageFilePath) {
    try {
      var path = Paths.get(imageFilePath);
      add(new ImageAccordion(path.getFileName().toString(), Files.readAllBytes(path)));
    } catch (IOException e) {
      add(new JBLabel(
          "<html><small>Unable to load image %s</small></html>".formatted(imageFilePath),
          General.Error,
          SwingConstants.LEFT));
    }
  }

  private ChatMessageResponseBody createResponseBody(
      Project project,
      String prompt,
      Disposable parentDisposable) {
    return new ChatMessageResponseBody(project, false, true, parentDisposable).withResponse(prompt);
  }

  private JBLabel createDisplayNameLabel() {
    return new JBLabel(
        GeneralSettings.getCurrentState().getDisplayName(),
        Icons.User,
        SwingConstants.LEADING)
        .setAllowAutoWrapping(true)
        .withFont(JBFont.label().asBold())
        .withBorder(JBUI.Borders.emptyBottom(6));
  }
}
