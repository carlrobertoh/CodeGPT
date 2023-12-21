package ee.carlrobert.codegpt.toolwindow.chat.ui;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.CurrentTheme.NotificationInfo;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier;
import ee.carlrobert.embedding.CheckedFile;
import java.awt.BorderLayout;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

public class SelectedFilesNotification extends JPanel {

  private final Project project;
  private final JBLabel label;

  public SelectedFilesNotification(@NotNull Project project) {
    super(new BorderLayout());
    this.project = project;
    this.label = new JBLabel(
        getSelectedFilesLabel(),
        General.BalloonInformation,
        SwingConstants.LEADING);

    setVisible(false);
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8, 12)));

    setBackground(NotificationInfo.backgroundColor());
    setForeground(NotificationInfo.foregroundColor());
    add(label, BorderLayout.LINE_START);
    add(new ActionLink("Remove", (event) -> {
      clearSelectedFilesNotification();
    }), BorderLayout.LINE_END);
  }

  public void displaySelectedFilesNotification(@NotNull List<CheckedFile> checkedFiles) {
    if (checkedFiles.isEmpty()) {
      return;
    }

    label.setText(checkedFiles.size() + " files selected");
    var referencedFilePaths = checkedFiles.stream()
        .map(CheckedFile::getFilePath)
        .collect(Collectors.toList());
    label.setToolTipText(getHtml(referencedFilePaths));
    setVisible(true);
  }

  public void clearSelectedFilesNotification() {
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
    project.getMessageBus()
        .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
        .filesIncluded(emptyList());

    label.setText("0 files selected");
    label.setToolTipText(null);
    setVisible(false);
  }

  private String getHtml(List<String> referencedFilePaths) {
    var html = referencedFilePaths.stream()
        .map(filePath -> format("<li>%s</li>", Paths.get(filePath).getFileName().toString()))
        .collect(Collectors.joining());
    return format("<ul style=\"margin: 4px 12px;\">%s</ul>", html);
  }

  private String getSelectedFilesLabel() {
    var selectedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
    var fileCount = selectedFiles == null ? 0 : selectedFiles.size();
    return fileCount + " files selected";
  }
}
