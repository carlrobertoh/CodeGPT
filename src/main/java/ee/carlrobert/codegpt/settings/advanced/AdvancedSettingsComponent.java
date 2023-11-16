package ee.carlrobert.codegpt.settings.advanced;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.event.ItemEvent;
import java.net.Proxy;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class AdvancedSettingsComponent {

  private final JPanel mainPanel;
  private final ComboBox<Proxy.Type> proxyTypeComboBox;
  private final JBTextField proxyHostField;
  private final PortField proxyPortField;
  private final JBCheckBox proxyAuthCheckbox;
  private final JBTextField proxyAuthUsername;
  private final JBPasswordField proxyAuthPassword;
  private final PortField connectionTimeoutField;
  private final PortField readTimeoutField;

  public AdvancedSettingsComponent(AdvancedSettingsState advancedSettings) {
    proxyTypeComboBox = new ComboBox<>(new Proxy.Type[]{
        Proxy.Type.SOCKS,
        Proxy.Type.HTTP,
        Proxy.Type.DIRECT,
    });
    proxyTypeComboBox.setSelectedItem(advancedSettings.getProxyType());
    proxyHostField = new JBTextField(advancedSettings.getProxyHost(), 20);
    proxyPortField = new PortField();
    proxyAuthCheckbox = new JBCheckBox(CodeGPTBundle.get(
        "advancedSettingsConfigurable.proxy.authCheckBoxField.label"));
    proxyAuthUsername = new JBTextField(20);
    proxyAuthUsername.setEnabled(advancedSettings.isProxyAuthSelected());
    proxyAuthPassword = new JBPasswordField();
    proxyAuthPassword.setColumns(20);
    proxyAuthPassword.setEnabled(advancedSettings.isProxyAuthSelected());
    proxyAuthCheckbox.addItemListener(itemEvent -> {
      proxyAuthUsername.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
      proxyAuthPassword.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
    });
    connectionTimeoutField = new PortField(advancedSettings.getConnectTimeout());
    readTimeoutField = new PortField(advancedSettings.getReadTimeout());

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(CodeGPTBundle.get(
            "advancedSettingsConfigurable.proxy.title")))
        .addComponent(createProxySettingsForm())
        .addVerticalGap(4)
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("advancedSettingsConfigurable.connectionSettings.title")))
        .addComponent(createConnectionSettingsForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JPanel createConnectionSettingsForm() {
    var panel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get(
                "advancedSettingsConfigurable.connectionSettings.connectionTimeout.label"),
            connectionTimeoutField)
        .addLabeledComponent(
            CodeGPTBundle.get("advancedSettingsConfigurable.connectionSettings.readTimeout.label"),
            readTimeoutField)
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public Proxy.Type getProxyType() {
    return (Proxy.Type) proxyTypeComboBox.getSelectedItem();
  }

  public void setProxyType(Proxy.Type type) {
    proxyTypeComboBox.setSelectedItem(type);
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

  public int getConnectionTimeout() {
    return connectionTimeoutField.getNumber();
  }

  public void setConnectionTimeoutField(int timeout) {
    connectionTimeoutField.setNumber(timeout);
  }

  public int getReadTimeout() {
    return readTimeoutField.getNumber();
  }

  public void setReadTimeout(int timeout) {
    readTimeoutField.setNumber(timeout);
  }

  private JComponent createProxySettingsForm() {
    var proxyPanel = new JPanel();
    proxyPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.PAGE_AXIS));

    var proxyTypePanel = SwingUtils.createPanel(
        proxyTypeComboBox,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.typeComboBoxField.label"),
        false);
    var proxyHostPanel = SwingUtils.createPanel(
        proxyHostField,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.hostField.label"),
        false);
    var proxyPortPanel = SwingUtils.createPanel(
        proxyPortField,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.portField.label"),
        false);
    SwingUtils.setEqualLabelWidths(proxyTypePanel, proxyHostPanel);
    SwingUtils.setEqualLabelWidths(proxyPortPanel, proxyHostPanel);

    proxyPanel.add(proxyTypePanel);
    proxyPanel.add(proxyHostPanel);
    proxyPanel.add(proxyPortPanel);
    proxyPanel.add(UI.PanelFactory
        .panel(proxyAuthCheckbox)
        .createPanel());

    var proxyUsernamePanel = SwingUtils.createPanel(proxyAuthUsername,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.usernameField.label"),
        false);
    var proxyPasswordPanel = SwingUtils.createPanel(proxyAuthPassword,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.passwordField.label"),
        false);
    SwingUtils.setEqualLabelWidths(proxyPasswordPanel, proxyUsernamePanel);

    var proxyAuthPanel = FormBuilder.createFormBuilder()
        .addVerticalGap(4)
        .addComponent(proxyUsernamePanel)
        .addComponent(proxyPasswordPanel)
        .getPanel();
    proxyAuthPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.add(proxyAuthPanel);

    return proxyPanel;
  }
}
