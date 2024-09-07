package ee.carlrobert.codegpt.settings.service.watsonx;

import static com.intellij.ide.BrowserUtil.open;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.WATSONX_API_KEY;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.llm.client.watsonx.completion.WatsonxCompletionModel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jetbrains.annotations.Nullable;

public class WatsonxSettingsForm {

    private final JBRadioButton onPremRadio;
    private final JBRadioButton onCloudRadio;
    private final JBPasswordField apiKeyField;
    private final JBPasswordField onPremApiKeyField;
    private final ComboBox regionComboBox;
    private final JBTextField onPremHostField;
    private final JBTextField usernameField;
    private final JBCheckBox zenApiKeyCheckbox;

    private final JPanel onPremAuthenticationFieldPanel;
    private final JPanel onCloudAuthenticationFieldPanel;

    private final String getStartedText = "Get started with IBM watsonx.ai as a Service";
    private final String getStartedUrl = "https://dataplatform.cloud.ibm.com/registration/stepone?context=wx";
    private final JButton getStartedLink;
    private final JBTextField apiVersionField;
    private final JBTextField projectIdField;
    private final JBTextField spaceIdField;

    private final ComboBox modelComboBox;
    private final JBRadioButton greedyDecodingRadio;
    private final JBRadioButton sampleDecodingRadio;
    private final JBTextField temperatureField;
    private final JBTextField topKField;
    private final JBTextField topPField;
    private final JBTextField repetitionPenaltyField;
    private final JBTextField randomSeedField;
    private final JBTextField maxNewTokensField;
    private final JBTextField minNewTokensField;
    private final JBTextField stopSequencesField;
    private final JBCheckBox includeStopSequenceCheckbox;
    private final JPanel sampleParametersFieldPanel;

