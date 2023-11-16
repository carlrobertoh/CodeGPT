package ee.carlrobert.codegpt.toolwindow.chat.contextual;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.indexes.CodebaseIndexingCompletedNotifier;
import ee.carlrobert.codegpt.indexes.CodebaseIndexingTask;
import ee.carlrobert.codegpt.indexes.FolderStructureTreePanel;
import ee.carlrobert.codegpt.settings.SettingsConfigurable;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.vector.VectorStore;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;

class ContextualChatToolWindowLandingPanel extends ResponsePanel {

  private static final Logger LOG = Logger.getInstance(ContextualChatToolWindowLandingPanel.class);
  private final Project project;
  private final EditorActionEvent actionEvent;

  ContextualChatToolWindowLandingPanel(Project project, EditorActionEvent actionEvent) {
    this.project = project;
    this.actionEvent = actionEvent;
    addContent(createContent());

    project.getMessageBus()
        .connect()
        .subscribe(CodebaseIndexingCompletedNotifier.INDEXING_COMPLETED_TOPIC,
            (CodebaseIndexingCompletedNotifier) () -> updateContent(createContent()));
  }

  private JTextPane createContent() {
    var description = createTextPane();
    if (VectorStore.getInstance(CodeGPTPlugin.getPluginBasePath()).isIndexExists()) {
      description.setText("<html>"
          + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
          + "Feel free to ask me anything about your codebase, and I'll be your helpful guide, "
          + "dedicated to providing you with the best answers possible!"
          + "</p>"
          + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
          + "Here are a few examples of how I might be helpful:"
          + "</p>"
          + "<ul>"
          + "<li>"
          + "<a href=\"LIST_DEPENDENCIES\">List all the dependencies that the project uses</a>"
          + "</li>"
          + "<li>"
          + "<a href=\"SCHEDULED_TASKS\">Are there any scheduled tasks or background jobs "
          + "running in our codebase, and if so, what are they responsible for?</a>"
          + "</li>"
          + "<li>"
          + "<a href=\"AUTHENTICATION_MECHANISM\">Can you provide an overview of the "
          + "authentication and authorization mechanism implemented in our application?</a>"
          + "</li>"
          + "</ul>"
          + "</html>");
    } else {
      description.setText("<html>"
          + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
          + "It looks like you haven't indexed your codebase yet."
          + "</p>"
          + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
          + "<a href=\"START_INDEXING\">Start indexing</a> your codebase to get "
          + "access to contextual chat experience."
          + "</p>"
          + "</html>");
    }

    return description;
  }

  private JTextPane createTextPane() {
    var textPane = SwingUtils.createTextPane("", this::handleHyperlinkClicked);
    textPane.setBackground(getPanelBackgroundColor());
    return textPane;
  }

  private void handleHyperlinkClicked(HyperlinkEvent event) {
    if (ACTIVATED.equals(event.getEventType())) {
      if (event.getURL() == null) {
        switch (event.getDescription()) {
          case "LOGIN":
            ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
            break;
          case "LIST_DEPENDENCIES":
            actionEvent.handleAction("List all the dependencies that the project uses");
            break;
          case "SCHEDULED_TASKS":
            actionEvent.handleAction("Are there any scheduled tasks or background "
                + "jobs running in our codebase, and if so, what are they responsible for?");
            break;
          case "AUTHENTICATION_MECHANISM":
            actionEvent.handleAction("Can you provide an overview of the authentication "
                + "and authorization mechanism implemented in our application?");
            break;
          case "START_INDEXING":
            var folderStructureTreePanel = new FolderStructureTreePanel(project);
            var show = OverlayUtils.showFileStructureDialog(project, folderStructureTreePanel);
            if (show == OK_EXIT_CODE) {
              new CodebaseIndexingTask(project, folderStructureTreePanel.getCheckedFiles()).run();
            }
            break;
          default:
            LOG.error("Could not trigger action {}", event.getDescription());
        }
      } else {
        SwingUtils.handleHyperlinkClicked(event);
      }
    }
  }
}
