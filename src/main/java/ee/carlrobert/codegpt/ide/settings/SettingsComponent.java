package ee.carlrobert.codegpt.ide.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.client.BaseModel;
import ee.carlrobert.codegpt.ide.util.SwingUtils;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import org.jetbrains.annotations.NotNull;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField apiKeyField;
  private final ComboBox<BaseModel> chatCompletionBaseModelComboBox;
  private final ComboBox<BaseModel> textCompletionBaseModelComboBox;
  private final ComboBox<String> reverseProxyComboBox;
  private final JBTextField accessTokenField;
  private final JBRadioButton useGPTRadioButton;
  private final JBRadioButton useChatCompletionRadioButton;
  private final JBRadioButton useTextCompletionRadioButton;
  private final JBRadioButton useChatGPTRadioButton;

  public SettingsComponent(SettingsState settings) {
    apiKeyField = new JBTextField(settings.apiKey, 1);
    chatCompletionBaseModelComboBox = new BaseModelComboBox(
        new BaseModel[] {
            BaseModel.CHATGPT_3_5,
            BaseModel.CHATGPT_3_5_SNAPSHOT,
        },
        settings.textCompletionBaseModel);
    textCompletionBaseModelComboBox = new BaseModelComboBox(
        new BaseModel[] {
            BaseModel.DAVINCI,
            BaseModel.CURIE,
            BaseModel.BABBAGE,
            BaseModel.ADA,
        },
        settings.textCompletionBaseModel);
    reverseProxyComboBox = new ComboBox<>(new String[] {
        "https://bypass.duti.tech/api/conversation",
    }, 400);
    reverseProxyComboBox.setSelectedItem(settings.reverseProxyUrl);
    accessTokenField = new JBTextField(settings.accessToken, 1);
    useGPTRadioButton = new JBRadioButton("Use OpenAI's official API (recommended)", settings.isGPTOptionSelected);
    useChatCompletionRadioButton = new JBRadioButton("Use chat completion", settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton("Use text completion", settings.isTextCompletionOptionSelected);
    useChatGPTRadioButton = new JBRadioButton("Use ChatGPT's unofficial API (unstable)", settings.isChatGPTOptionSelected);
    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Integration Preference"))
        .addVerticalGap(8)
        .addComponent(createMainSelectionForm())
        .addVerticalGap(8)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();

    registerButtons();
    registerFields(settings.isChatGPTOptionSelected);
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

  public boolean isChatCompletionOptionSelected() {
    return useChatCompletionRadioButton.isSelected();
  }

  public void setUseChatCompletionSelected(boolean isSelected) {
    useChatCompletionRadioButton.setSelected(isSelected);
  }

  public boolean isTextCompletionOptionSelected() {
    return useTextCompletionRadioButton.isSelected();
  }

  public void setUseTextCompletionSelected(boolean isSelected) {
    useTextCompletionRadioButton.setSelected(isSelected);
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

  public BaseModel getTextCompletionBaseModel() {
    return (BaseModel) textCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setTextCompletionBaseModel(BaseModel baseModel) {
    textCompletionBaseModelComboBox.setSelectedItem(baseModel);
  }

  public BaseModel getChatCompletionBaseModel() {
    return (BaseModel) chatCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setChatCompletionBaseModel(BaseModel baseModel) {
    chatCompletionBaseModelComboBox.setSelectedItem(baseModel);
  }

  private JPanel createMainSelectionForm() {
    var apiKeyFieldPanel = UI.PanelFactory.panel(apiKeyField)
        .withLabel("API key:")
        .withComment("You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();
    apiKeyFieldPanel.setBorder(JBUI.Borders.emptyLeft(8));

    var chatCompletionModelsPanel = SwingUtils.createPanel(chatCompletionBaseModelComboBox, "Model:", false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(24));

    var textCompletionModelsPanel = SwingUtils.createPanel(textCompletionBaseModelComboBox, "Model:", false);
    textCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(24));

    var gptRadioPanel = FormBuilder.createFormBuilder()
        .addComponent(apiKeyFieldPanel)
        .addComponent(UI.PanelFactory.panel(useChatCompletionRadioButton)
            .withComment("OpenAI’s most advanced language model")
            .createPanel())
        .addComponent(chatCompletionModelsPanel)
        .addVerticalGap(8)
        .addComponent(UI.PanelFactory.panel(useTextCompletionRadioButton)
            .withComment("Best for high-quality texts")
            .createPanel())
        .addComponent(textCompletionModelsPanel)
        .getPanel();
    gptRadioPanel.setBorder(JBUI.Borders.emptyLeft(16));


    var panel = FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(useGPTRadioButton)
            .withComment("Fast and robust, requires API key")
            .createPanel())
        .addComponent(gptRadioPanel)
        .addVerticalGap(8)
        .addComponent(UI.PanelFactory.panel(useChatGPTRadioButton)
            .withComment("Slow and free, more suitable for conversational tasks, rate-limited")
            .createPanel())
        .addComponent(createSecondSelectionForm())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  private JPanel createSecondSelectionForm() {
    var reverseProxyUrlPanel = SwingUtils.createPanel(reverseProxyComboBox, "Reverse proxy url:", false);
    var accessTokenPanel = UI.PanelFactory.panel(accessTokenField)
        .withLabel("Access token:")
        .withComment(
            "Access token can be obtained from <a href=\"https://chat.openai.com/api/auth/session\">https://chat.openai.com/api/auth/session</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();

    SwingUtils.setEqualLabelWidths(accessTokenPanel, reverseProxyUrlPanel);

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
    useGPTRadioButton.addActionListener(e -> registerFields(false));
    useChatGPTRadioButton.addActionListener(e -> registerFields(true));

    ButtonGroup completionButtonGroup = new ButtonGroup();
    completionButtonGroup.add(useChatCompletionRadioButton);
    completionButtonGroup.add(useTextCompletionRadioButton);
    useChatCompletionRadioButton.addActionListener(e -> {
      chatCompletionBaseModelComboBox.setEnabled(true);
      textCompletionBaseModelComboBox.setEnabled(false);
    });
    useTextCompletionRadioButton.addActionListener(e -> {
      chatCompletionBaseModelComboBox.setEnabled(false);
      textCompletionBaseModelComboBox.setEnabled(true);
    });
  }

  private void registerFields(boolean isUseChatGPTOption) {
    apiKeyField.setEnabled(!isUseChatGPTOption);
    if (isUseChatGPTOption) {
      List.of(
          useChatCompletionRadioButton,
          useTextCompletionRadioButton,
          chatCompletionBaseModelComboBox,
          textCompletionBaseModelComboBox
      ).forEach(it -> it.setEnabled(false));
    } else {
      useChatCompletionRadioButton.setEnabled(true);
      useTextCompletionRadioButton.setEnabled(true);
      chatCompletionBaseModelComboBox.setEnabled(useChatCompletionRadioButton.isSelected());
      textCompletionBaseModelComboBox.setEnabled(useTextCompletionRadioButton.isSelected());
    }
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
