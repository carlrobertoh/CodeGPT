package ee.carlrobert.codegpt.toolwindow.chat.components;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import ee.carlrobert.codegpt.completions.you.YouSubscriptionNotifier;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.ModelIconLabel;
import ee.carlrobert.codegpt.toolwindow.chat.YouModelChangeNotifier;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class UserPromptTextAreaHeader extends JPanel {

  public UserPromptTextAreaHeader(
      Project project,
      SettingsState settings,
      TotalTokensPanel totalTokensPanel) {
    super(new BorderLayout());
    setBackground(getPanelBackgroundColor());
    setBorder(JBUI.Borders.emptyBottom(8));
    switch (settings.getSelectedService()) {
      case OPENAI:
      case AZURE:
        add(totalTokensPanel, BorderLayout.LINE_START);
        break;
      case YOU:
        JBCheckBox gpt4CheckBox = new YouProCheckbox(project);
        subscribeToYouTopics(project, gpt4CheckBox);
        add(gpt4CheckBox, BorderLayout.LINE_START);
        break;
      default:
    }
    add(JBUI.Panels
        .simplePanel(new ModelIconLabel(
            settings.getSelectedService().getCompletionCode(),
            settings.getModel()))
        .withBorder(Borders.emptyRight(4))
        .withBackground(getPanelBackgroundColor()), BorderLayout.LINE_END);
  }

  private void subscribeToYouTopics(Project project, JBCheckBox gpt4CheckBox) {
    var messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
    subscribeToYouModelChangeTopic(project, gpt4CheckBox);
    subscribeToYouSubscriptionTopic(messageBusConnection, gpt4CheckBox);
    subscribeToSignedOutTopic(messageBusConnection, gpt4CheckBox);
  }

  private void subscribeToYouModelChangeTopic(Project project, JBCheckBox gpt4CheckBox) {
    project.getMessageBus()
        .connect()
        .subscribe(
            YouModelChangeNotifier.YOU_MODEL_CHANGE_NOTIFIER_TOPIC,
            (YouModelChangeNotifier) gpt4CheckBox::setSelected);
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