    public WatsonxSettingsForm(WatsonxSettingsState settings) {
        onPremRadio = new JBRadioButton("Watsonx.ai on-premises software", false);
        onCloudRadio = new JBRadioButton("Watsonx.ai as a Service", true);

        onPremHostField = new JBTextField(settings.getOnPremHost(), 35);

        usernameField = new JBTextField(settings.getUsername(), 35);

        zenApiKeyCheckbox = new JBCheckBox("Is Platform (Zen) API key", false);


        class OpenUrlAction implements ActionListener {
            @Override public void actionPerformed(ActionEvent e) {
                open(getStartedUrl);
            }
        }
        getStartedLink = new JButton();
        getStartedLink.setText("<HTML><FONT color=\"#0f62fe\">"+getStartedText+"</FONT>");
        getStartedLink.setHorizontalAlignment(SwingConstants.LEFT);
        getStartedLink.setBorderPainted(false);
        getStartedLink.setContentAreaFilled(false);
        getStartedLink.setFocusPainted(false);
        getStartedLink.setOpaque(false);
        getStartedLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        getStartedLink.setToolTipText(getStartedUrl);
        getStartedLink.addActionListener(new OpenUrlAction());

        regionComboBox = new ComboBox(new String[] {"Dallas", "Frankfurt", "London", "Tokyo"});
        regionComboBox.setSelectedItem(settings.getRegion());

        apiKeyField = new JBPasswordField();
        apiKeyField.setColumns(35);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            var apiKey = CredentialsStore.getCredential(WATSONX_API_KEY);
            SwingUtilities.invokeLater(() -> apiKeyField.setText(apiKey));
        });

        onPremApiKeyField = new JBPasswordField();
        onPremApiKeyField.setColumns(35);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            var apiKey = CredentialsStore.getCredential(WATSONX_API_KEY);
            SwingUtilities.invokeLater(() -> onPremApiKeyField.setText(apiKey));
        });

        onPremAuthenticationFieldPanel = new UI.PanelFactory().grid()
                .add(UI.PanelFactory.panel(onPremHostField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.onPremHost.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.onPremHost.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(usernameField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.username.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.username.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(onPremApiKeyField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.onPremApiKey.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.onPremApiKey.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(zenApiKeyCheckbox).resizeX(false))
                .createPanel();

        onCloudAuthenticationFieldPanel = new UI.PanelFactory().grid()
                .add(UI.PanelFactory.panel(getStartedLink))
                .add(UI.PanelFactory.panel(regionComboBox)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.cloudRegion.label"))
                        .withComment(CodeGPTBundle.get("settingsConfigurable.service.watsonx.cloudRegion.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(apiKeyField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.onCloudApiKey.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.onCloudApiKey.comment"))
                        .resizeX(false))
                .createPanel();

        apiVersionField = new JBTextField(settings.getApiVersion(), 35);
        projectIdField = new JBTextField(settings.getProjectId(), 35);
        spaceIdField = new JBTextField(settings.getSpaceId(), 35);
        greedyDecodingRadio = new JBRadioButton("Greedy decoding", false);
        sampleDecodingRadio = new JBRadioButton("Sample decoding", true);

        modelComboBox = new ComboBox<>(new EnumComboBoxModel(WatsonxCompletionModel.class));
        modelComboBox.setSelectedItem(WatsonxCompletionModel.findByCode(settings.getModel()));
        temperatureField=new JBTextField(String.valueOf(settings.getTemperature()),35);
        topKField=new JBTextField(String.valueOf(settings.getTopK()),35);
        topPField=new JBTextField(String.valueOf(settings.getTopP()),35);
        repetitionPenaltyField=new JBTextField(String.valueOf(settings.getRepetitionPenalty()),35);
        randomSeedField=new JBTextField(settings.getRandomSeed() == null ? "" : String.valueOf(settings.getRandomSeed()),35);
        maxNewTokensField=new JBTextField(String.valueOf(settings.getMaxNewTokens()),35);
        minNewTokensField=new JBTextField(String.valueOf(settings.getMinNewTokens()),35);
        stopSequencesField=new JBTextField(String.valueOf(settings.getStopSequences()),35);
        includeStopSequenceCheckbox=new JBCheckBox(String.valueOf(settings.getIncludeStopSequence()));
        includeStopSequenceCheckbox.setText("Include stop sequence");
        includeStopSequenceCheckbox.setSelected(false);

        sampleParametersFieldPanel = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(temperatureField)
                        .withLabel(CodeGPTBundle.get("configurationConfigurable.section.assistant.temperatureField.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.temperature.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(topKField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.llama.topK.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.topK.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(topPField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.llama.topP.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.llama.topP.comment"))
                        .resizeX(false))
                .add(UI.PanelFactory.panel(randomSeedField)
                        .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.randomSeed.label"))
                        .withComment(CodeGPTBundle.get(
                                "settingsConfigurable.service.watsonx.randomSeed.comment"))
                        .resizeX(false))
                .createPanel();

        registerPanelsVisibility(settings);

        onPremRadio.addActionListener(e -> {
            settings.setOnPrem(true);
            onCloudRadio.setSelected(false);
            onPremAuthenticationFieldPanel.setVisible(true);
            onCloudAuthenticationFieldPanel.setVisible(false);

        });

        onCloudRadio.addActionListener(e -> {
            settings.setOnPrem(false);
            onPremRadio.setSelected(false);
            onPremAuthenticationFieldPanel.setVisible(false);
            onCloudAuthenticationFieldPanel.setVisible(true);

        });

        greedyDecodingRadio.addActionListener(e -> {
            settings.setGreedyDecoding(true);
            sampleDecodingRadio.setSelected(false);
            sampleParametersFieldPanel.setVisible(false);
        });

        sampleDecodingRadio.addActionListener(e -> {
            settings.setGreedyDecoding(false);
            greedyDecodingRadio.setSelected(false);
            sampleParametersFieldPanel.setVisible(true);
        });
    }

    private void registerPanelsVisibility(WatsonxSettingsState settings) {
        onPremAuthenticationFieldPanel.setVisible(settings.isOnPrem());
        onCloudAuthenticationFieldPanel.setVisible(!settings.isOnPrem());
        sampleParametersFieldPanel.setVisible(!settings.isGreedyDecoding());
    }

    public JPanel getForm() {
        return FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator("Connection Parameters"))
                .addComponent(UI.PanelFactory.grid()
                        .add(UI.PanelFactory.panel(onCloudRadio)
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(onPremRadio)
                                .resizeX(false)).createPanel())
                .addComponent(onCloudAuthenticationFieldPanel)
                .addComponent(onPremAuthenticationFieldPanel)
                .addComponent(UI.PanelFactory.grid()
                        .add(UI.PanelFactory.panel(apiVersionField)
                                .withLabel(CodeGPTBundle.get("shared.apiVersion"))
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.apiVersion.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(projectIdField)
                                .withLabel("Project ID:")
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.projectId.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(spaceIdField)
                                .withLabel("Space ID:")
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.spaceId.comment"))
                                .resizeX(false))
                                .createPanel())
                .addComponent(new TitledSeparator("Generation Parameters"))
                .addComponent(UI.PanelFactory.grid()
                        .add(UI.PanelFactory.panel(modelComboBox)
                                .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.model.label"))
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.modelId.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(maxNewTokensField)
                                .withLabel(CodeGPTBundle.get("configurationConfigurable.section.assistant.maxTokensField.label"))
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.maxNewTokens.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(minNewTokensField)
                                .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.minNewTokens.label"))
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.minNewTokens.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(stopSequencesField)
                                .withLabel(CodeGPTBundle.get("settingsConfigurable.service.watsonx.stopSequences.label"))
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.stopSequences.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(includeStopSequenceCheckbox)
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(repetitionPenaltyField)
                                .withLabel("Repetition Penalty:")
                                .withComment(CodeGPTBundle.get(
                                        "settingsConfigurable.service.watsonx.repetitionPenalty.comment"))
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(greedyDecodingRadio)
                                .resizeX(false))
                        .add(UI.PanelFactory.panel(sampleDecodingRadio)
                                .resizeX(false))
                        .createPanel())
                .addComponent(sampleParametersFieldPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public WatsonxSettingsState getCurrentState() {
        var state = new WatsonxSettingsState();
        state.setModel(((WatsonxCompletionModel) modelComboBox.getSelectedItem()).getCode());
        state.setOnPrem(onPremRadio.isSelected());
        state.setOnPremHost(onPremHostField.getText());
        state.setUsername(usernameField.getText());
        state.setZenApiKey(zenApiKeyCheckbox.isSelected());
        state.setRegion((String)regionComboBox.getSelectedItem());
        state.setApiVersion(apiVersionField.getText());
        state.setSpaceId(spaceIdField.getText());
        state.setProjectId(projectIdField.getText());
        state.setGreedyDecoding(greedyDecodingRadio.isSelected());
        state.setMaxNewTokens(Integer.valueOf(maxNewTokensField.getText()));
        state.setMinNewTokens(Integer.valueOf(minNewTokensField.getText()));
        state.setTemperature(Double.valueOf(temperatureField.getText()));
        state.setRandomSeed(randomSeedField.getText().isEmpty() ? null : Integer.valueOf(randomSeedField.getText()));
        state.setTopP(Double.valueOf(topPField.getText()));
        state.setTopK(Integer.valueOf(topKField.getText()));
        state.setIncludeStopSequence(includeStopSequenceCheckbox.isSelected());
        state.setStopSequences(stopSequencesField.getText());
        state.setRepetitionPenalty(Double.valueOf(repetitionPenaltyField.getText()));
        return state;
    }

    public void resetForm() {
        var state = WatsonxSettings.getCurrentState();
        onPremRadio.setSelected(state.isOnPrem());
        onCloudRadio.setSelected(!state.isOnPrem());
        onPremHostField.setText(state.getOnPremHost());
        usernameField.setText(state.getUsername());
        zenApiKeyCheckbox.setSelected(state.isZenApiKey());
        apiKeyField.setText(CredentialsStore.getCredential(WATSONX_API_KEY));
        apiVersionField.setText(state.getApiVersion());
        spaceIdField.setText(state.getSpaceId());
        projectIdField.setText(state.getProjectId());
        greedyDecodingRadio.setSelected(state.isGreedyDecoding());
        sampleDecodingRadio.setSelected(!state.isGreedyDecoding());
        modelComboBox.setSelectedItem(WatsonxCompletionModel.findByCode(state.getModel()));
        maxNewTokensField.setText(String.valueOf(state.getMaxNewTokens()));
        minNewTokensField.setText(String.valueOf(state.getMinNewTokens()));
        temperatureField.setText(String.valueOf(state.getTemperature()));
        randomSeedField.setText(state.getRandomSeed() == null ? "" : String.valueOf(state.getRandomSeed()));
        topPField.setText(String.valueOf(state.getTopP()));
        topKField.setText(String.valueOf(state.getTopK()));
        repetitionPenaltyField.setText(String.valueOf(state.getRepetitionPenalty()));
        includeStopSequenceCheckbox.setSelected(state.getIncludeStopSequence());
        stopSequencesField.setText(String.valueOf(state.getStopSequences()));
    }

    public @Nullable String getApiKey() {
        var apiKey = new String(apiKeyField.getPassword());
        return apiKey.isEmpty() ? null : apiKey;
    }
}
