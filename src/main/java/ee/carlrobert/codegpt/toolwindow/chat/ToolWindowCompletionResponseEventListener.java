package ee.carlrobert.codegpt.toolwindow.chat;

import static com.intellij.openapi.ui.Messages.OK;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.completions.CompletionResponseEventListener;
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
import ee.carlrobert.codegpt.toolwindow.chat.components.UserPromptTextArea;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class ToolWindowCompletionResponseEventListener implements
    CompletionResponseEventListener {

  private static final Logger LOG = Logger.getInstance(
      ToolWindowCompletionResponseEventListener.class);

  private final StringBuilder messageBuilder = new StringBuilder();
  private final Map<UUID, List<YouSerpResult>> serpResultsMapping = new HashMap<>();
  private final EncodingManager encodingManager;
  private final ConversationService conversationService;
  private final ResponsePanel responsePanel;
  private final ChatMessageResponseBody responseContainer;
  private final TotalTokensPanel totalTokensPanel;
  private final UserPromptTextArea userPromptTextArea;

  private volatile boolean completed;

  public ToolWindowCompletionResponseEventListener(
      ConversationService conversationService,
      ResponsePanel responsePanel,
      TotalTokensPanel totalTokensPanel,
      UserPromptTextArea userPromptTextArea) {
    this.encodingManager = EncodingManager.getInstance();
    this.conversationService = conversationService;
    this.responsePanel = responsePanel;
    this.responseContainer = (ChatMessageResponseBody) responsePanel.getContent();
    this.totalTokensPanel = totalTokensPanel;
    this.userPromptTextArea = userPromptTextArea;
  }

  public abstract void handleTokensExceededPolicyAccepted();

  @Override
  public void handleMessage(String partialMessage) {
    try {
      ApplicationManager.getApplication()
          .invokeLater(() -> {
            responseContainer.update(partialMessage);
            messageBuilder.append(partialMessage);

            if (!completed) {
              var ongoingTokens = encodingManager.countTokens(messageBuilder.toString());
              totalTokensPanel.update(
                  totalTokensPanel.getTokenDetails().getTotal() + ongoingTokens);
            }
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
      handleTokensExceededPolicyAccepted();
    } else {
      stopStreaming(responseContainer);
    }
  }

  @Override
  public void handleCompleted(
      String fullMessage,
      Message message,
      Conversation conversation,
      boolean retry) {
    try {
      responsePanel.enableActions();
      conversationService.saveMessage(fullMessage, message, conversation, retry);

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
    completed = true;
    userPromptTextArea.setSubmitEnabled(true);
    responseContainer.hideCarets();
  }
}
