package ee.carlrobert.codegpt.state.settings.components;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.util.SwingUtils;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServiceSelectionForm {

  private final SettingsState settings;
  private final JBTextField openAIOrganizationField;
  private final JBTextField azureResourceNameField;
  private final JBTextField azureDeploymentIdField;
  private final JBTextField azureApiVersionField;
  private final JBTextField customHostField;
  private final JBCheckBox useActiveDirectoryAuthenticationCheckBox;
  private final JBRadioButton useCustomServiceRadioButton;
  private final JBRadioButton useAzureServiceRadioButton;
  private final JBRadioButton useOpenAIServiceRadioButton;
  private final JPanel openAIServiceSectionPanel;
  private final JPanel azureServiceSectionPanel;
  private final JPanel customServiceSectionPanel;
  private final ModelSelectionForm modelSelectionForm;

  public ServiceSelectionForm(SettingsState settings, ModelSelectionForm modelSelectionForm) {
    this.settings = settings;
    this.modelSelectionForm = modelSelectionForm;
    useOpenAIServiceRadioButton = new JBRadioButton(
        "Use OpenAI service API", settings.useOpenAIService);
    useAzureServiceRadioButton = new JBRadioButton(
        "Use Azure OpenAI service API", settings.useAzureService);
    useCustomServiceRadioButton = new JBRadioButton(
        "Use custom service", settings.useCustomService);

    useActiveDirectoryAuthenticationCheckBox = new JBCheckBox(
        "Use Azure Active Directory authentication", settings.useActiveDirectoryAuthentication);

    openAIOrganizationField = new JBTextField(settings.organization, 40);
    azureResourceNameField = new JBTextField(settings.resourceName, 40);
    azureDeploymentIdField = new JBTextField(settings.deploymentId, 40);
    azureApiVersionField = new JBTextField(settings.apiVersion, 40);
    customHostField = new JBTextField(settings.customHost, 40);

    openAIServiceSectionPanel = createOpenAIServiceSectionPanel();
    azureServiceSectionPanel = createAzureServiceSectionPanel();
    customServiceSectionPanel = createCustomServiceSectionPanel();

    registerSectionPanels();
    registerRadioButtons();
  }

  public JPanel getForm() {
    var panel = FormBuilder.createFormBuilder()
        .addComponent(useOpenAIServiceRadioButton)
        .addComponent(openAIServiceSectionPanel)
        .addVerticalGap(8)
        .addComponent(useAzureServiceRadioButton)
        .addComponent(azureServiceSectionPanel)
        .addVerticalGap(8)
        .addComponent(useCustomServiceRadioButton)
        .addComponent(customServiceSectionPanel)
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  public void setUseOpenAIServiceSelected(boolean selected) {
    useOpenAIServiceRadioButton.setSelected(selected);
  }

  public boolean isUseOpenAIService() {
    return useOpenAIServiceRadioButton.isSelected();
  }

  public void setUseAzureServiceSelected(boolean selected) {
    useAzureServiceRadioButton.setSelected(selected);
  }

  public boolean isUseActiveDirectoryAuthentication() {
    return useActiveDirectoryAuthenticationCheckBox.isSelected();
  }

  public void setUseActiveDirectoryAuthenticationSelected(boolean selected) {
    useActiveDirectoryAuthenticationCheckBox.setSelected(selected);
  }

  public boolean isUseAzureService() {
    return useAzureServiceRadioButton.isSelected();
  }

  public void setUseCustomServiceSelected(boolean selected) {
    useCustomServiceRadioButton.setSelected(selected);
  }

  public boolean isUseCustomService() {
    return useCustomServiceRadioButton.isSelected();
  }

  public String getCustomHost() {
    return customHostField.getText();
  }

  public void setCustomHost(String customHost) {
    customHostField.setText(customHost);
  }

  public String getResourceName() {
    return azureResourceNameField.getText();
  }

  public void setResourceName(String resourceName) {
    azureResourceNameField.setText(resourceName);
  }

  public String getDeploymentId() {
    return azureDeploymentIdField.getText();
  }

  public void setDeploymentId(String deploymentId) {
    azureDeploymentIdField.setText(deploymentId);
  }

  public String getApiVersion() {
    return azureApiVersionField.getText();
  }

  public void setApiVersionField(String apiVersion) {
    azureApiVersionField.setText(apiVersion);
  }

  public String getOrganization() {
    return openAIOrganizationField.getText();
  }

  public void setOrganization(String organization) {
    openAIOrganizationField.setText(organization);
  }

  private JPanel createPanel(JComponent component, String label, String comment) {
    return UI.PanelFactory.panel(component)
        .withLabel(label)
        .resizeX(false)
        .withComment(comment)
        .createPanel();
  }

  private JPanel createOpenAIServiceSectionPanel() {
    var organizationFieldPanel = UI.PanelFactory.panel(openAIOrganizationField)
        .withLabel("Organization:")
        .resizeX(false)
        .withComment(
            "Useful when you are part of multiple organizations <sup><strong>optional</strong></sup>")
        .createPanel();
    organizationFieldPanel.setBorder(JBUI.Borders.empty(8, 16, 0, 0));
    return organizationFieldPanel;
  }

  private JPanel createAzureServiceSectionPanel() {
    var resourceNameFieldPanel = createPanel(
        azureResourceNameField, "Resource name:", "Azure OpenAI Service resource name");
    var deploymentIdFieldPanel = createPanel(
        azureDeploymentIdField, "Deployment ID:", "Azure OpenAI Service deployment ID");
    var apiVersionFieldPanel = createPanel(
        azureApiVersionField, "API version:", "API version to be used for Azure OpenAI Service");
    var authFieldPanel = UI.PanelFactory.panel(useActiveDirectoryAuthenticationCheckBox)
        .resizeX(false)
        .createPanel();

    var azureRelatedFieldsPanel = FormBuilder.createFormBuilder()
        .addVerticalGap(8)
        .addComponent(resourceNameFieldPanel)
        .addComponent(deploymentIdFieldPanel)
        .addComponent(apiVersionFieldPanel)
        .addComponent(authFieldPanel)
        .getPanel();
    azureRelatedFieldsPanel.setBorder(Borders.emptyLeft(16));

    SwingUtils.setEqualLabelWidths(deploymentIdFieldPanel, resourceNameFieldPanel);
    SwingUtils.setEqualLabelWidths(apiVersionFieldPanel, resourceNameFieldPanel);

    return azureRelatedFieldsPanel;
  }

  private JPanel createCustomServiceSectionPanel() {
    var customHostFieldPanel = UI.PanelFactory.panel(customHostField)
        .withLabel("Custom host:")
        .withComment("Example: <strong>http://localhost:8080</strong>")
        .resizeX(false)
        .createPanel();
    customHostFieldPanel.setBorder(JBUI.Borders.empty(8, 16, 0, 0));
    return customHostFieldPanel;
  }

  private void registerSectionPanels() {
    openAIServiceSectionPanel.setVisible(settings.useOpenAIService);
    azureServiceSectionPanel.setVisible(settings.useAzureService);
    customServiceSectionPanel.setVisible(settings.useCustomService);
    modelSelectionForm.changeModelPanelsVisibility(settings.useCustomService);
  }

  private void registerRadioButtons() {
    var serviceButtonGroup = new ButtonGroup();
    serviceButtonGroup.add(useOpenAIServiceRadioButton);
    serviceButtonGroup.add(useAzureServiceRadioButton);
    serviceButtonGroup.add(useCustomServiceRadioButton);

    useOpenAIServiceRadioButton.addActionListener(e -> {
      openAIServiceSectionPanel.setVisible(true);
      azureServiceSectionPanel.setVisible(false);
      customServiceSectionPanel.setVisible(false);
      modelSelectionForm.changeModelPanelsVisibility(false);
    });
    useAzureServiceRadioButton.addActionListener(e -> {
      openAIServiceSectionPanel.setVisible(false);
      azureServiceSectionPanel.setVisible(true);
      customServiceSectionPanel.setVisible(false);
      modelSelectionForm.changeModelPanelsVisibility(false);
    });
    useCustomServiceRadioButton.addActionListener(e -> {
      openAIServiceSectionPanel.setVisible(false);
      azureServiceSectionPanel.setVisible(false);
      customServiceSectionPanel.setVisible(true);
      modelSelectionForm.changeModelPanelsVisibility(true);
    });
  }
}
