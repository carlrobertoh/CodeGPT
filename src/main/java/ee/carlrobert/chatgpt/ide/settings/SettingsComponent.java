package ee.carlrobert.chatgpt.ide.settings;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import org.jetbrains.annotations.NotNull;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField apiKeyField;
  private final JComboBox<String> reverseProxyComboBox;
  private final JBTextField accessTokenField;
  private final JBRadioButton useGPTRadioButton;
  private final JBRadioButton useChatGPTRadioButton;

  public SettingsComponent(SettingsState settings) {
    apiKeyField = new JBTextField(settings.apiKey);
    reverseProxyComboBox = new JComboBox<>(new String[] {
        "https://chat.duti.tech/api/conversation",
        "https://gpt.pawan.krd/backend-api/conversation"
    });
    accessTokenField = new JBTextField(settings.accessToken, 1);
    useGPTRadioButton = new JBRadioButton("Use OpenAI's official GPT3 API", settings.isGPTOptionSelected);
    useChatGPTRadioButton = new JBRadioButton("Use ChatGPT's unofficial backend API", settings.isChatGPTOptionSelected);
    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Integration Preference"))
        .addComponent(createMainSelectionForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();

    if (settings.isGPTOptionSelected) {
      reverseProxyComboBox.setEnabled(false);
      accessTokenField.setEnabled(false);
    } else {
      apiKeyField.setEnabled(false);
      reverseProxyComboBox.setEnabled(true);
      accessTokenField.setEnabled(true);
    }

    registerButtons();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return apiKeyField;
  }

  @NotNull
  public String getApiKey() {
    return apiKeyField.getText();
  }

  public void setApiKey(@NotNull String apiKey) {
    apiKeyField.setText(apiKey);
  }

  @NotNull
  public String getAccessToken() {
    return accessTokenField.getText();
  }

  public void setAccessToken(@NotNull String accessToken) {
    accessTokenField.setText(accessToken);
  }

  public boolean isGPTOptionSelected() {
    return useGPTRadioButton.isSelected();
  }

  public void setUseGPTOptionSelected(boolean isSelected) {
    useGPTRadioButton.setSelected(isSelected);
  }

  public boolean isChatGPTOptionSelected() {
    return useChatGPTRadioButton.isSelected();
  }

  public void setUseChatGPTOptionSelected(boolean isSelected) {
    useChatGPTRadioButton.setSelected(isSelected);
  }

  public String getReverseProxyUrl() {
    return (String) reverseProxyComboBox.getSelectedItem();
  }

  public void setReverseProxyUrl(String reverseProxyUrl) {
    reverseProxyComboBox.setSelectedItem(reverseProxyUrl);
  }

  private JPanel createMainSelectionForm() {
    var panel = FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(UI.PanelFactory.panel(useGPTRadioButton)
            .withComment("Fast and robust, requires API key")
            .createPanel())
        .addComponent(createFirstSelectionForm())
        .addVerticalGap(8)
        .addComponent(UI.PanelFactory.panel(useChatGPTRadioButton)
            .withComment("Slow and free, more suitable for conversational tasks")
            .createPanel())
        .addComponent(createSecondSelectionForm())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  private JPanel createFirstSelectionForm() {
    var panel = FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(apiKeyField)
            .withLabel("API key:")
            .withComment("You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
            .withCommentHyperlinkListener(this::handleHyperlinkClicked)
            .createPanel())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(24));
    return panel;
  }

  private JPanel createSecondSelectionForm() {
    var reverseProxyUrlPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent("Reverse proxy url:", reverseProxyComboBox)
        .getPanel();

    var accessTokenPanel = UI.PanelFactory.panel(accessTokenField)
        .withLabel("Access token:")
        .withComment(
            "Access token can be obtained from <a href=\"https://chat.openai.com/api/auth/session\">https://chat.openai.com/api/auth/session</a> and is valid for ~8h.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();

    var accessTokenLabel = accessTokenPanel.getComponents()[0];
    var reverseProxyUrlLabel = reverseProxyUrlPanel.getComponents()[0];
    if (accessTokenLabel instanceof JLabel && reverseProxyUrlLabel instanceof JLabel) {
      accessTokenLabel.setPreferredSize(reverseProxyUrlLabel.getPreferredSize());
    }

    var panel = FormBuilder.createFormBuilder()
        .addComponent(reverseProxyUrlPanel)
        .addVerticalGap(8)
        .addComponent(accessTokenPanel)
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(24));
    return panel;
  }

  private void registerButtons() {
    ButtonGroup myButtonGroup = new ButtonGroup();
    myButtonGroup.add(useGPTRadioButton);
    myButtonGroup.add(useChatGPTRadioButton);
    useGPTRadioButton.addActionListener(e -> handleRadioOptionChange(false));
    useChatGPTRadioButton.addActionListener(e -> handleRadioOptionChange(true));
  }

  private void handleRadioOptionChange(boolean isUseChatGPTOption) {
    apiKeyField.setEnabled(!isUseChatGPTOption);
    accessTokenField.setEnabled(isUseChatGPTOption);
    reverseProxyComboBox.setEnabled(isUseChatGPTOption);
  }

  private void handleHyperlinkClicked(HyperlinkEvent event) {
    if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
      if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        try {
          Desktop.getDesktop().browse(event.getURL().toURI());
        } catch (IOException | URISyntaxException e) {
          throw new RuntimeException("Couldn't open the browser.", e);
        }
      }
    }
  }
}
