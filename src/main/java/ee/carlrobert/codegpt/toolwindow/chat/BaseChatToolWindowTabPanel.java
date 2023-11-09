package ee.carlrobert.codegpt.toolwindow.chat;

import static com.intellij.openapi.ui.Messages.OK;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ToolWindowCompletionEventListener;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.ModelIconLabel;
import ee.carlrobert.codegpt.toolwindow.chat.components.ChatMessageResponseBody;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.SmartScroller;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserMessagePanel;
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextArea;
import ee.carlrobert.codegpt.toolwindow.chat.components.YouProCheckbox;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseChatToolWindowTabPanel implements ChatToolWindowTabPanel {

  private static final Logger LOG = Logger.getInstance(BaseChatToolWindowTabPanel.class);

  private final SettingsState settings;
  private final YouUserManager youUserManager;

  private final boolean useContextualSearch;
  private final JPanel rootPanel;
  private final ScrollablePanel scrollablePanel;
  private final Map<UUID, JPanel> visibleMessagePanels = new HashMap<>();
  private final Map<UUID, List<YouSerpResult>> serpResultsMapping = new HashMap<>();
  private final JBCheckBox gpt4CheckBox;

  protected final Project project;
  protected final UserPromptTextArea userPromptTextArea;
  protected final ConversationService conversationService;
  protected @Nullable Conversation conversation;

  protected abstract JComponent getLandingView();

  public BaseChatToolWindowTabPanel(@NotNull Project project, boolean useContextualSearch) {
    this.project = project;
    this.useContextualSearch = useContextualSearch;
    this.conversationService = ConversationService.getInstance();
    this.scrollablePanel = new ScrollablePanel(new VerticalStackLayout());
    this.userPromptTextArea = new UserPromptTextArea(this::handleSubmit);
    this.gpt4CheckBox = new YouProCheckbox(project);
    this.settings = SettingsState.getInstance();
    this.youUserManager = YouUserManager.getInstance();
    this.rootPanel = createRootPanel();

    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
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
    if (settings.getSelectedService() == ServiceType.YOU &&
        (!youUserManager.isAuthenticated() || !youUserManager.isSubscribed())) {
      scrollablePanel.add(new ResponsePanel().addContent(createYouCouponTextPane()));
    }
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
    messageWrapper.add(new UserMessagePanel(
        project,
        message,
        message.getUserMessage() != null,
        this));
    var responsePanel = new ResponsePanel()
        .withReloadAction(() -> reloadMessage(message, conversation))
        .withDeleteAction(() -> removeMessage(message.getId(), messageWrapper, conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messageWrapper.add(responsePanel);

    call(conversation, message, responsePanel, false);
  }

  @Override
  public void dispose() {
  }

  protected void reloadMessage(Message message, Conversation conversation) {
    ResponsePanel responsePanel = null;
    try {
      responsePanel = (ResponsePanel) Arrays.stream(
              visibleMessagePanels.get(message.getId()).getComponents())
          .filter(component -> component instanceof ResponsePanel)
          .findFirst().orElseThrow();
      ((ChatMessageResponseBody) responsePanel.getContent()).clear();
      scrollablePanel.revalidate();
      scrollablePanel.repaint();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't delete the existing message component", e);
    } finally {
      LOG.debug("Reloading message: " + message.getId());

      if (responsePanel != null) {
        message.setResponse("");
        conversationService.saveMessage(conversation, message);
        call(conversation, message, responsePanel, true);
      }

      TelemetryAction.IDE_ACTION.createActionMessage()
          .property("action", ActionType.RELOAD_MESSAGE.name())
          .send();
    }
  }

  protected void removeMessage(UUID messageId, JPanel messageWrapper, Conversation conversation) {
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

  private static JScrollPane createScrollPane(ScrollablePanel scrollablePanel) {
    var scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    new SmartScroller(scrollPane);
    return scrollPane;
  }

  private JPanel createRootPanel() {
    var rootPanel = new JPanel(new GridBagLayout());
    var gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    gbc.weightx = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    rootPanel.add(createScrollPane(scrollablePanel), gbc);

    var wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    wrapper.setBackground(getPanelBackgroundColor());
    wrapper.add(createPromptTextAreaHeader(), BorderLayout.NORTH);
    wrapper.add(userPromptTextArea, BorderLayout.SOUTH);

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    rootPanel.add(wrapper, gbc);

    return rootPanel;
  }

  private JPanel createPromptTextAreaHeader() {
    var header = new JPanel(new BorderLayout());
    header.setBackground(getPanelBackgroundColor());
    header.setBorder(JBUI.Borders.emptyBottom(8));
    var model = settings.getModel();
    if ("YouCode".equals(model)) {
      var messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
      subscribeToYouModelChangeTopic();
      subscribeToYouSubscriptionTopic(messageBusConnection);
      subscribeToSignedOutTopic(messageBusConnection);
      header.add(gpt4CheckBox, BorderLayout.LINE_START);
    }
    header.add(JBUI.Panels
        .simplePanel(
            new ModelIconLabel(settings.getSelectedService().getCompletionCode(),
                model))
        .withBorder(Borders.emptyRight(4))
        .withBackground(getPanelBackgroundColor()), BorderLayout.LINE_END);
    return header;
  }

  private void subscribeToYouModelChangeTopic() {
    project.getMessageBus()
        .connect()
        .subscribe(
            YouModelChangeNotifier.YOU_MODEL_CHANGE_NOTIFIER_TOPIC,
            (YouModelChangeNotifier) gpt4CheckBox::setSelected);
  }

  private void subscribeToSignedOutTopic(MessageBusConnection messageBusConnection) {
    messageBusConnection.subscribe(
        SignedOutNotifier.SIGNED_OUT_TOPIC,
        (SignedOutNotifier) () -> gpt4CheckBox.setEnabled(false));
  }

  private void subscribeToYouSubscriptionTopic(MessageBusConnection messageBusConnection) {
    messageBusConnection.subscribe(
        YouSubscriptionNotifier.SUBSCRIPTION_TOPIC,
        (YouSubscriptionNotifier) () -> {
          displayLandingView();
          gpt4CheckBox.setEnabled(true);
        });
  }

  private JTextPane createYouCouponTextPane() {
    var textPane = SwingUtils.createTextPane(
        "<html>\n"
            + "<body>\n"
            + "  <p style=\"margin: 4px 0;\">Use CodeGPT coupon for free month of GPT-4.</p>\n"
            + "  <p style=\"margin: 4px 0;\">\n"
            + "    <a href=\"https://you.com/plans\">Sign up here</a>\n"
            + "  </p>\n"
            + "</body>\n"
            + "</html>"
    );
    textPane.setBackground(getPanelBackgroundColor());
    textPane.setFocusable(false);
    return textPane;
  }

  private class ChatToolWindowCompletionEventListener implements ToolWindowCompletionEventListener {

    private final Logger LOG = Logger.getInstance(ChatToolWindowCompletionEventListener.class);

    private final ResponsePanel responsePanel;
    private final ChatMessageResponseBody responseContainer;

    public ChatToolWindowCompletionEventListener(ResponsePanel responsePanel) {
      this.responsePanel = responsePanel;
      this.responseContainer = (ChatMessageResponseBody) responsePanel.getContent();
    }

    @Override
    public void handleMessage(String message) {
      try {
        LOG.debug(message);
        ApplicationManager.getApplication()
            .invokeLater(() -> responseContainer.update(message));
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
      responsePanel.enableActions();
      conversationService.saveMessage(fullMessage, message, conversation, isRetry);
      stopStreaming(responseContainer);

      var serpResults = serpResultsMapping.get(message.getId());
      var containsResults = serpResults != null && !serpResults.isEmpty();
      if (YouSettingsState.getInstance().isDisplayWebSearchResults() && containsResults) {
        responseContainer.displaySerpResults(serpResults);
      }

      if (containsResults) {
        message.setSerpResults(serpResults);
      }
    }

    @Override
    public void handleSerpResults(List<YouSerpResult> results, Message message) {
      serpResultsMapping.put(message.getId(), results);
    }

    private void stopStreaming(ChatMessageResponseBody responseContainer) {
      SwingUtilities.invokeLater(() -> {
        userPromptTextArea.setSubmitEnabled(true);
        responseContainer.hideCarets();
      });
    }
  }
}
