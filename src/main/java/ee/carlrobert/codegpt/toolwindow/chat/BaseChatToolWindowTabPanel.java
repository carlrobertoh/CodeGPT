package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.ui.UIUtil.createScrollPaneWithSmartScroller;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowPanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatToolWindowScrollablePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensDetails;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensPanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.UserPromptTextArea;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.UserPromptTextAreaHeader;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import ee.carlrobert.embedding.CheckedFile;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
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
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel();
    totalTokensPanel = new TotalTokensPanel(
        conversation,
        EditorUtil.getSelectedEditorSelectedText(project),
        this);
    userPromptTextArea = new UserPromptTextArea(this::handleSubmit, totalTokensPanel);
    rootPanel = createRootPanel();
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }

  @Override
  public void dispose() {
  }

  @Override
  public JComponent getContent() {
    return rootPanel;
  }

  @Override
  public Conversation getConversation() {
    return conversation;
  }

  @Override
  public void sendMessage(Message message) {
    var referencedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
    if (referencedFiles != null && !referencedFiles.isEmpty()) {
      var referencedFilePaths = referencedFiles.stream()
          .map(CheckedFile::getFilePath)
          .collect(toList());
      message.setReferencedFilePaths(referencedFilePaths);
      message.setUserMessage(message.getPrompt());
      message.setPrompt(getPromptWithContext(referencedFiles, message.getPrompt()));
    }

    var userMessagePanel = new UserMessagePanel(project, message, this);
    var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
    messagePanel.add(userMessagePanel);
    var responsePanel = new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messagePanel.add(responsePanel);

    var userPromptTokens = EncodingManager.getInstance().countTokens(message.getPrompt());
    var conversationTokens = EncodingManager.getInstance().countConversationTokens(conversation);
    totalTokensPanel.updateConversationTokens(conversationTokens + userPromptTokens);

    call(message, responsePanel, false);
    project.getService(StandardChatToolWindowContentManager.class)
        .tryFindChatToolWindowPanel()
        .ifPresent(StandardChatToolWindowPanel::clearSelectedFilesNotification);
  }

  // TODO: Move to util class
  private String getPromptWithContext(List<CheckedFile> referencedFiles, String userPrompt) {
    var context = referencedFiles.stream()
        .map(item -> format(
            "Path:\n"
                + "[File Path](%s)\n\n"
                + "Content:\n"
                + "%s\n",
            item.getFilePath(),
            format("```%s\n%s\n```", item.getFileExtension(), item.getFileContent().trim())))
        .collect(joining("\n"));
    return format(
        "Use the following context to answer question at the end:\n\n"
            + "%s\n"
            + "Question: %s",
        context,
        userPrompt);
  }

  @Override
  public TotalTokensDetails getTokenDetails() {
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
    var editor = EditorUtil.getSelectedEditor(project);
    if (editor != null) {
      var selectionModel = editor.getSelectionModel();
      var selectedText = selectionModel.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        var fileExtension = FileUtil.getFileExtension(
            ((EditorImpl) editor).getVirtualFile().getName());
        message = new Message(text + format("\n```%s\n%s\n```", fileExtension, selectedText));
        selectionModel.removeSelection();
      }
    }
    message.setUserMessage(text);
    sendMessage(message);
  }

  private JPanel createUserPromptPanel(ServiceType selectedService) {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    var contentManager = project.getService(StandardChatToolWindowContentManager.class);
    panel.add(JBUI.Panels.simplePanel(new UserPromptTextAreaHeader(
        selectedService,
        totalTokensPanel,
        contentManager::createNewTabPanel)), BorderLayout.NORTH);
    panel.add(JBUI.Panels.simplePanel(userPromptTextArea), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createRootPanel() {
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
    rootPanel.add(
        createUserPromptPanel(SettingsState.getInstance().getSelectedService()), gbc);
    return rootPanel;
  }
}