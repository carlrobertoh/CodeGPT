package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.settings.service.ServiceType;

import java.awt.*;
import javax.swing.*;

public class UserPromptTextAreaHeader extends JPanel {

  public UserPromptTextAreaHeader(
          ServiceType selectedService,
          Persona selectedPersona,
          TotalTokensPanel totalTokensPanel,
          Runnable onAddNewTab) {
    super(new FlowLayout(FlowLayout.CENTER, 0, 0));
    setOpaque(false);
    setBorder(JBUI.Borders.emptyBottom(8));

    switch (selectedService) {
      case OPENAI:
      case AZURE:
        add(Box.createHorizontalStrut(8));
        add(totalTokensPanel);
        break;
      case YOU:
        break;
      default:
    }

    add(Box.createHorizontalStrut(8));
    add(new ModelComboBoxAction(onAddNewTab, selectedService)
            .createCustomComponent(ActionPlaces.UNKNOWN));

    add(Box.createHorizontalStrut(8));

    PersonaComboBoxAction personaComboBoxAction = new PersonaComboBoxAction(selectedPersona);
    JComponent personaComboBox = personaComboBoxAction.createCustomComponent(ActionPlaces.UNKNOWN);
    add(personaComboBox);
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
