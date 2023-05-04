package ee.carlrobert.codegpt.state.settings.advanced;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
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
  private final JBCheckBox offScreenRenderingCheckbox;

  public AdvancedSettingsComponent(AdvancedSettingsState advancedSettings) {
    proxyTypeComboBox = new ComboBox<>(new Proxy.Type[] {
        Proxy.Type.SOCKS,
        Proxy.Type.HTTP,
        Proxy.Type.DIRECT,
    });
    proxyTypeComboBox.setSelectedItem(advancedSettings.proxyType);
    proxyHostField = new JBTextField(advancedSettings.proxyHost, 20);
    proxyPortField = new PortField();
    proxyAuthCheckbox = new JBCheckBox("Proxy authentication");
    proxyAuthUsername = new JBTextField(20);
    proxyAuthUsername.setEnabled(advancedSettings.isProxyAuthSelected);
    proxyAuthPassword = new JBPasswordField();
    proxyAuthPassword.setColumns(20);
    proxyAuthPassword.setEnabled(advancedSettings.isProxyAuthSelected);
    proxyAuthCheckbox.addItemListener(itemEvent -> {
      proxyAuthUsername.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
      proxyAuthPassword.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
    });
    offScreenRenderingCheckbox = new JBCheckBox("Use off-screen browser rendering");

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("HTTP/SOCKS Proxy"))
        .addVerticalGap(8)
        .addComponent(createProxySettingsForm())
        .addComponent(new TitledSeparator("Browser Settings"))
        .addComponent(createBrowserSettingsForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
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

  public boolean isUseOffScreenRendering() {
    return offScreenRenderingCheckbox.isSelected();
  }

  public void setUseOffScreenRendering(boolean isUseOffscreenRendering) {
    offScreenRenderingCheckbox.setSelected(isUseOffscreenRendering);
  }

  private JComponent createProxySettingsForm() {
    var proxyPanel = new JPanel();
    proxyPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.PAGE_AXIS));

    var proxyTypePanel = SwingUtils.createPanel(proxyTypeComboBox, "Proxy:", false);
    var proxyHostPanel = SwingUtils.createPanel(proxyHostField, "Host name:", false);
    var proxyPortPanel = SwingUtils.createPanel(proxyPortField, "Port:", false);
    SwingUtils.setEqualLabelWidths(proxyTypePanel, proxyHostPanel);
    SwingUtils.setEqualLabelWidths(proxyPortPanel, proxyHostPanel);

    proxyPanel.add(proxyTypePanel);
    proxyPanel.add(proxyHostPanel);
    proxyPanel.add(proxyPortPanel);
    proxyPanel.add(UI.PanelFactory
        .panel(proxyAuthCheckbox)
        .createPanel());

    var proxyUsernamePanel = SwingUtils.createPanel(proxyAuthUsername, "Username:", false);
    var proxyPasswordPanel = SwingUtils.createPanel(proxyAuthPassword, "Password:", false);
    SwingUtils.setEqualLabelWidths(proxyPasswordPanel, proxyUsernamePanel);

    var proxyAuthPanel = FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(proxyUsernamePanel)
        .addComponent(proxyPasswordPanel)
        .getPanel();
    proxyAuthPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.add(proxyAuthPanel);

    return proxyPanel;
  }

  private JComponent createBrowserSettingsForm() {
    var panel = FormBuilder.createFormBuilder()
        .addComponent(FormBuilder.createFormBuilder()
            .addComponent(offScreenRenderingCheckbox)
            .getPanel())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }
}
