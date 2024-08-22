package ee.carlrobert.codegpt.toolwindow.chat.ui;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.events.WebSearchEventDetails;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.toolwindow.ui.WebpageList;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.Nullable;

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

    var additionalContextPanel = getAdditionalContextPanel(project, message);
    if (additionalContextPanel != null) {
      add(additionalContextPanel, BorderLayout.CENTER);
    }

    var referencedFilePaths = message.getReferencedFilePaths();
    if (referencedFilePaths != null && !referencedFilePaths.isEmpty()) {
      add(createResponseBody(
          project,
          message.getUserMessage(),
          parentDisposable), BorderLayout.SOUTH);
    } else {
      add(createResponseBody(project, message.getPrompt(), parentDisposable), BorderLayout.SOUTH);
    }
  }

  public @Nullable JPanel getAdditionalContextPanel(Project project, Message message) {
    var addedDocumentation = CodeGPTKeys.ADDED_DOCUMENTATION.get(project);
    var referencedFilePaths = message.getReferencedFilePaths();
    if (addedDocumentation == null
        && (referencedFilePaths == null
        || referencedFilePaths.isEmpty())) {
      return null;
    }

    var panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    if (addedDocumentation != null) {
      var listModel = new DefaultListModel<WebSearchEventDetails>();
      listModel.addElement(
          new WebSearchEventDetails(UUID.randomUUID(), addedDocumentation.getName(),
              addedDocumentation.getUrl(), addedDocumentation.getUrl()));
      panel.add(createWebpageListPanel(new WebpageList(listModel)), BorderLayout.NORTH);
    }

    if (referencedFilePaths != null && !referencedFilePaths.isEmpty()) {
      panel.add(new SelectedFilesAccordion(project, referencedFilePaths), BorderLayout.NORTH);
    }
    return panel;
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
    return new ChatMessageResponseBody(
        project,
        false,
        true,
        false,
        false,
        parentDisposable)
        .withResponse(prompt);
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

  private static JPanel createWebpageListPanel(WebpageList webpageList) {
    var title = new JPanel(new BorderLayout());
    title.setOpaque(false);
    title.setBorder(JBUI.Borders.empty(8, 0));
    title.add(new JBLabel(CodeGPTBundle.get("userMessagePanel.documentation.title"))
        .withFont(JBUI.Fonts.miniFont()), BorderLayout.LINE_START);
    var listPanel = new JPanel(new BorderLayout());
    listPanel.setOpaque(false);
    listPanel.add(webpageList, BorderLayout.LINE_START);

    var panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.add(title, BorderLayout.NORTH);
    panel.add(listPanel, BorderLayout.CENTER);
    return panel;
  }
}
