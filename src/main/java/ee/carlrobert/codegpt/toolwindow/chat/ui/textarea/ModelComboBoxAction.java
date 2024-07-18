package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CODEGPT;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.GOOGLE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OLLAMA;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static java.lang.String.format;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTAvailableModels;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTModel;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ModelComboBoxAction extends ComboBoxAction {

  private final Consumer<ServiceType> onModelChange;
  private final Project project;
  private final List<ServiceType> availableProviders;

  public ModelComboBoxAction(
      Project project,
      Consumer<ServiceType> onModelChange,
      ServiceType selectedService) {
    this(project, onModelChange, selectedService, Arrays.asList(ServiceType.values()));
  }

  public ModelComboBoxAction(
      Project project,
      Consumer<ServiceType> onModelChange,
      ServiceType selectedProvider,
      List<ServiceType> availableProviders) {
    this.project = project;
    this.onModelChange = onModelChange;
    this.availableProviders = availableProviders;
    updateTemplatePresentation(selectedProvider);
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

  private AnAction[] getCodeGPTModelActions(Project project, Presentation presentation) {
    var userDetails = CodeGPTKeys.CODEGPT_USER_DETAILS.get(project);
    return CodeGPTAvailableModels.getToolWindowModels(
            userDetails == null ? null : userDetails.getPricingPlan()).stream()
        .map(model -> createCodeGPTModelAction(model, presentation))
        .toArray(AnAction[]::new);
  }

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();

    if (availableProviders.contains(CODEGPT)) {
      actionGroup.addSeparator("CodeGPT");
      actionGroup.addAll(getCodeGPTModelActions(project, presentation));
    }
    if (availableProviders.contains(OPENAI)) {
      actionGroup.addSeparator("OpenAI");
      List.of(
              OpenAIChatCompletionModel.GPT_4_O,
              OpenAIChatCompletionModel.GPT_4_O_MINI,
              OpenAIChatCompletionModel.GPT_4_VISION_PREVIEW,
              OpenAIChatCompletionModel.GPT_4_0125_128k)
          .forEach(model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
    }
    if (availableProviders.contains(CUSTOM_OPENAI)) {
      actionGroup.addSeparator("Custom OpenAI");
      actionGroup.add(createModelAction(
          CUSTOM_OPENAI,
          ApplicationManager.getApplication().getService(CustomServiceSettings.class)
              .getState()
              .getTemplate()
              .getProviderName(),
          Icons.OpenAI,
          presentation));
    }
    if (availableProviders.contains(ANTHROPIC)) {
      actionGroup.addSeparator("Anthropic");
      actionGroup.add(createModelAction(
          ANTHROPIC,
          "Anthropic (Claude)",
          Icons.Anthropic,
          presentation));
    }
    if (availableProviders.contains(AZURE)) {
      actionGroup.addSeparator("Azure");
      actionGroup.add(
          createModelAction(AZURE, "Azure OpenAI", Icons.Azure, presentation));
    }
    if (availableProviders.contains(GOOGLE)) {
      actionGroup.addSeparator("Google");
      actionGroup.add(createModelAction(
          GOOGLE,
          "Google (Gemini)",
          Icons.Google,
          presentation));
    }
    if (availableProviders.contains(LLAMA_CPP)) {
      actionGroup.addSeparator("LLaMA C/C++");
      actionGroup.add(createModelAction(
          LLAMA_CPP,
          getLlamaCppPresentationText(),
          Icons.Llama,
          presentation));
    }
    if (availableProviders.contains(OLLAMA)) {
      actionGroup.addSeparator("Ollama");
      createOllamaModelAction("Default model", presentation);
      ApplicationManager.getApplication()
          .getService(OllamaSettings.class)
          .getState()
          .getAvailableModels()
          .forEach(model ->
              actionGroup.add(createOllamaModelAction(model, presentation)));
    }

    return actionGroup;
  }

  @Override
  protected boolean shouldShowDisabledActions() {
    return true;
  }

  private void updateTemplatePresentation(ServiceType selectedService) {
    var application = ApplicationManager.getApplication();
    var templatePresentation = getTemplatePresentation();
    switch (selectedService) {
      case CODEGPT:
        var modelCode = application.getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .getModel();
        var model = CodeGPTAvailableModels.getALL_CHAT_MODELS().stream()
            .filter(it -> it.getCode().equals(modelCode))
            .findFirst();
        templatePresentation.setIcon(model.map(CodeGPTModel::getIcon).orElse(Icons.CodeGPTModel));
        templatePresentation.setText(model.map(CodeGPTModel::getName).orElse("Unknown"));
        break;
      case OPENAI:
        templatePresentation.setIcon(Icons.OpenAI);
        templatePresentation.setText(
            OpenAIChatCompletionModel.findByCode(OpenAISettings.getCurrentState().getModel())
                .getDescription());
        break;
      case CUSTOM_OPENAI:
        templatePresentation.setIcon(Icons.OpenAI);
        templatePresentation.setText(application.getService(CustomServiceSettings.class)
            .getState()
            .getTemplate()
            .getProviderName());
        break;
      case ANTHROPIC:
        templatePresentation.setIcon(Icons.Anthropic);
        templatePresentation.setText("Anthropic (Claude)");
        break;
      case AZURE:
        templatePresentation.setIcon(Icons.Azure);
        templatePresentation.setText("Azure OpenAI");
        break;
      case LLAMA_CPP:
        templatePresentation.setText(getLlamaCppPresentationText());
        templatePresentation.setIcon(Icons.Llama);
        break;
      case OLLAMA:
        templatePresentation.setIcon(Icons.Ollama);
        templatePresentation.setText(application.getService(OllamaSettings.class)
            .getState()
            .getModel());
        break;
      case GOOGLE:
        templatePresentation.setText("Google (Gemini)");
        templatePresentation.setIcon(Icons.Google);
        break;
      default:
        break;
    }
  }

  private String getLlamaCppPresentationText() {
    var llamaSettingState = LlamaSettings.getCurrentState();
    if (!llamaSettingState.isRunLocalServer()) {
      return format("Remote %s", llamaSettingState.getRemoteModelPromptTemplate());
    }
    return getSelectedHuggingFace();
  }

  private String getSelectedHuggingFace() {
    var huggingFaceModel = LlamaSettings.getCurrentState().getHuggingFaceModel();
    var llamaModel = LlamaModel.findByHuggingFaceModel(huggingFaceModel);
    return format(
        "%s %dB (Q%d)",
        llamaModel.getLabel(),
        huggingFaceModel.getParameterSize(),
        huggingFaceModel.getQuantization());
  }

  private AnAction createModelAction(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    return new DumbAwareAction(label, "", icon) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        handleModelChange(serviceType, label, label, icon, comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private void handleModelChange(
      ServiceType serviceType,
      String label,
      String code,
      Icon icon,
      Presentation comboBoxPresentation) {
    GeneralSettings.getCurrentState().setSelectedService(serviceType);
    comboBoxPresentation.setIcon(icon);
    comboBoxPresentation.setText(label);
    onModelChange.accept(serviceType);
  }

  private AnAction createCodeGPTModelAction(CodeGPTModel model, Presentation comboBoxPresentation) {
    return new DumbAwareAction(model.getName(), "", model.getIcon()) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .setModel(model.getCode());
        handleModelChange(
            CODEGPT,
            model.getName(),
            model.getCode(),
            model.getIcon(),
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private AnAction createOllamaModelAction(
      String model,
      Presentation comboBoxPresentation
  ) {
    return new DumbAwareAction(model, "", Icons.Ollama) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication()
            .getService(OllamaSettings.class)
            .getState()
            .setModel(model);
        handleModelChange(
            OLLAMA,
            model,
            model,
            Icons.Ollama,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private AnAction createOpenAIModelAction(
      OpenAIChatCompletionModel model,
      Presentation comboBoxPresentation) {
    createModelAction(OPENAI, model.getDescription(), Icons.OpenAI,
        comboBoxPresentation);
    return new DumbAwareAction(model.getDescription(), "", Icons.OpenAI) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettings.getCurrentState().setModel(model.getCode());
        handleModelChange(
            OPENAI,
            model.getDescription(),
            model.getCode(),
            Icons.OpenAI,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }
}
