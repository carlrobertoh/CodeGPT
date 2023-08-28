package ee.carlrobert.codegpt.toolwindow.chat;

import static com.intellij.openapi.ui.Messages.OK;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;

import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.components.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextArea;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.FileUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseChatToolWindowTabPanel implements ChatToolWindowTabPanel {

  private final boolean useContextualSearch;
  private final JPanel rootPanel;
  private final ScrollablePanel scrollablePanel;
  private final Map<UUID, JPanel> visibleMessagePanels = new HashMap<>();

  protected final Project project;
  protected final UserPromptTextArea userPromptTextArea;
  protected final ConversationService conversationService;
  protected @Nullable Conversation conversation;

  protected abstract JComponent getLandingView();

  public BaseChatToolWindowTabPanel(@NotNull Project project, boolean useContextualSearch) {
    this.project = project;
    this.useContextualSearch = useContextualSearch;
    this.conversationService = ConversationService.getInstance();
    this.rootPanel = new JPanel(new GridBagLayout());
    this.scrollablePanel = new ScrollablePanel();
    this.userPromptTextArea = new UserPromptTextArea(this::handleSubmit);
    init();
  }

  public void requestFocusForTextArea() {
    userPromptTextArea.focus();
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
    scrollablePanel.removeAll();
    scrollablePanel.add(getLandingView());
    scrollablePanel.repaint();
    scrollablePanel.revalidate();
  }

  @Override
  public void startNewConversation(Message message) {
    conversation = conversationService.startConversation();
    sendMessage(message);
  }

  @Override
  public void sendMessage(Message message) {
    if (conversation == null) {
      conversation = conversationService.startConversation();
    }

    var messageWrapper = createNewMessageWrapper(message.getId());
    messageWrapper.add(new UserMessagePanel(project, message, message.getUserMessage() != null, this));
    var responsePanel = new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation))
        .withDeleteAction(() -> deleteMessage(message.getId(), messageWrapper, conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messageWrapper.add(responsePanel);
    call(conversation, message, responsePanel, false);
  }

  @Override
  public void dispose() {
  }

  private boolean isCredentialSet() {
    if (SettingsState.getInstance().isUseAzureService()) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    return OpenAICredentialsManager.getInstance().isApiKeySet();
  }

  private void call(Conversation conversation, Message message, ResponsePanel responsePanel, boolean isRetry) {
    ChatMessageResponseBody responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!isCredentialSet()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler();
    requestHandler.withContextualSearch(useContextualSearch);
    requestHandler.addMessageListener(partialMessage -> {
      try {
        responseContainer.update(partialMessage);
      } catch (Exception e) {
        responseContainer.displayDefaultError();
        throw new RuntimeException("Error while updating the content", e);
      }
    });
    requestHandler.addRequestCompletedListener(completeMessage -> {
      responsePanel.enableActions();
      conversationService.saveMessage(completeMessage, message, conversation, isRetry);
      stopStreaming(responseContainer);
    });
    requestHandler.addTokensExceededListener(() -> SwingUtilities.invokeLater(() -> {
      var answer = OverlayUtils.showTokenLimitExceededDialog();
      if (answer == OK) {
        conversationService.discardTokenLimits(conversation);
        requestHandler.call(conversation, message, true);
      } else {
        stopStreaming(responseContainer);
      }
    }));
    requestHandler.addErrorListener((error, ex) -> {
      responsePanel.enableActions();
      responseContainer.displayError(error.getMessage());
      stopStreaming(responseContainer);
    });
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);
    requestHandler.call(conversation, message, isRetry);
  }

  protected void reloadMessage(Message message, Conversation conversation) {
    ResponsePanel responsePanel = null;
    try {
      responsePanel = (ResponsePanel) Arrays.stream(visibleMessagePanels.get(message.getId()).getComponents())
          .filter(component -> component instanceof ResponsePanel)
          .findFirst().orElseThrow();
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      scrollablePanel.revalidate();
      scrollablePanel.repaint();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't reload message", e);
    } finally {
      if (responsePanel != null) {
        message.setResponse("");
        conversationService.saveMessage(conversation, message);
        call(conversation, message, responsePanel, true);
      }
    }
  }

  protected void deleteMessage(UUID messageId, JPanel messageWrapper, Conversation conversation) {
    scrollablePanel.remove(messageWrapper);
    scrollablePanel.repaint();
    scrollablePanel.revalidate();

    visibleMessagePanels.remove(messageId);
    conversation.removeMessage(messageId);
    conversationService.saveConversation(conversation);

    if (conversation.getMessages().isEmpty()) {
      conversationService.deleteConversation(conversation);
      setConversation(null);
      displayLandingView();
    }
  }

  protected JPanel createNewMessageWrapper(UUID messageId) {
    var messageWrapper = new JPanel();
    messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.PAGE_AXIS));
    scrollablePanel.add(messageWrapper);
    scrollablePanel.repaint();
    scrollablePanel.revalidate();
    visibleMessagePanels.put(messageId, messageWrapper);
    return messageWrapper;
  }

  protected void clearWindow() {
    scrollablePanel.removeAll();
    scrollablePanel.revalidate();
    scrollablePanel.repaint();
  }

  private void stopStreaming(ChatMessageResponseBody responseContainer) {
    SwingUtilities.invokeLater(() -> {
      userPromptTextArea.setSubmitEnabled(true);
      responseContainer.hideCarets();
    });
  }

  private void handleSubmit(String text) {
    var message = new Message(text);
    var editor = EditorUtils.getSelectedEditor(project);
    if (editor != null) {
      var selectionModel = editor.getSelectionModel();
      var selectedText = selectionModel.getSelectedText();
      if (selectedText != null && !selectedText.isEmpty()) {
        var fileExtension = FileUtils.getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
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

  private void init() {
    var gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
    JBScrollPane scrollPane = new JBScrollPane(scrollablePanel);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
    rootPanel.add(scrollPane, gbc);
    new SmartScroller(scrollPane);

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;

    // JBUI.Panels.simplePanel(8, 0).add();
    JPanel chatTextAreaWrapper = new JPanel(new BorderLayout());
    chatTextAreaWrapper.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    chatTextAreaWrapper.setBackground(getPanelBackgroundColor());
    chatTextAreaWrapper.add(userPromptTextArea, BorderLayout.SOUTH);
    rootPanel.add(chatTextAreaWrapper, gbc);
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }
}
