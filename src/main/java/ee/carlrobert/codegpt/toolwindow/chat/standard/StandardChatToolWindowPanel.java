package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.util.Collections.emptyList;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.CurrentTheme.NotificationInfo;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.embedding.CheckedFile;
import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

public class StandardChatToolWindowPanel extends SimpleToolWindowPanel {

  private final Project project;
  private final JPanel selectedFilesLabelWrapper;
  private final JBLabel selectedFilesLabel;

  public StandardChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    this.project = project;

    var checkedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
    var fileCount = checkedFiles != null ? checkedFiles.size() : 0;
    selectedFilesLabel = new JBLabel(
        fileCount + " files selected",
        General.BalloonInformation,
        SwingConstants.LEADING);
    selectedFilesLabelWrapper = getSelectedFilesNotification(selectedFilesLabel);
    init(project, selectedFilesLabelWrapper, parentDisposable);
  }

  public void displaySelectedFilesNotification(@NotNull List<CheckedFile> checkedFiles) {
    if (checkedFiles.isEmpty()) {
      return;
    }

    selectedFilesLabel.setText(checkedFiles.size() + " files selected");
    selectedFilesLabelWrapper.setVisible(true);
  }

  public void clearSelectedFilesNotification() {
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
    selectedFilesLabel.setText("0 files selected");
    selectedFilesLabelWrapper.setVisible(false);
  }

  private void init(Project project, JPanel selectedFilesLabelWrapper,
      Disposable parentDisposable) {
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      conversation = ConversationService.getInstance().startConversation();
    }

    var tabPanel = new StandardChatToolWindowTabPanel(project, conversation);
    var tabbedPane = createTabbedPane(tabPanel, parentDisposable);
    Runnable onAddNewTab = () -> {
      tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
      repaint();
      revalidate();
    };
    var actionToolbarPanel = new JPanel(new BorderLayout());
    actionToolbarPanel.add(
        createActionToolbar(project, tabbedPane, onAddNewTab).getComponent(),
        BorderLayout.LINE_START);

    setToolbar(actionToolbarPanel);
    setContent(JBUI.Panels.simplePanel(tabbedPane).addToBottom(selectedFilesLabelWrapper));

    Disposer.register(parentDisposable, tabPanel);
  }

  private JPanel getSelectedFilesNotification(JBLabel label) {
    var notification = new JPanel(new BorderLayout());
    notification.setVisible(false);
    notification.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8, 12)));
    notification.setBackground(NotificationInfo.backgroundColor());
    notification.setForeground(NotificationInfo.foregroundColor());
    notification.add(label, BorderLayout.LINE_START);
    notification.add(new ActionLink("Remove", (event) -> {
      project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
      selectedFilesLabelWrapper.setVisible(false);
    }), BorderLayout.LINE_END);
    return notification;
  }

  private ActionToolbar createActionToolbar(
      Project project,
      StandardChatToolWindowTabbedPane tabbedPane,
      Runnable onAddNewTab) {
    var actionGroup = new DefaultCompactActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(onAddNewTab));
    actionGroup.add(
        new ClearChatWindowAction(() -> tabbedPane.resetCurrentlyActiveTabPanel(project)));
    actionGroup.addSeparator();
    actionGroup.add(new OpenInEditorAction());

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(this);
    return toolbar;
  }

  private StandardChatToolWindowTabbedPane createTabbedPane(
      StandardChatToolWindowTabPanel tabPanel,
      Disposable parentDisposable) {
    var tabbedPane = new StandardChatToolWindowTabbedPane(parentDisposable);
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }
}
