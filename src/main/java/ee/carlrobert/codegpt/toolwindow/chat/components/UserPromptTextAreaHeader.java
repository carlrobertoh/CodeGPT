package ee.carlrobert.codegpt.toolwindow.chat.components;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.toolwindow.chat.standard.ModelComboBoxAction;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class UserPromptTextAreaHeader extends JPanel {

  public UserPromptTextAreaHeader(
      ServiceType selectedService,
      TotalTokensPanel totalTokensPanel,
      Runnable onAddNewTab) {
    super(new BorderLayout());
    setOpaque(false);
    setBorder(JBUI.Borders.emptyBottom(4));
    switch (selectedService) {
      case OPENAI:
      case AZURE:
        add(totalTokensPanel, BorderLayout.LINE_START);
        break;
      case YOU:
        JBCheckBox gpt4CheckBox = new YouProCheckbox();
        subscribeToYouTopics(gpt4CheckBox);
        add(gpt4CheckBox, BorderLayout.LINE_START);
        break;
      default:
    }
    add(new ModelComboBoxAction(onAddNewTab, selectedService)
        .createCustomComponent(ActionPlaces.UNKNOWN), BorderLayout.LINE_END);
  }

  private void subscribeToYouTopics(JBCheckBox gpt4CheckBox) {
    var messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
    subscribeToYouSubscriptionTopic(messageBusConnection, gpt4CheckBox);
    subscribeToSignedOutTopic(messageBusConnection, gpt4CheckBox);
  }

  private void subscribeToSignedOutTopic(
      MessageBusConnection messageBusConnection,
      JBCheckBox gpt4CheckBox) {
    messageBusConnection.subscribe(
        SignedOutNotifier.SIGNED_OUT_TOPIC,
        (SignedOutNotifier) () -> gpt4CheckBox.setEnabled(false));
  }

  private void subscribeToYouSubscriptionTopic(
      MessageBusConnection messageBusConnection,
      JBCheckBox gpt4CheckBox) {
    messageBusConnection.subscribe(
        YouSubscriptionNotifier.SUBSCRIPTION_TOPIC,
        (YouSubscriptionNotifier) () -> gpt4CheckBox.setEnabled(true));
  }
}
