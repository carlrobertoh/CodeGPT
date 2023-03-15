package ee.carlrobert.codegpt.ide.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.client.BaseModel;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
  private final ComboBox<Proxy.Type> proxyTypeComboBox;
  private final JBTextField proxyHostField;
  private final PortField proxyPortField;
  private final JBCheckBox proxyAuthCheckbox;
  private final JBTextField proxyAuthUsername;
  private final JBPasswordField proxyAuthPassword;

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
    proxyPortField = new PortField();
    reverseProxyComboBox = new ComboBox<>(new String[] {
        "https://bypass.duti.tech/api/conversation",
    }, 400);
    reverseProxyComboBox.setSelectedItem(settings.reverseProxyUrl);
    accessTokenField = new JBTextField(settings.accessToken, 1);
    proxyTypeComboBox = new ComboBox<>(new Proxy.Type[] {
        Proxy.Type.SOCKS,
        Proxy.Type.HTTP,
        Proxy.Type.DIRECT,
    });
    proxyTypeComboBox.setSelectedItem(settings.proxyType);
    proxyHostField = new JBTextField(settings.proxyHost, 20);
    proxyAuthCheckbox = new JBCheckBox("Proxy authentication");
    proxyAuthUsername = new JBTextField(20);
    proxyAuthUsername.setEnabled(settings.isProxyAuthSelected);
    proxyAuthPassword = new JBPasswordField();
    proxyAuthPassword.setColumns(20);
    proxyAuthPassword.setEnabled(settings.isProxyAuthSelected);
    proxyAuthCheckbox.addItemListener(itemEvent -> {
      proxyAuthUsername.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
      proxyAuthPassword.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
    });
    useGPTRadioButton = new JBRadioButton("Use OpenAI's official API (recommended)", settings.isGPTOptionSelected);
    useChatCompletionRadioButton = new JBRadioButton("Use chat completion", settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton("Use text completion", settings.isTextCompletionOptionSelected);
    useChatGPTRadioButton = new JBRadioButton("Use ChatGPT's unofficial API (unstable)", settings.isChatGPTOptionSelected);
    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Integration Preference"))
        .addVerticalGap(8)
        .addComponent(createMainSelectionForm())
        .addVerticalGap(8)
        .addComponent(new TitledSeparator("HTTP/SOCKS Proxy"))
        .addVerticalGap(8)
        .addComponent(createProxySettingsForm())
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

  public String getProxyHost() {
    return proxyHostField.getText().trim();
  }

  public void setProxyHost(String host) {
    proxyHostField.setText(host.trim());
  }

  public int getProxyPort() {
    return proxyPortField.getNumber();
  }

  public void setProxyPort(int port) {
    proxyPortField.setNumber(port);
  }

  public Proxy.Type getProxyType() {
    return (Proxy.Type) proxyTypeComboBox.getSelectedItem();
  }

  public void setProxyType(Proxy.Type type) {
    proxyTypeComboBox.setSelectedItem(type);
  }

  public boolean isProxyAuthSelected() {
    return proxyAuthCheckbox.isSelected();
  }

  public void setUseProxyAuthentication(boolean isProxyAuthSelected) {
    proxyAuthCheckbox.setSelected(isProxyAuthSelected);
  }

  public String getProxyAuthUsername() {
    return proxyAuthUsername.getText().trim();
  }

  public void setProxyUsername(String proxyUsername) {
    proxyAuthUsername.setText(proxyUsername);
  }

  public String getProxyAuthPassword() {
    return new String(proxyAuthPassword.getPassword());
  }

  public void setProxyPassword(String proxyPassword) {
    proxyAuthPassword.setText(proxyPassword);
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

    var chatCompletionModelsPanel = createPanel(chatCompletionBaseModelComboBox, "Model:", false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(24));

    var textCompletionModelsPanel = createPanel(textCompletionBaseModelComboBox, "Model:", false);
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
    var reverseProxyUrlPanel = createPanel(reverseProxyComboBox, "Reverse proxy url:", false);
    var accessTokenPanel = UI.PanelFactory.panel(accessTokenField)
        .withLabel("Access token:")
        .withComment(
            "Access token can be obtained from <a href=\"https://chat.openai.com/api/auth/session\">https://chat.openai.com/api/auth/session</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();

    setEqualLabelWidths(accessTokenPanel, reverseProxyUrlPanel);

    var panel = FormBuilder.createFormBuilder()
        .addComponent(reverseProxyUrlPanel)
        .addVerticalGap(8)
        .addComponent(accessTokenPanel)
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(24));
    return panel;
  }

  private JComponent createProxySettingsForm() {
    var proxyPanel = new JPanel();
    proxyPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.PAGE_AXIS));

    var proxyTypePanel = createPanel(proxyTypeComboBox, "Proxy:", false);
    var proxyHostPanel = createPanel(proxyHostField, "Host name:", false);
    var proxyPortPanel = createPanel(proxyPortField, "Port:", false);
    setEqualLabelWidths(proxyTypePanel, proxyHostPanel);
    setEqualLabelWidths(proxyPortPanel, proxyHostPanel);

    proxyPanel.add(proxyTypePanel);
    proxyPanel.add(proxyHostPanel);
    proxyPanel.add(proxyPortPanel);
    proxyPanel.add(UI.PanelFactory
        .panel(proxyAuthCheckbox)
        .createPanel());

    var proxyUsernamePanel = createPanel(proxyAuthUsername, "Username:", false);
    var proxyPasswordPanel = createPanel(proxyAuthPassword, "Password:", false);
    setEqualLabelWidths(proxyPasswordPanel, proxyUsernamePanel);

    var proxyAuthPanel = FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(proxyUsernamePanel)
        .addComponent(proxyPasswordPanel)
        .getPanel();
    proxyAuthPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.add(proxyAuthPanel);

    return proxyPanel;
  }

  // TODO: Find better way of doing this
  private void setEqualLabelWidths(JPanel firstPanel, JPanel secondPanel) {
    var firstLabel = firstPanel.getComponents()[0];
    var secondLabel = secondPanel.getComponents()[0];
    if (firstLabel instanceof JLabel && secondLabel instanceof JLabel) {
      firstLabel.setPreferredSize(secondLabel.getPreferredSize());
    }
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


  private JPanel createPanel(JComponent component, String label) {
    return createPanel(component, label, true);
  }

  private JPanel createPanel(JComponent component, String label, boolean resizeX) {
    return UI.PanelFactory.panel(component)
        .withLabel(label)
        .resizeX(resizeX)
        .createPanel();
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
