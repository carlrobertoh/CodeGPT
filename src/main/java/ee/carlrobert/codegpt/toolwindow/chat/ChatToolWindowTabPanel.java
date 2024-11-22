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
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatToolWindowScrollablePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensDetails;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensPanel;
import ee.carlrobert.codegpt.toolwindow.ui.ChatToolWindowLandingPanel;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.UserInputPanel;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import git4idea.GitCommit;
import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
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
        totalTokensPanel,
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

  public List<ReferencedFile> getReferencedFiles() {
    var referencedFiles = new LinkedHashMap<String, ReferencedFile>();

    conversation.getMessages().stream()
        .flatMap(prevMessage -> {
          if (prevMessage.getReferencedFilePaths() != null) {
            return prevMessage.getReferencedFilePaths().stream();
          }
          return Stream.empty();
        })
        .forEach(filePath -> {
          try {
            referencedFiles.put(filePath, new ReferencedFile(new File(filePath)));
          } catch (Exception ex) {
            LOG.error("Failed to create referenced file for path: " + filePath, ex);
          }
        });

    List<ReferencedFile> selectedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
    if (selectedFiles != null) {
      selectedFiles.forEach(file -> referencedFiles.put(file.getFilePath(), file));
    }

    return new ArrayList<>(referencedFiles.values());
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    ApplicationManager.getApplication().invokeLater(() -> {
      var callParameters = ChatCompletionParameters.builder(conversation, message)
          .sessionId(chatSession.getId())
          .conversationType(conversationType)
          .imageDetailsFromPath(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project))
          .persona(CodeGPTKeys.ADDED_PERSONA.get(project))
          .referencedFiles(getReferencedFiles())
          .build();

      CodeGPTKeys.ADDED_PERSONA.set(project, null);

      var referencedFiles = callParameters.getReferencedFiles();
      if ((referencedFiles != null && !referencedFiles.isEmpty())
          || callParameters.getImageDetails() != null) {
        project.getService(ChatToolWindowContentManager.class)
            .tryFindChatToolWindowPanel()
            .ifPresent(panel -> panel.clearNotifications(project));
      }

      totalTokensPanel.updateConversationTokens(conversation);
      if (callParameters.getReferencedFiles() != null) {
        totalTokensPanel.updateReferencedFilesTokens(callParameters.getReferencedFiles());
      }

      var responsePanel = createResponsePanel(callParameters);
      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(new UserMessagePanel(project, message, this));
      messagePanel.add(responsePanel);

      call(callParameters, responsePanel);
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

  private ResponsePanel createResponsePanel(ChatCompletionParameters callParameters) {
    var message = callParameters.getMessage();
    var fileContextIncluded =
        hasReferencedFilePaths(message) || hasReferencedFilePaths(conversation);

    return new ResponsePanel()
        .withReloadAction(() -> reloadMessage(callParameters))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(
            new ChatMessageResponseBody(
                project,
                true,
                false,
                message.isWebSearchIncluded(),
                fileContextIncluded || message.getDocumentationDetails() != null,
                this));
  }

  private void reloadMessage(ChatCompletionParameters prevParameters) {
    var prevMessage = prevParameters.getMessage();
    ResponsePanel responsePanel = null;
    try {
      responsePanel = toolWindowScrollablePanel.getMessageResponsePanel(prevMessage.getId());
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      toolWindowScrollablePanel.update();
    } catch (Exception e) {
      throw new RuntimeException("Could not delete the existing message component", e);
    } finally {
      LOG.debug("Reloading message: " + prevMessage.getId());

      if (responsePanel != null) {
        prevMessage.setResponse("");
        conversationService.saveMessage(conversation, prevMessage);
        call(prevParameters.toBuilder().retry(true).build(), responsePanel);
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

  private void call(ChatCompletionParameters callParameters, ResponsePanel responsePanel) {
    var responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    requestHandler = new ToolwindowChatCompletionRequestHandler(
        new ToolWindowCompletionResponseEventListener(
            conversationService,
            responsePanel,
            totalTokensPanel,
            userInputPanel) {
          @Override
          public void handleTokensExceededPolicyAccepted() {
            call(callParameters, responsePanel);
          }
        });
    userInputPanel.setSubmitEnabled(false);

    requestHandler.call(callParameters);
  }

  private Unit handleSubmit(String text, List<? extends AppliedActionInlay> appliedInlayActions) {
    var messageBuilder = new MessageBuilder(project, text)
        .withSelectedEditorContent()
        .withInlays(appliedInlayActions);

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
    panel.add(JBUI.Panels.simplePanel(totalTokensPanel)
        .withBorder(JBUI.Borders.emptyBottom(8)), BorderLayout.NORTH);
    panel.add(JBUI.Panels.simplePanel(userInputPanel), BorderLayout.CENTER);
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
          "{{selectedCode}}",
          format("%n```%s%n%s%n```", fileExtension, editor.getSelectionModel().getSelectedText())));
      sendMessage(message, ConversationType.DEFAULT);
      return Unit.INSTANCE;
    });
  }

  private void displayConversation() {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var response = message.getResponse() == null ? "" : message.getResponse();
      var messageResponseBody =
          new ChatMessageResponseBody(project, this).withResponse(response);

      messageResponseBody.hideCaret();

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var imageFilePath = message.getImageFilePath();
      if (imageFilePath != null && !imageFilePath.isEmpty()) {
        userMessagePanel.displayImage(imageFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);
      messagePanel.add(new ResponsePanel()
          .withReloadAction(() -> reloadMessage(
              ChatCompletionParameters.builder(conversation, message)
                  .conversationType(ConversationType.DEFAULT)
                  .build()))
          .withDeleteAction(() -> removeMessage(message.getId(), conversation))
          .addContent(messageResponseBody));
    });
  }

  private JPanel createRootPanel() {
    var rootPanel = new JPanel(new BorderLayout());
    rootPanel.add(createScrollPaneWithSmartScroller(toolWindowScrollablePanel),
        BorderLayout.CENTER);
    rootPanel.add(createUserPromptPanel(), BorderLayout.SOUTH);
    return rootPanel;
  }
}
