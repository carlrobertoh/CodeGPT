package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.getPromptWithContext;
import static ee.carlrobert.codegpt.ui.UIUtil.createScrollPaneWithSmartScroller;
import static java.lang.String.format;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ConversationType;
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
import ee.carlrobert.codegpt.ui.textarea.AppliedCodeActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedSuggestionActionInlay;
import ee.carlrobert.codegpt.ui.textarea.UserInputPanel;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.CreateDocumentationActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.DocumentationActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.GitCommitActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.PersonaActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.WebSearchActionItem;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.file.FileUtil;
import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JPanel;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatToolWindowTabPanel implements Disposable {

  private static final Logger LOG = Logger.getInstance(ChatToolWindowTabPanel.class);

  private final Project project;
  private final JPanel rootPanel;
  private final Conversation conversation;
  private final UserInputPanel userInputPanel;
  private final ConversationService conversationService;
  private final TotalTokensPanel totalTokensPanel;
  private final ChatToolWindowScrollablePanel toolWindowScrollablePanel;

  private @Nullable CompletionRequestHandler requestHandler;

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
      displayConversation(conversation);
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

  public void sendMessage(Message message) {
    sendMessage(message, ConversationType.DEFAULT);
  }

  public void sendMessage(Message message, ConversationType conversationType) {
    sendMessage(message, conversationType, null);
  }

  public void sendMessage(
      Message message,
      ConversationType conversationType,
      @Nullable String highlightedText) {
    ApplicationManager.getApplication().invokeLater(() -> {
      var referencedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
      var chatToolWindowPanel = project.getService(ChatToolWindowContentManager.class)
          .tryFindChatToolWindowPanel();
      if (referencedFiles != null && !referencedFiles.isEmpty()) {
        var referencedFilePaths = referencedFiles.stream()
            .map(ReferencedFile::getFilePath)
            .toList();
        message.setReferencedFilePaths(referencedFilePaths);
        message.setUserMessage(message.getPrompt());
        message.setPrompt(getPromptWithContext(referencedFiles, message.getPrompt()));

        totalTokensPanel.updateReferencedFilesTokens(referencedFiles);

        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
      }
      totalTokensPanel.updateConversationTokens(conversation);

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var attachedFilePath = CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.get(project);
      var callParameters =
          getCallParameters(conversationType, message, highlightedText, attachedFilePath);
      if (callParameters.getImageData() != null) {
        message.setImageFilePath(attachedFilePath);
        chatToolWindowPanel.ifPresent(panel -> panel.clearNotifications(project));
        userMessagePanel.displayImage(attachedFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);

      var responsePanel = createResponsePanel(callParameters, conversationType);
      messagePanel.add(responsePanel);
      call(callParameters, responsePanel);
    });
  }

  private CallParameters getCallParameters(
      ConversationType conversationType,
      Message message,
      @Nullable String highlightedText,
      @Nullable String attachedFilePath) {
    var callParameters = new CallParameters(conversation, conversationType, message,
        highlightedText, false);
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

  private ResponsePanel createResponsePanel(
      CallParameters callParameters,
      ConversationType conversationType) {
    var message = callParameters.getMessage();
    return new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation, conversationType))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(
            new ChatMessageResponseBody(
                project,
                callParameters.getHighlightedText(),
                true,
                false,
                message.isWebSearchIncluded(),
                message.getDocumentationDetails() != null, this));
  }

  private void reloadMessage(
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
        call(new CallParameters(conversation, conversationType, message, null, true),
            responsePanel);
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

  private void call(CallParameters callParameters, ResponsePanel responsePanel) {
    var responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.getInstance().isAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    requestHandler = new CompletionRequestHandler(
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
    var message = new Message(text);
    var editor = EditorUtil.getSelectedEditor(project);
    String highlightedText = null;
    if (editor != null) {
      var selectionModel = editor.getSelectionModel();
      var selectedText = selectionModel.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        var fileExtension = FileUtil.getFileExtension(editor.getVirtualFile().getName());
        message = new Message(text + format("%n```%s%n%s%n```", fileExtension, selectedText));
        highlightedText = selectedText;
        selectionModel.removeSelection();
      }
    }
    message.setUserMessage(text);
    processAppliedInlayActions(message, appliedInlayActions, text, editor);
    sendMessage(message, ConversationType.DEFAULT, highlightedText);
    return Unit.INSTANCE;
  }

  private void processAppliedInlayActions(
      Message message,
      List<? extends AppliedActionInlay> appliedInlayActions,
      String text,
      Editor editor) {
    for (var action : appliedInlayActions) {
      if (action instanceof AppliedSuggestionActionInlay) {
        processSuggestionActions(
            message,
            filterActions(appliedInlayActions, AppliedSuggestionActionInlay.class),
            text);
      } else if (action instanceof AppliedCodeActionInlay) {
        processCodeActions(
            message,
            filterActions(appliedInlayActions, AppliedCodeActionInlay.class),
            text,
            editor);
      }
    }
  }

  private <T extends AppliedActionInlay> List<T> filterActions(
      List<? extends AppliedActionInlay> actions,
      Class<T> actionClass) {
    return actions.stream()
        .filter(actionClass::isInstance)
        .map(actionClass::cast)
        .toList();
  }

  private boolean containsWebSearchActionInlay(List<AppliedSuggestionActionInlay> actions) {
    return actions.stream().anyMatch(it -> it.getSuggestion() instanceof WebSearchActionItem);
  }

  private void processSuggestionActions(
      Message message,
      List<AppliedSuggestionActionInlay> actions,
      String text) {
    message.setWebSearchIncluded(containsWebSearchActionInlay(actions));
    processDocumentationAction(message, actions);
    processPersonaAction(message, actions);
    processGitCommitAction(message, actions, text);
  }

  private void processDocumentationAction(
      Message message,
      List<AppliedSuggestionActionInlay> actions) {
    var addedDocumentation = CodeGPTKeys.ADDED_DOCUMENTATION.get(project);
    var appliedInlayExists = actions.stream().anyMatch(it -> {
      var suggestion = it.getSuggestion();
      return suggestion instanceof DocumentationActionItem
          || suggestion instanceof CreateDocumentationActionItem;
    });

    if (addedDocumentation != null && appliedInlayExists) {
      message.setDocumentationDetails(addedDocumentation);
      CodeGPTKeys.ADDED_DOCUMENTATION.set(project, null);
    }
  }

  private void processPersonaAction(Message message, List<AppliedSuggestionActionInlay> actions) {
    var addedPersona = CodeGPTKeys.ADDED_PERSONA.get(project);
    var personaInlayExists = actions.stream()
        .anyMatch(it -> it.getSuggestion() instanceof PersonaActionItem);

    if (addedPersona != null && personaInlayExists) {
      message.setPersonaDetails(addedPersona);
      CodeGPTKeys.ADDED_PERSONA.set(project, null);
    }
  }

  private <T extends AppliedActionInlay> void processActions(
      Message message,
      List<T> actions,
      String text,
      Function<T, String> codeExtractor,
      Function<T, String> languageExtractor) {
    var stringBuilder = new StringBuilder(text);
    var resultStringBuilder = new StringBuilder();
    int lastProcessedIndex = 0;

    for (var actionInlay : actions) {
      var inlayOffset = actionInlay.getInlay().getOffset();

      resultStringBuilder
          .append(stringBuilder, lastProcessedIndex, Math.min(stringBuilder.length(), inlayOffset))
          .append('\n')
          .append(formatCodeBlock(languageExtractor.apply(actionInlay),
              codeExtractor.apply(actionInlay)))
          .append('\n');

      lastProcessedIndex = inlayOffset;
    }

    resultStringBuilder.append(stringBuilder, lastProcessedIndex, stringBuilder.length());

    var result = resultStringBuilder.toString();
    message.setUserMessage(result);
    message.setPrompt(result);
  }

  private void processGitCommitAction(
      Message message,
      List<AppliedSuggestionActionInlay> actions,
      String text) {
    var gitCommitInlays = actions.stream()
        .filter(it -> it.getSuggestion() instanceof GitCommitActionItem)
        .toList();

    if (!gitCommitInlays.isEmpty()) {
      processActions(
          message,
          gitCommitInlays,
          text,
          action -> ((GitCommitActionItem) action.getSuggestion()).getDiffString(),
          action -> "shell"
      );
    }
  }

  private void processCodeActions(
      Message message,
      List<AppliedCodeActionInlay> actions,
      String text,
      Editor editor) {
    processActions(
        message,
        actions,
        text,
        AppliedCodeActionInlay::getCode,
        action -> FileUtil.getFileExtension(editor.getVirtualFile().getName())
    );
  }

  private String formatCodeBlock(String fileExtension, String code) {
    return String.format("```%s\n%s\n```", fileExtension, code);
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
      message.setUserMessage(action.getUserMessage());

      sendMessage(message, ConversationType.DEFAULT);
      return Unit.INSTANCE;
    });
  }

  private void displayConversation(@NotNull Conversation conversation) {
    clearWindow();
    conversation.getMessages().forEach(message -> {
      var messageResponseBody =
          new ChatMessageResponseBody(project, this).withResponse(message.getResponse());

      messageResponseBody.hideCaret();

      var userMessagePanel = new UserMessagePanel(project, message, this);
      var imageFilePath = message.getImageFilePath();
      if (imageFilePath != null && !imageFilePath.isEmpty()) {
        userMessagePanel.displayImage(imageFilePath);
      }

      var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
      messagePanel.add(userMessagePanel);
      messagePanel.add(new ResponsePanel()
          .withReloadAction(() -> reloadMessage(message, conversation, ConversationType.DEFAULT))
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

  public void addSelection(String fileName, SelectionModel selectionModel) {
    userInputPanel.addSelection(fileName, selectionModel);
  }
}
