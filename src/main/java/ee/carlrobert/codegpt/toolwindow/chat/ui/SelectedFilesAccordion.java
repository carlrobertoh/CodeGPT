package ee.carlrobert.codegpt.toolwindow.chat.ui;

import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

public class SelectedFilesAccordion extends JPanel {

  public SelectedFilesAccordion(
      @NotNull Project project,
      @NotNull List<String> referencedFilePaths) {
    super(new BorderLayout());
    setOpaque(false);

    var contentPanel = createContentPane(project, referencedFilePaths);
    add(createToggleButton(contentPanel, referencedFilePaths.size()), BorderLayout.NORTH);
    add(contentPanel, BorderLayout.CENTER);
  }

  private JTextPane createContentPane(Project project, List<String> referencedFilePaths) {
    var textPane = UIUtil.createTextPane(
        getHtml(referencedFilePaths),
        false,
        event -> {
          if (FileUtil.exists(event.getDescription()) && ACTIVATED.equals(event.getEventType())) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(event.getDescription());
            FileEditorManager.getInstance(project).openFile(Objects.requireNonNull(file), true);
          }
        });
    textPane.setBorder(JBUI.Borders.emptyBottom(8));
    textPane.setVisible(false);
    return textPane;
  }

  private String getHtml(List<String> referencedFilePaths) {
    var html = referencedFilePaths.stream()
        .map(filePath -> format(
            "<li><a href=\"%s\">%s</a></li>",
            filePath,
            Paths.get(filePath).getFileName().toString()))
        .collect(Collectors.joining());
    return format("<ul style=\"margin: 4px 12px;\">%s</ul>", html);
  }

  private JToggleButton createToggleButton(JTextPane contentPane, int fileCount) {
    var accordionToggle = new JToggleButton(
        format("Referenced files (+%d)", fileCount), General.ArrowDown);
    accordionToggle.setFocusPainted(false);
    accordionToggle.setContentAreaFilled(false);
    accordionToggle.setBackground(getBackground());
    accordionToggle.setSelectedIcon(General.ArrowUp);
    accordionToggle.setBorder(null);
    accordionToggle.setHorizontalAlignment(SwingConstants.LEADING);
    accordionToggle.setHorizontalTextPosition(SwingConstants.LEADING);
    accordionToggle.addItemListener(e ->
        contentPane.setVisible(e.getStateChange() == ItemEvent.SELECTED));
    return accordionToggle;
  }
}
