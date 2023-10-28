package ee.carlrobert.codegpt.toolwindow.chat;

import static com.intellij.openapi.ui.Messages.OK;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.you.YouSerpResult;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
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
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.codegpt.util.file.FileUtils;
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
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseChatToolWindowTabPanel implements ChatToolWindowTabPanel {

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
    this.rootPanel = new JPanel(new GridBagLayout());
    this.scrollablePanel = new ScrollablePanel();
    this.userPromptTextArea = new UserPromptTextArea(this::handleSubmit);
    this.gpt4CheckBox = createGPT4ModelCheckBox();
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
    var youUserManager = YouUserManager.getInstance();
    if (SettingsState.getInstance().isUseYouService() &&
        (!youUserManager.isAuthenticated() || !youUserManager.isSubscribed())) {
      scrollablePanel.add(new ResponsePanel().addContent(createTextPane()));
    }
    scrollablePanel.repaint();
    scrollablePanel.revalidate();
  }

  private JTextPane createTextPane() {
    var textPane = new JTextPane();
    textPane.addHyperlinkListener(SwingUtils::handleHyperlinkClicked);
    textPane.setBackground(getPanelBackgroundColor());
    textPane.setContentType("text/html");
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.setFocusable(false);
    textPane.setEditable(false);
    textPane.setText(
        "<html>\n"
            + "<body>\n"
            + "  <p style=\"margin: 4px 0;\">Use CodeGPT coupon for free month of GPT-4.</p>\n"
            + "  <p style=\"margin: 4px 0;\">\n"
            + "    <a href=\"https://you.com/plans\">Sign up here</a>\n"
            + "  </p>\n"
            + "</body>\n"
            + "</html>");
    return textPane;
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
        .withDeleteAction(() -> deleteMessage(message.getId(), messageWrapper, conversation))
        .addContent(new ChatMessageResponseBody(project, true, this));
    messageWrapper.add(responsePanel);
    call(conversation, message, responsePanel, false);
  }

  @Override
  public void dispose() {
  }

  private boolean isRequestAllowed() {
    if (SettingsState.getInstance().isUseAzureService()) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    if (SettingsState.getInstance().isUseYouService()) {
      return true;
    }
    return OpenAICredentialsManager.getInstance().isApiKeySet();
  }

  private void call(
      Conversation conversation,
      Message message,
      ResponsePanel responsePanel,
      boolean isRetry) {
    ChatMessageResponseBody responseContainer = (ChatMessageResponseBody) responsePanel.getContent();

    if (!isRequestAllowed()) {
      responseContainer.displayMissingCredential();
      return;
    }

    var requestHandler = new CompletionRequestHandler();
    requestHandler.withContextualSearch(useContextualSearch);
    requestHandler.addMessageListener(partialMessage -> {
      try {
        ApplicationManager.getApplication()
            .invokeLater(() -> responseContainer.update(partialMessage));
      } catch (Exception e) {
        responseContainer.displayDefaultError();
        throw new RuntimeException("Error while updating the content", e);
      }
    });
    requestHandler.addRequestCompletedListener(completeMessage -> {
      responsePanel.enableActions();
      conversationService.saveMessage(completeMessage, message, conversation, isRetry);
      stopStreaming(responseContainer);

      var serpResults = serpResultsMapping.get(message.getId());
      var containsResults = serpResults != null && !serpResults.isEmpty();
      if (YouSettingsState.getInstance().isDisplayWebSearchResults()) {
        if (containsResults) {
          responseContainer.displaySerpResults(serpResults);
        }
      }

      if (containsResults) {
        message.setSerpResults(serpResults.stream()
            .map(result -> new YouSerpResult(
                result.getUrl(),
                result.getName(),
                result.getSnippet(),
                result.getSnippetSource()))
            .collect(toList()));
      }
    });
    requestHandler.addTokensExceededListener(() -> SwingUtilities.invokeLater(() -> {
      var answer = OverlayUtils.showTokenLimitExceededDialog();
      if (answer == OK) {
        TelemetryAction.IDE_ACTION.createActionMessage()
            .property("action", "DISCARD_TOKEN_LIMIT")
            .property("model", conversation.getModel())
            .send();

        conversationService.discardTokenLimits(conversation);
        requestHandler.call(conversation, message, true);
      } else {
        stopStreaming(responseContainer);
      }
    }));
    requestHandler.addErrorListener((error, ex) -> {
      try {
        if ("insufficient_quota".equals(error.getCode())) {
          if (SettingsState.getInstance().isUseOpenAIService()) {
            OpenAISettingsState.getInstance().setOpenAIQuotaExceeded(true);
          }
          responseContainer.displayQuotaExceeded();
        } else {
          responseContainer.displayError(error.getMessage());
        }
      } finally {
        responsePanel.enableActions();
        stopStreaming(responseContainer);
      }
    });
    requestHandler.addSerpResultsListener(
        serpResults -> serpResultsMapping.put(message.getId(), serpResults.stream()
            .map(result -> new YouSerpResult(
                result.getUrl(),
                result.getName(),
                result.getSnippet(),
                result.getSnippetSource()))
            .collect(toList())));
    userPromptTextArea.setRequestHandler(requestHandler);
    userPromptTextArea.setSubmitEnabled(false);
    requestHandler.call(conversation, message, isRetry);
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
      throw new RuntimeException("Couldn't reload message", e);
    } finally {
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

    var model = getModel();
    var modelIconWrapper = JBUI.Panels
        .simplePanel(new ModelIconLabel(getClientCode(), model))
        .withBorder(Borders.emptyRight(4))
        .withBackground(getPanelBackgroundColor());

    var wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8)));
    wrapper.setBackground(getPanelBackgroundColor());
    wrapper.add(userPromptTextArea, BorderLayout.SOUTH);
    if (model != null) {
      var header = new JPanel(new BorderLayout());
      header.setBackground(getPanelBackgroundColor());
      header.setBorder(JBUI.Borders.emptyBottom(8));
      if ("YouCode".equals(model)) {
        var messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        subscribeToYouModelChangeTopic();
        subscribeToYouSubscriptionTopic(messageBusConnection);
        subscribeToSignedOutTopic(messageBusConnection);
        header.add(gpt4CheckBox, BorderLayout.LINE_START);
      }
      header.add(modelIconWrapper, BorderLayout.LINE_END);
      wrapper.add(header);
    }
    rootPanel.add(wrapper, gbc);
    userPromptTextArea.requestFocusInWindow();
    userPromptTextArea.requestFocus();
  }

  private void subscribeToSignedOutTopic(MessageBusConnection messageBusConnection) {
    messageBusConnection.subscribe(
        SignedOutNotifier.SIGNED_OUT_TOPIC,
        (SignedOutNotifier) () -> gpt4CheckBox.setEnabled(false));
  }

  private void subscribeToYouModelChangeTopic() {
    project.getMessageBus()
        .connect()
        .subscribe(
            YouModelChangeNotifier.YOU_MODEL_CHANGE_NOTIFIER_TOPIC,
            (YouModelChangeNotifier) gpt4CheckBox::setSelected);
  }

  private void subscribeToYouSubscriptionTopic(MessageBusConnection messageBusConnection) {
    messageBusConnection.subscribe(
        YouSubscriptionNotifier.SUBSCRIPTION_TOPIC,
        (YouSubscriptionNotifier) () -> {
          displayLandingView();
          gpt4CheckBox.setEnabled(true);
        });
  }

  private JBCheckBox createGPT4ModelCheckBox() {
    var gpt4CheckBox = new JBCheckBox("Use GPT-4 model");
    gpt4CheckBox.setOpaque(false);
    gpt4CheckBox.setEnabled(YouUserManager.getInstance().isSubscribed());
    gpt4CheckBox.setSelected(YouSettingsState.getInstance().isUseGPT4Model());
    gpt4CheckBox.setToolTipText(getTooltipText(gpt4CheckBox.isSelected()));
    gpt4CheckBox.addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      var tooltipText = getTooltipText(selected);
      gpt4CheckBox.setToolTipText(tooltipText);
      // TODO: Remove
      project.getMessageBus()
          .syncPublisher(YouModelChangeNotifier.YOU_MODEL_CHANGE_NOTIFIER_TOPIC)
          .modelChanged(selected);
      YouSettingsState.getInstance().setUseGPT4Model(selected);
    });
    return gpt4CheckBox;
  }

  private String getTooltipText(boolean selected) {
    if (YouUserManager.getInstance().isSubscribed()) {
      return selected ? "Turn off for faster responses" : "Turn on for complex queries";
    }
    return "Enable by subscribing to YouPro plan";
  }

  private String getClientCode() {
    var settings = SettingsState.getInstance();
    if (settings.isUseOpenAIService()) {
      return "chat.completion";
    }
    if (settings.isUseAzureService()) {
      return "azure.chat.completion";
    }
    if (settings.isUseYouService()) {
      return "you.chat.completion";
    }
    if (settings.isUseLlamaService()) {
      return "llama.chat.completion";
    }
    return null;
  }

  private @Nullable String getModel() {
    var settings = SettingsState.getInstance();
    if (settings.isUseOpenAIService()) {
      return OpenAISettingsState.getInstance().getModel();
    }
    if (settings.isUseAzureService()) {
      return AzureSettingsState.getInstance().getModel();
    }
    if (settings.isUseYouService()) {
      return "YouCode";
    }
    if (settings.isUseLlamaService()) {
      var huggingFaceModel = LlamaSettingsState.getInstance().getHuggingFaceModel();
      var llamaModel = LlamaModel.findByHuggingFaceModel(huggingFaceModel);
      return String.format(
          "%s %dB (%d - bits)",
          llamaModel,
          huggingFaceModel.getParameterSize(),
          huggingFaceModel.getQuantization());
    }

    return "Unknown";
  }
}
