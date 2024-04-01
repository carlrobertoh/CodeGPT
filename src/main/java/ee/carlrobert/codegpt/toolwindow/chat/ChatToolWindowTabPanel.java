package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.getPromptWithContext;
import static ee.carlrobert.codegpt.ui.UIUtil.createScrollPaneWithSmartScroller;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
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
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ChatToolWindowTabPanel implements Disposable {

  private static final Logger LOG = Logger.getInstance(ChatToolWindowTabPanel.class);

  private final JPanel rootPanel;
  private final Conversation conversation;
  private final UserPromptTextArea userPromptTextArea;
  private final ConversationService conversationService;

  protected final Project project;
  protected final TotalTokensPanel totalTokensPanel;
  protected final ChatToolWindowScrollablePanel toolWindowScrollablePanel;

  protected abstract JComponent getLandingView();

  public ChatToolWindowTabPanel(@NotNull Project project, @NotNull Conversation conversation) {
    this.project = project;
    this.conversation = conversation;
    conversationService = ConversationService.getInstance();
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel();
    totalTokensPanel = new TotalTokensPanel(
        project,
        conversation,
        EditorUtil.getSelectedEditorSelectedText(project),
        this);
    userPromptTextArea = new UserPromptTextArea(this::handleSubmit, totalTokensPanel);
    rootPanel = createRootPanel();
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }

  public void dispose() {
    LOG.info("Disposing BaseChatToolWindowTabPanel component");
  }

  public JComponent getContent() {
    return rootPanel;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public void sendMessage(Message message) {
    sendMessage(message, ConversationType.DEFAULT);
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    SwingUtilities.invokeLater(() -> {
      var referencedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
      var chatToolWindowPanel = project.getService(StandardChatToolWindowContentManager.class)
          .tryFindChatToolWindowPanel();
      if (referencedFiles != null && !referencedFiles.isEmpty()) {
        var referencedFilePaths = referencedFiles.stream()
            .map(ReferencedFile::getFilePath)
            .collect(toList());
        message.setReferencedFilePaths(referencedFilePaths);
        message.setUserMessage(message.getPrompt());
        message.setPrompt(getPromptWithContext(referencedFiles, message.getPrompt()));

        totalTokensPanel.updateReferencedFilesTokens(referencedFiles);

        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
      }

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var attachedFilePath = CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project);
      var callParameters = getCallParameters(conversationType, message, attachedFilePath);
      if (callParameters.getImageData() != null) {
        message.setImageFilePath(attachedFilePath);
        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
        userMessagePanel.displayImage(attachedFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);

      var responsePanel = createResponsePanel(message, conversationType);
      messagePanel.add(responsePanel);
      updateTotalTokens(message);
      call(callParameters, responsePanel);
    });
  }

  private CallParameters getCallParameters(
      ConversationType conversationType,
      Message message,
      @Nullable String attachedFilePath) {
    var callParameters = new CallParameters(conversation, conversationType, message, false);
    if (attachedFilePath != null && !attachedFilePath.isEmpty()) {
      try {
        callParameters.setImageData(Files.readAllBytes(Path.of(attachedFilePath)));
        callParameters.setImageMediaType(FileUtil.getImageMediaType(attachedFilePath));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return callParameters;
  }

  private void updateTotalTokens(Message message) {
    int userPromptTokens = EncodingManager.getInstance().countTokens(message.getPrompt());
    int conversationTokens = EncodingManager.getInstance().countConversationTokens(conversation);
    totalTokensPanel.updateConversationTokens(conversationTokens + userPromptTokens);
  }

  private ResponsePanel createResponsePanel(Message message, ConversationType conversationType) {
    return new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation, conversationType))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
  }

  public TotalTokensDetails getTokenDetails() {
    return totalTokensPanel.getTokenDetails();
  }

  public void requestFocusForTextArea() {
    userPromptTextArea.focus();
  }

  public void displayLandingView() {
    toolWindowScrollablePanel.displayLandingView(getLandingView());
    totalTokensPanel.updateConversationTokens(conversation);
  }

  protected void reloadMessage(
      Message message,
      Conversation conversation,
      ConversationType conversationType) {
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
        call(new CallParameters(conversation, conversationType, message, true), responsePanel);
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

  private void call(CallParameters callParameters, ResponsePanel responsePanel) {
    var responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.getInstance().isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler(
        new ToolWindowCompletionResponseEventListener(
            conversationService,
            responsePanel,
            totalTokensPanel,
            userPromptTextArea) {
          @Override
          public void handleTokensExceededPolicyAccepted() {
            call(callParameters, responsePanel);
          }
        });
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);

    requestHandler.call(callParameters);
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
    sendMessage(message, ConversationType.DEFAULT);
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
        () -> {
          ConversationService.getInstance().startConversation();
          contentManager.createNewTabPanel();
        })), BorderLayout.NORTH);
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
        createUserPromptPanel(GeneralSettings.getCurrentState().getSelectedService()), gbc);
    return rootPanel;
  }
}