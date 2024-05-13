package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import static ee.carlrobert.codegpt.settings.service.ServiceType.CODEGPT;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OLLAMA;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;
import static java.lang.String.format;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.util.messages.MessageBusConnection;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.SignedOutNotifier;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTAvailableModels;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTModel;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.you.completion.YouCompletionCustomModel;
import ee.carlrobert.llm.client.you.completion.YouCompletionMode;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ModelComboBoxAction extends ComboBoxAction {

  private final Runnable onModelChange;

  public ModelComboBoxAction(Runnable onModelChange, ServiceType selectedService) {
    this.onModelChange = onModelChange;
    updateTemplatePresentation(selectedService);

    subscribeToYouSignedOutTopic(ApplicationManager.getApplication().getMessageBus().connect());
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

  private AnAction[] getCodeGPTModelActions(Presentation presentation) {
    var apiKey = CredentialsStore.getCredential(CredentialKey.CODEGPT_API_KEY);
    return CodeGPTAvailableModels.getCHAT_MODELS().stream()
        .map(model -> {
          var enabled = "meta-llama/Llama-3-8b-chat-hf".equals(model.getCode())
              || (apiKey != null && !apiKey.isEmpty());
          return createCodeGPTModelAction(model, enabled, presentation);
        })
        .toArray(AnAction[]::new);
  }

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();
    actionGroup.addSeparator("CodeGPT");
    actionGroup.addAll(getCodeGPTModelActions(presentation));
    actionGroup.addSeparator("OpenAI");
    List.of(
            OpenAIChatCompletionModel.GPT_4_O,
            OpenAIChatCompletionModel.GPT_4_VISION_PREVIEW,
            OpenAIChatCompletionModel.GPT_4_0125_128k,
            OpenAIChatCompletionModel.GPT_3_5_0125_16k)
        .forEach(model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
    actionGroup.addSeparator("Custom OpenAI");
    actionGroup.add(createModelAction(
        CUSTOM_OPENAI,
        ApplicationManager.getApplication().getService(CustomServiceSettings.class)
            .getState()
            .getTemplate()
            .getProviderName(),
        Icons.OpenAI,
        presentation));
    actionGroup.addSeparator();
    actionGroup.add(createModelAction(
        ServiceType.ANTHROPIC,
        "Anthropic (Claude)",
        Icons.Anthropic,
        presentation));
    actionGroup.addSeparator();
    actionGroup.add(
        createModelAction(ServiceType.AZURE, "Azure OpenAI", Icons.Azure, presentation));
    actionGroup.addSeparator();
    actionGroup.add(createModelAction(
        ServiceType.LLAMA_CPP,
        getLlamaCppPresentationText(),
        Icons.Llama,
        presentation));
    actionGroup.addSeparator("Ollama");
    ApplicationManager.getApplication()
        .getService(OllamaSettings.class)
        .getState()
        .getAvailableModels()
        .forEach(model ->
            actionGroup.add(createOllamaModelAction(model, presentation)));
    actionGroup.addSeparator();
    actionGroup.add(createModelAction(
        ServiceType.GOOGLE,
        "Google (Gemini)",
        Icons.Google,
        presentation));

    if (YouUserManager.getInstance().isSubscribed()) {
      actionGroup.addSeparator("You.com");
      List.of(
              YouCompletionMode.DEFAULT,
              YouCompletionMode.AGENT,
              YouCompletionMode.RESEARCH)
          .forEach(mode -> actionGroup.add(createYouModeAction(mode, presentation)));
      List.of(
              YouCompletionCustomModel.values()
          )
          .forEach(model -> actionGroup.add(createYouModelAction(model, presentation)));
    } else {
      actionGroup.addSeparator();
      actionGroup.add(createYouModeAction(YouCompletionMode.DEFAULT, presentation));
    }
    return actionGroup;
  }

  @Override
  protected boolean shouldShowDisabledActions() {
    return true;
  }

  private void subscribeToYouSignedOutTopic(
      MessageBusConnection messageBusConnection
  ) {
    messageBusConnection.subscribe(
        SignedOutNotifier.SIGNED_OUT_TOPIC,
        (SignedOutNotifier) () -> {
          var youSettings = YouSettings.getCurrentState();
          if (!YouUserManager.getInstance().isSubscribed()
              && youSettings.getChatMode() != YouCompletionMode.DEFAULT) {
            youSettings.setChatMode(YouCompletionMode.DEFAULT);
            updateTemplatePresentation(GeneralSettings.getSelectedService());
          }
        }
    );
  }

  private void updateTemplatePresentation(ServiceType selectedService) {
    var application = ApplicationManager.getApplication();
    var templatePresentation = getTemplatePresentation();
    switch (selectedService) {
      case CODEGPT:
        var model = application.getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .getModel();
        var modelName = CodeGPTAvailableModels.getCHAT_MODELS().stream()
            .filter(it -> it.getCode().equals(model))
            .map(CodeGPTModel::getName)
            .findFirst().orElse("Unknown");
        templatePresentation.setIcon(Icons.CodeGPTModel);
        templatePresentation.setText(modelName);
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
      case YOU:
        var settings = YouSettings.getCurrentState();
        templatePresentation.setIcon(Icons.YouSmall);
        templatePresentation.setText(
            settings.getChatMode() == YouCompletionMode.CUSTOM
                ? settings.getCustomModel().getDescription()
                : settings.getChatMode().getDescription()
        );
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
        handleModelChange(serviceType, label, icon, comboBoxPresentation);
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
      Icon icon,
      Presentation comboBoxPresentation) {
    GeneralSettings.getCurrentState().setSelectedService(serviceType);
    comboBoxPresentation.setIcon(icon);
    comboBoxPresentation.setText(label);
    onModelChange.run();
  }

  private AnAction createCodeGPTModelAction(CodeGPTModel model, boolean enabled,
      Presentation comboBoxPresentation) {
    return new DumbAwareAction(model.getName(), "", Icons.CodeGPTModel) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(
            enabled && !presentation.getText().equals(comboBoxPresentation.getText()));
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
            Icons.OpenAI,
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
            Icons.OpenAI,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private AnAction createYouModeAction(
      YouCompletionMode mode,
      Presentation comboBoxPresentation) {
    createModelAction(YOU, mode.getDescription(), Icons.YouSmall,
        comboBoxPresentation);
    return new DumbAwareAction(mode.getDescription(), "", Icons.YouSmall) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        YouSettings.getCurrentState().setChatMode(mode);
        handleModelChange(
            YOU,
            mode.getDescription(),
            Icons.YouSmall,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private AnAction createYouModelAction(
      YouCompletionCustomModel model,
      Presentation comboBoxPresentation) {
    createModelAction(YOU, model.getDescription(), Icons.YouSmall,
        comboBoxPresentation);
    return new DumbAwareAction(model.getDescription(), "", Icons.YouSmall) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        var settings = YouSettings.getCurrentState();
        settings.setCustomModel(model);
        settings.setChatMode(YouCompletionMode.CUSTOM);
        handleModelChange(
            YOU,
            model.getDescription(),
            Icons.YouSmall,
            comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }
}
