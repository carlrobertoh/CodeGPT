package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.SwingUtils.createScrollPaneWithSmartScroller;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.components.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.TotalTokensPanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextArea;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextAreaHeader;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public abstract class BaseChatToolWindowTabPanel implements ChatToolWindowTabPanel {

  private static final Logger LOG = Logger.getInstance(BaseChatToolWindowTabPanel.class);

  private final boolean useContextualSearch;
  private final JPanel rootPanel;
  private final Conversation conversation;
  private final UserPromptTextArea userPromptTextArea;
  private final ConversationService conversationService;

  protected final Project project;
  protected final TotalTokensPanel totalTokensPanel;
  protected final ChatToolWindowScrollablePanel toolWindowScrollablePanel;

  protected abstract JComponent getLandingView();

  public BaseChatToolWindowTabPanel(
      @NotNull Project project,
      @NotNull Conversation conversation,
      boolean useContextualSearch) {
    this.project = project;
    this.conversation = conversation;
    this.useContextualSearch = useContextualSearch;
    conversationService = ConversationService.getInstance();
    var settings = SettingsState.getInstance();
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel(settings);
    totalTokensPanel = new TotalTokensPanel(
        conversation,
        EditorUtils.getSelectedEditorSelectedText(project),
        this);
    userPromptTextArea = new UserPromptTextArea(this::handleSubmit, totalTokensPanel);
    rootPanel = createRootPanel(settings);
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }

  @Override
  public void dispose() {
  }

  @Override
  public JPanel getContent() {
    return rootPanel;
  }

  @Override
  public Conversation getConversation() {
    return conversation;
  }

  @Override
  public void sendMessage(Message message) {
    var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
    messagePanel.add(new UserMessagePanel(project, message, this));
    var responsePanel = new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messagePanel.add(responsePanel);

    var userPromptTokens = EncodingManager.getInstance().countTokens(message.getPrompt());
    var conversationTokens = EncodingManager.getInstance().countConversationTokens(conversation);
    totalTokensPanel.updateConversationTokens(conversationTokens + userPromptTokens);

    call(message, responsePanel, false);
  }

  @Override
  public TokenDetails getTokenDetails() {
    return totalTokensPanel.getTokenDetails();
  }

  @Override
  public void requestFocusForTextArea() {
    userPromptTextArea.focus();
  }

  @Override
  public void displayLandingView() {
    toolWindowScrollablePanel.displayLandingView(getLandingView());
    totalTokensPanel.updateConversationTokens(conversation);
  }

  protected void reloadMessage(Message message, Conversation conversation) {
    ResponsePanel responsePanel = null;
    try {
      responsePanel = toolWindowScrollablePanel.getMessageResponsePanel(message.getId());
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      toolWindowScrollablePanel.update();
    } catch (Exception e) {
      throw new RuntimeException("Could not delete the existing message component", e);
    } finally {
      LOG.debug("Reloading message: " + message.getId());

      if (responsePanel != null) {
        message.setResponse("");
        conversationService.saveMessage(conversation, message);
        call(message, responsePanel, true);
      }

      totalTokensPanel.updateConversationTokens(conversation);

      TelemetryAction.IDE_ACTION.createActionMessage()
          .property("action", ActionType.RELOAD_MESSAGE.name())
          .send();
    }
  }

  protected void removeMessage(UUID messageId, Conversation conversation) {
    toolWindowScrollablePanel.removeMessage(messageId);
    conversation.removeMessage(messageId);
    conversationService.saveConversation(conversation);
    totalTokensPanel.updateConversationTokens(conversation);

    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    }
  }

  protected void clearWindow() {
    toolWindowScrollablePanel.clearAll();
    totalTokensPanel.updateConversationTokens(conversation);
  }

  private void call(Message message, ResponsePanel responsePanel, boolean retry) {
    var responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.getInstance().isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler(
        useContextualSearch,
        new ToolWindowCompletionResponseEventListener(
            conversationService,
            responsePanel,
            totalTokensPanel,
            userPromptTextArea) {
          @Override
          public void handleTokensExceededPolicyAccepted() {
            call(message, responsePanel, true);
          }
        });
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);
    requestHandler.call(conversation, message, retry);
  }

  private void handleSubmit(String text) {
    var message = new Message(text);
    var editor = EditorUtils.getSelectedEditor(project);
    if (editor != null) {
      var selectionModel = editor.getSelectionModel();
      var selectedText = selectionModel.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        var fileExtension = FileUtils.getFileExtension(
            ((EditorImpl) editor).getVirtualFile().getName());
        message = new Message(text + format("\n```%s\n%s\n```", fileExtension, selectedText));
        message.setUserMessage(text);
        selectionModel.removeSelection();
      }
    }

    sendMessage(message);
  }

  private JPanel createUserPromptPanel(SettingsState settings) {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    panel.setBackground(getPanelBackgroundColor());
    panel.add(
        new UserPromptTextAreaHeader(project, settings, totalTokensPanel),
        BorderLayout.NORTH);
    panel.add(userPromptTextArea, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createRootPanel(SettingsState settings) {
    var gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    var rootPanel = new JPanel(new GridBagLayout());
    rootPanel.add(createScrollPaneWithSmartScroller(toolWindowScrollablePanel), gbc);

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    rootPanel.add(createUserPromptPanel(settings), gbc);
    return rootPanel;
  }
}