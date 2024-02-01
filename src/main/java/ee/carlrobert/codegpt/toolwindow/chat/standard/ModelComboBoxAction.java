package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ModelComboBoxAction extends ComboBoxAction {

  private final Runnable onAddNewTab;
  private final SettingsState settings;
  private final OpenAISettingsState openAISettings;

  public ModelComboBoxAction(Runnable onAddNewTab, ServiceType selectedService) {
    this.onAddNewTab = onAddNewTab;
    settings = SettingsState.getInstance();
    openAISettings = OpenAISettingsState.getInstance();
    updateTemplatePresentation(selectedService);
  }

  public JComponent createCustomComponent(@NotNull String place) {
    return createCustomComponent(getTemplatePresentation(), place);
  }

  @NotNull
  @Override
  public JComponent createCustomComponent(
      @NotNull Presentation presentation,
      @NotNull String place) {
    ComboBoxButton button = createComboBoxButton(presentation);
    button.setBorder(null);
    return button;
  }

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();
    actionGroup.addSeparator("OpenAI");
    List.of(
            OpenAIChatCompletionModel.GPT_4_1106_128k,
            OpenAIChatCompletionModel.GPT_3_5_1106_16k,
            OpenAIChatCompletionModel.GPT_4_32k,
            OpenAIChatCompletionModel.GPT_4,
            OpenAIChatCompletionModel.GPT_3_5)
        .forEach(
            model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
    actionGroup.addSeparator();
    actionGroup.add(
        createModelAction(ServiceType.AZURE, "Azure OpenAI", Icons.Azure, presentation));
    actionGroup.addSeparator();
    actionGroup.add(createModelAction(
        ServiceType.LLAMA,
        getSelectedHuggingFace(),
        Icons.Llama,
        presentation));
    // TODO add Ollama
    actionGroup.addSeparator();
    actionGroup.add(createModelAction(ServiceType.YOU, "You.com", Icons.YouSmall, presentation));
    return actionGroup;
  }

  @Override
  protected boolean shouldShowDisabledActions() {
    return true;
  }

  private void updateTemplatePresentation(ServiceType selectedService) {
    var templatePresentation = getTemplatePresentation();
    switch (selectedService) {
      case OPENAI:
        templatePresentation.setIcon(Icons.OpenAI);
        templatePresentation.setText(
            OpenAIChatCompletionModel.findByCode(openAISettings.getModel()).getDescription());
        break;
      case AZURE:
        templatePresentation.setIcon(Icons.Azure);
        templatePresentation.setText("Azure OpenAI");
        break;
      case YOU:
        templatePresentation.setIcon(Icons.YouSmall);
        templatePresentation.setText("You.com");
        break;
      case LLAMA:
        templatePresentation.setText(getSelectedHuggingFace());
        templatePresentation.setIcon(Icons.Llama);
        break;
      default:
    }
  }

  private String getSelectedHuggingFace() {
    var huggingFaceModel = LlamaSettingsState.getInstance().getLocalModel();
    return format(
        "%s %dB (Q%d)",
        LlamaModel.findByHuggingFaceModel(huggingFaceModel).getLabel(),
        huggingFaceModel.getParameterSize(),
        huggingFaceModel.getQuantization());
  }

  private AnAction createModelAction(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    return new AnAction(label, "", icon) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        handleProviderChange(serviceType, label, icon, comboBoxPresentation);
      }
    };
  }

  private void handleProviderChange(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    settings.setSelectedService(serviceType);
    comboBoxPresentation.setIcon(icon);
    comboBoxPresentation.setText(label);

    var currentConversation = ConversationsState.getCurrentConversation();
    if (currentConversation != null && !currentConversation.getMessages().isEmpty()) {
      onAddNewTab.run();
    } else {
      ConversationService.getInstance().startConversation();
    }
  }

  private AnAction createOpenAIModelAction(
      OpenAIChatCompletionModel model,
      Presentation comboBoxPresentation) {
    createModelAction(ServiceType.OPENAI, model.getDescription(), Icons.OpenAI,
        comboBoxPresentation);
    return new AnAction(model.getDescription(), "", Icons.OpenAI) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        openAISettings.setModel(model.getCode());
        handleProviderChange(
            ServiceType.OPENAI,
            model.getDescription(),
            Icons.OpenAI,
            comboBoxPresentation);
      }
    };
  }
}