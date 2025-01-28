package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.ui.UIUtil.createScrollPaneWithSmartScroller;
import static java.lang.String.format;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.ChatCompletionParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.completions.ToolwindowChatCompletionRequestHandler;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.CopyAction;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatToolWindowScrollablePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensDetails;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensPanel;
import ee.carlrobert.codegpt.toolwindow.ui.ChatToolWindowLandingPanel;
import ee.carlrobert.codegpt.toolwindow.ui.ResponseMessagePanel;
import ee.carlrobert.codegpt.toolwindow.ui.UserMessagePanel;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.textarea.UserInputPanel;
import ee.carlrobert.codegpt.ui.textarea.header.FileTagDetails;
import ee.carlrobert.codegpt.ui.textarea.header.GitCommitTagDetails;
import ee.carlrobert.codegpt.ui.textarea.header.HeaderTagDetails;
import ee.carlrobert.codegpt.ui.textarea.header.PersonaTagDetails;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import git4idea.GitCommit;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JPanel;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatToolWindowTabPanel implements Disposable {

  private static final Logger LOG = Logger.getInstance(ChatToolWindowTabPanel.class);

  private final ChatSession chatSession;

  private final Project project;
  private final JPanel rootPanel;
  private final Conversation conversation;
  private final UserInputPanel userInputPanel;
  private final ConversationService conversationService;
  private final TotalTokensPanel totalTokensPanel;
  private final ChatToolWindowScrollablePanel toolWindowScrollablePanel;

  private @Nullable ToolwindowChatCompletionRequestHandler requestHandler;

  public ChatToolWindowTabPanel(@NotNull Project project, @NotNull Conversation conversation) {
    this.project = project;
    this.conversation = conversation;
    this.chatSession = new ChatSession();
    conversationService = ConversationService.getInstance();
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel();
    totalTokensPanel = new TotalTokensPanel(
        project,
        conversation,
        EditorUtil.getSelectedEditorSelectedText(project),
        this);
    userInputPanel = new UserInputPanel(
        project,
        conversation,
        totalTokensPanel,
        this,
        this::handleSubmit,
        this::handleCancel);
    userInputPanel.requestFocus();
    rootPanel = createRootPanel();

    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    } else {
      displayConversation();
    }
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

  public TotalTokensDetails getTokenDetails() {
    return totalTokensPanel.getTokenDetails();
  }

  public void requestFocusForTextArea() {
    userInputPanel.requestFocus();
  }

  public void displayLandingView() {
    toolWindowScrollablePanel.displayLandingView(getLandingView());
    totalTokensPanel.updateConversationTokens(conversation);
  }

  public void addSelection(VirtualFile editorFile, SelectionModel selectionModel) {
    userInputPanel.addSelection(editorFile, selectionModel);
  }

  public void addCommitReferences(List<GitCommit> gitCommits) {
    userInputPanel.addCommitReferences(gitCommits);
  }

  public List<HeaderTagDetails> getSelectedTags() {
    return userInputPanel.getSelectedTags();
  }

  private ChatCompletionParameters getCallParameters(
      Message message,
      ConversationType conversationType) {
    var selectedTags = userInputPanel.getSelectedTags();
    var builder = ChatCompletionParameters.builder(conversation, message)
        .sessionId(chatSession.getId())
        .conversationType(conversationType)
        .imageDetailsFromPath(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project))
        .referencedFiles(getReferencedFiles(selectedTags));

    findTagOfType(selectedTags, PersonaTagDetails.class)
        .ifPresent(tag -> builder.personaDetails(tag.getPersonaDetails()));

    findTagOfType(selectedTags, GitCommitTagDetails.class)
        .ifPresent(tag -> builder.gitDiff(tag.getGitCommit().getFullMessage()));

    return builder.build();
  }

  private List<ReferencedFile> getReferencedFiles() {
    return getReferencedFiles(userInputPanel.getSelectedTags());
  }

  private List<ReferencedFile> getReferencedFiles(List<? extends HeaderTagDetails> tags) {
    return tags.stream()
        .filter(FileTagDetails.class::isInstance)
        .map(it -> ReferencedFile.from(((FileTagDetails) it).getVirtualFile()))
        .toList();
  }

  private <T extends HeaderTagDetails> Optional<T> findTagOfType(
      List<? extends HeaderTagDetails> tags,
      Class<T> tagClass) {
    return tags.stream()
        .filter(tagClass::isInstance)
        .map(tagClass::cast)
        .findFirst();
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    ApplicationManager.getApplication().invokeLater(() -> {
      var callParameters = getCallParameters(message, conversationType);
      if (callParameters.getImageDetails() != null) {
        project.getService(ChatToolWindowContentManager.class)
            .tryFindChatToolWindowPanel()
            .ifPresent(panel -> panel.clearImageNotifications(project));
      }

      totalTokensPanel.updateConversationTokens(conversation);
      if (callParameters.getReferencedFiles() != null) {
        totalTokensPanel.updateReferencedFilesTokens(callParameters.getReferencedFiles());
      }

      var userMessagePanel = createUserMessagePanel(message, callParameters);
      var responseMessagePanel = createResponseMessagePanel(callParameters);

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);
      messagePanel.add(responseMessagePanel);

      call(callParameters, responseMessagePanel, userMessagePanel);
    });
  }

  private boolean hasReferencedFilePaths(Message message) {
    return message.getReferencedFilePaths() != null && !message.getReferencedFilePaths().isEmpty();
  }

  private boolean hasReferencedFilePaths(Conversation conversation) {
    return conversation.getMessages().stream()
        .anyMatch(
            it -> it.getReferencedFilePaths() != null && !it.getReferencedFilePaths().isEmpty());
  }

  private UserMessagePanel createUserMessagePanel(
      Message message,
      ChatCompletionParameters callParameters) {
    var panel = new UserMessagePanel(project, message, this);
    panel.addCopyAction(() -> CopyAction.copyToClipboard(message.getPrompt()));
    panel.addReloadAction(() -> reloadMessage(callParameters, panel));
    panel.addDeleteAction(() -> removeMessage(message.getId(), conversation));
    return panel;
  }

  private ResponseMessagePanel createResponseMessagePanel(ChatCompletionParameters callParameters) {
    var message = callParameters.getMessage();
    var fileContextIncluded =
        hasReferencedFilePaths(message) || hasReferencedFilePaths(conversation);

    var panel = new ResponseMessagePanel();
    panel.addCopyAction(() -> CopyAction.copyToClipboard(message.getResponse()));
    panel.addContent(new ChatMessageResponseBody(
        project,
        true,
        false,
        message.isWebSearchIncluded(),
        fileContextIncluded || message.getDocumentationDetails() != null,
        this));
    return panel;
  }

  private void reloadMessage(ChatCompletionParameters prevParameters,
      UserMessagePanel userMessagePanel) {
    var prevMessage = prevParameters.getMessage();
    ResponseMessagePanel responsePanel = null;
    try {
      responsePanel = toolWindowScrollablePanel.getResponseMessagePanel(prevMessage.getId());
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      toolWindowScrollablePanel.update();
    } catch (Exception e) {
      throw new RuntimeException("Could not delete the existing message component", e);
    } finally {
      LOG.debug("Reloading message: " + prevMessage.getId());

      if (responsePanel != null) {
        prevMessage.setResponse("");
        conversationService.saveMessage(conversation, prevMessage);
        call(prevParameters.toBuilder().retry(true).build(), responsePanel, userMessagePanel);
      }

      totalTokensPanel.updateConversationTokens(conversation);

      TelemetryAction.IDE_ACTION.createActionMessage()
          .property("action", ActionType.RELOAD_MESSAGE.name())
          .send();
    }
  }

  private void removeMessage(UUID messageId, Conversation conversation) {
    toolWindowScrollablePanel.removeMessage(messageId);
    conversation.removeMessage(messageId);
    conversationService.saveConversation(conversation);
    totalTokensPanel.updateConversationTokens(conversation);

    if (conversation.getMessages().isEmpty()) {
      displayLandingView();
    }
  }

  private void clearWindow() {
    toolWindowScrollablePanel.clearAll();
    totalTokensPanel.updateConversationTokens(conversation);
  }

  private void call(
      ChatCompletionParameters callParameters,
      ResponseMessagePanel responseMessagePanel,
      UserMessagePanel userMessagePanel) {
    var responseContainer = (ChatMessageResponseBody) responseMessagePanel.getContent();

    if (!CompletionRequestService.isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    requestHandler = new ToolwindowChatCompletionRequestHandler(
        new ToolWindowCompletionResponseEventListener(
            conversationService,
            userMessagePanel,
            responseMessagePanel,
            totalTokensPanel,
            userInputPanel) {
          @Override
          public void handleTokensExceededPolicyAccepted() {
            call(callParameters, responseMessagePanel, userMessagePanel);
          }
        });
    userInputPanel.setSubmitEnabled(false);
    userMessagePanel.disableActions(List.of("RELOAD", "DELETE"));
    responseMessagePanel.disableActions(List.of("COPY"));

    ApplicationManager.getApplication()
        .executeOnPooledThread(() -> requestHandler.call(callParameters));
  }

  private Unit handleSubmit(String text, List<? extends HeaderTagDetails> appliedTags) {
    var messageBuilder = new MessageBuilder(project, text)
        .withSelectedEditorContent()
        .withInlays(appliedTags);

    List<ReferencedFile> referencedFiles = getReferencedFiles();
    if (!referencedFiles.isEmpty()) {
      messageBuilder.withReferencedFiles(referencedFiles);
    }

    String attachedImagePath = CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project);
    if (attachedImagePath != null) {
      messageBuilder.withImage(attachedImagePath);
    }

    sendMessage(messageBuilder.build(), ConversationType.DEFAULT);
    return Unit.INSTANCE;
  }

  private Unit handleCancel() {
    if (requestHandler != null) {
      requestHandler.cancel();
    }
    return Unit.INSTANCE;
  }

  private JPanel createUserPromptPanel() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    if (GeneralSettings.getSelectedService() != ServiceType.CODEGPT) {
      panel.add(JBUI.Panels.simplePanel(totalTokensPanel)
          .withBorder(JBUI.Borders.emptyBottom(8)), BorderLayout.NORTH);
    }
    panel.add(userInputPanel, BorderLayout.CENTER);
    return panel;
  }

  private JComponent getLandingView() {
    return new ChatToolWindowLandingPanel((action, locationOnScreen) -> {
      var editor = EditorUtil.getSelectedEditor(project);
      if (editor == null || !editor.getSelectionModel().hasSelection()) {
        OverlayUtil.showWarningBalloon(
            editor == null ? "Unable to locate a selected editor"
                : "Please select a target code before proceeding",
            locationOnScreen);
        return Unit.INSTANCE;
      }

      var fileExtension = FileUtil.getFileExtension(editor.getVirtualFile().getName());
      var message = new Message(action.getPrompt().replace(
          "{SELECTION}",
          format("%n```%s%n%s%n```", fileExtension, editor.getSelectionModel().getSelectedText())));
      sendMessage(message, ConversationType.DEFAULT);
      return Unit.INSTANCE;
    });
  }

  private void displayConversation() {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(getUserMessagePanel(message));
      messagePanel.add(getResponseMessagePanel(message));
    });
  }

  private UserMessagePanel getUserMessagePanel(Message message) {
    var userMessagePanel = new UserMessagePanel(project, message, this);
    userMessagePanel.addCopyAction(() -> CopyAction.copyToClipboard(message.getPrompt()));
    userMessagePanel.addReloadAction(() -> reloadMessage(
        ChatCompletionParameters.builder(conversation, message)
            .conversationType(ConversationType.DEFAULT)
            .build(),
        userMessagePanel));
    userMessagePanel.addDeleteAction(() -> removeMessage(message.getId(), conversation));
    var imageFilePath = message.getImageFilePath();
    if (imageFilePath != null && !imageFilePath.isEmpty()) {
      userMessagePanel.displayImage(imageFilePath);
    }
    return userMessagePanel;
  }

  private ResponseMessagePanel getResponseMessagePanel(Message message) {
    var response = message.getResponse() == null ? "" : message.getResponse();
    var messageResponseBody =
        new ChatMessageResponseBody(project, this).withResponse(response);

    messageResponseBody.hideCaret();

    var responseMessagePanel = new ResponseMessagePanel();
    responseMessagePanel.addContent(messageResponseBody);
    responseMessagePanel.addCopyAction(() -> CopyAction.copyToClipboard(message.getResponse()));
    return responseMessagePanel;
  }

  private JPanel createRootPanel() {
    var rootPanel = new JPanel(new BorderLayout());
    rootPanel.add(createScrollPaneWithSmartScroller(toolWindowScrollablePanel),
        BorderLayout.CENTER);
    rootPanel.add(createUserPromptPanel(), BorderLayout.SOUTH);
    return rootPanel;
  }
}