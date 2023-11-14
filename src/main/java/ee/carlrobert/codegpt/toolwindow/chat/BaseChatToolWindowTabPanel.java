package ee.carlrobert.codegpt.toolwindow.chat;

import static com.intellij.openapi.ui.Messages.OK;
import static ee.carlrobert.codegpt.util.SwingUtils.createScrollPaneWithSmartScroller;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ToolWindowCompletionEventListener;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.components.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.TotalTokensPanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextArea;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextAreaHeader;
import ee.carlrobert.codegpt.toolwindow.chat.components.YouProCheckbox;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseChatToolWindowTabPanel implements ChatToolWindowTabPanel {

  private static final Logger LOG = Logger.getInstance(BaseChatToolWindowTabPanel.class);

  private final SettingsState settings;
  private final boolean useContextualSearch;
  private final JPanel rootPanel;
  private final Map<UUID, List<YouSerpResult>> serpResultsMapping = new HashMap<>();
  private final JBCheckBox gpt4CheckBox;
  protected final TotalTokensPanel totalTokensPanel;
  protected final Project project;
  protected final UserPromptTextArea userPromptTextArea;
  protected final ConversationService conversationService;
  protected final ChatToolWindowScrollablePanel toolWindowScrollablePanel;
  private final EncodingManager encodingManager;

  private boolean streaming;
  protected @Nullable Conversation conversation;

  protected abstract JComponent getLandingView();

  public BaseChatToolWindowTabPanel(@NotNull Project project, boolean useContextualSearch) {
    this.project = project;
    this.useContextualSearch = useContextualSearch;
    conversationService = ConversationService.getInstance();
    encodingManager = EncodingManager.getInstance();
    settings = SettingsState.getInstance();
    toolWindowScrollablePanel = new ChatToolWindowScrollablePanel(settings);
    gpt4CheckBox = new YouProCheckbox(project);
    userPromptTextArea = new UserPromptTextArea(this::handleSubmit, getUserPromptDocumentAdapter());
    totalTokensPanel = new TotalTokensPanel(
        null,
        userPromptTextArea.getText(),
        EditorUtils.getSelectedEditorSelectedText(project));
    rootPanel = createRootPanel();

    addSelectionListeners();

    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }

  @Override
  public JPanel getContent() {
    return rootPanel;
  }

  @Override
  public @Nullable Conversation getConversation() {
    return conversation;
  }

  @Override
  public void setConversation(@Nullable Conversation conversation) {
    this.conversation = conversation;
  }

  @Override
  public void displayLandingView() {
    toolWindowScrollablePanel.displayLandingView(getLandingView());
  }

  @Override
  public void startNewConversation(Message message) {
    conversation = conversationService.startConversation();
    sendMessage(message);
  }

  @Override
  public void sendMessage(Message message) {
    streaming = true;
    if (conversation == null) {
      conversation = conversationService.startConversation();
    }

    var messagePanel = toolWindowScrollablePanel.addMessage(message.getId());
    messagePanel.add(new UserMessagePanel(project, message, this));
    var responsePanel = new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation))
        .withDeleteAction(() -> removeMessage(message.getId(), conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messagePanel.add(responsePanel);

    totalTokensPanel.updateUserPromptTokens(message.getPrompt());

    call(conversation, message, responsePanel, false);
  }

  @Override
  public TokenDetails getTokenDetails() {
    return totalTokensPanel.getTokenDetails();
  }

  @Override
  public void dispose() {
  }

  public void requestFocusForTextArea() {
    userPromptTextArea.focus();
  }

  public void updateConversationTokens() {
    totalTokensPanel.updateConversationTokens(conversation);
  }

  public boolean isStreaming() {
    return streaming;
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
        call(conversation, message, responsePanel, true);
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

    if (conversation.getMessages().isEmpty()) {
      conversationService.deleteConversation(conversation);
      setConversation(null);
      displayLandingView();
    }
  }

  protected void clearWindow() {
    toolWindowScrollablePanel.clearAll();
    totalTokensPanel.updateConversationTokens(conversation);
    updateConversationTokens();
  }

  private void call(
      Conversation conversation,
      Message message,
      ResponsePanel responsePanel,
      boolean isRetry) {
    ChatMessageResponseBody responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!CompletionRequestService.getInstance().isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler(
        useContextualSearch,
        new ChatToolWindowCompletionEventListener(responsePanel));
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);
    requestHandler.call(conversation, message, isRetry);
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

    if (conversation == null) {
      startNewConversation(message);
    } else {
      sendMessage(message);
    }
  }

  private JPanel createUserPromptPanel() {
    var panel = new JPanel(new BorderLayout());
    panel.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    panel.setBackground(getPanelBackgroundColor());
    panel.add(
        new UserPromptTextAreaHeader(project, settings, totalTokensPanel, gpt4CheckBox),
        BorderLayout.NORTH);
    panel.add(userPromptTextArea, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createRootPanel() {
    var rootPanel = new JPanel(new GridBagLayout());
    var gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    rootPanel.add(createScrollPaneWithSmartScroller(toolWindowScrollablePanel), gbc);

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    rootPanel.add(createUserPromptPanel(), gbc);
    return rootPanel;
  }

  private class ChatToolWindowCompletionEventListener implements ToolWindowCompletionEventListener {

    private final Logger LOG = Logger.getInstance(ChatToolWindowCompletionEventListener.class);

    private final StringBuilder messageBuilder = new StringBuilder();
    private final ResponsePanel responsePanel;
    private final ChatMessageResponseBody responseContainer;

    public ChatToolWindowCompletionEventListener(ResponsePanel responsePanel) {
      this.responsePanel = responsePanel;
      this.responseContainer = (ChatMessageResponseBody) responsePanel.getContent();
    }

    @Override
    public void handleMessage(String partialMessage) {
      try {
        LOG.debug(partialMessage);
        ApplicationManager.getApplication()
            .invokeLater(() -> {
              responseContainer.update(partialMessage);
              messageBuilder.append(partialMessage);

              var ongoingTokens = encodingManager.countTokens(messageBuilder.toString());
              totalTokensPanel.update(
                  totalTokensPanel.getTokenDetails().getTotal() + ongoingTokens);
            });
      } catch (Exception e) {
        responseContainer.displayDefaultError();
        throw new RuntimeException("Error while updating the content", e);
      }
    }

    @Override
    public void handleError(ErrorDetails error, Throwable ex) {
      try {
        if ("insufficient_quota".equals(error.getCode())) {
          if (SettingsState.getInstance().getSelectedService() == ServiceType.OPENAI) {
            OpenAISettingsState.getInstance().setOpenAIQuotaExceeded(true);
          }
          responseContainer.displayQuotaExceeded();
        } else {
          responseContainer.displayError(error.getMessage());
        }
      } finally {
        LOG.error(error.getMessage(), ex);
        responsePanel.enableActions();
        stopStreaming(responseContainer);
      }
    }

    @Override
    public void handleTokensExceeded(Conversation conversation, Message message) {
      var answer = OverlayUtils.showTokenLimitExceededDialog();
      if (answer == OK) {
        TelemetryAction.IDE_ACTION.createActionMessage()
            .property("action", "DISCARD_TOKEN_LIMIT")
            .property("model", conversation.getModel())
            .send();

        conversationService.discardTokenLimits(conversation);
        call(conversation, message, responsePanel, true);
      } else {
        stopStreaming(responseContainer);
      }
    }

    @Override
    public void handleCompleted(
        String fullMessage,
        Message message,
        Conversation conversation,
        boolean isRetry) {
      try {
        responsePanel.enableActions();
        conversationService.saveMessage(fullMessage, message, conversation, isRetry);

        var serpResults = serpResultsMapping.get(message.getId());
        var containsResults = serpResults != null && !serpResults.isEmpty();
        if (YouSettingsState.getInstance().isDisplayWebSearchResults() && containsResults) {
          responseContainer.displaySerpResults(serpResults);
        }

        if (containsResults) {
          message.setSerpResults(serpResults);
        }

        totalTokensPanel.updateUserPromptTokens(userPromptTextArea.getText());
        totalTokensPanel.updateConversationTokens(conversation);
      } finally {
        stopStreaming(responseContainer);
      }
    }

    @Override
    public void handleSerpResults(List<YouSerpResult> results, Message message) {
      serpResultsMapping.put(message.getId(), results);
    }

    private void stopStreaming(ChatMessageResponseBody responseContainer) {
      streaming = false;
      userPromptTextArea.setSubmitEnabled(true);
      responseContainer.hideCarets();
    }
  }

  private void addSelectionListeners() {
    var editorFactory = EditorFactory.getInstance();
    for (var editor : editorFactory.getAllEditors()) {
      editor.getSelectionModel().addSelectionListener(getSelectionListener());
    }
    editorFactory.addEditorFactoryListener(new EditorFactoryListener() {
      @Override
      public void editorCreated(@NotNull EditorFactoryEvent event) {
        event.getEditor().getSelectionModel().addSelectionListener(getSelectionListener());
      }
    }, this);
  }

  private SelectionListener getSelectionListener() {
    return new SelectionListener() {
      @Override
      public void selectionChanged(@NotNull SelectionEvent e) {
        var selectedText = e.getEditor().getDocument().getText(e.getNewRange());
        totalTokensPanel.updateHighlightedTokens(selectedText);
      }
    };
  }

  private DocumentAdapter getUserPromptDocumentAdapter() {
    return new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent event) {
        try {
          if (!streaming) {
            var document = event.getDocument();
            var text = document.getText(
                document.getStartPosition().getOffset(),
                document.getEndPosition().getOffset() - 1);
            totalTokensPanel.updateUserPromptTokens(text);
          }
        } catch (BadLocationException ex) {
          LOG.error("Something went wrong while processing user input tokens", ex);
        }
      }
    };
  }
}
