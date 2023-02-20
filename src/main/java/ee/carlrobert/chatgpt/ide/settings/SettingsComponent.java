package ee.carlrobert.chatgpt.ide.settings;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import org.jetbrains.annotations.NotNull;

public class SettingsComponent {

  private final JPanel myMainPanel;
  private final JBTextField apiKeyField = new JBTextField();

  public SettingsComponent() {
    myMainPanel = FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(apiKeyField)
            .withLabel("API key:")
            .withComment("You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
            .withCommentHyperlinkListener(event -> {
              if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                  try {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                  } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException("Couldn't open the browser.", e);
                  }
                }
              }
            })
            .createPanel())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return apiKeyField;
  }

  @NotNull
  public String getApiKeyField() {
    return apiKeyField.getText();
  }

  public void setApiKeyField(@NotNull String apiKey) {
    apiKeyField.setText(apiKey);
  }
}
