package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
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
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.you.completion.YouCompletionCustomModel;
import ee.carlrobert.llm.client.you.completion.YouCompletionMode;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class PersonaModelComboBoxAction extends ComboBoxAction {

  private final OpenAISettingsState openAISettings;
  private final YouSettingsState youSettings;
  private Persona persona;
  private ServiceType unsavedServiceType;

  public PersonaModelComboBoxAction(Persona persona) {
    openAISettings = OpenAISettings.getCurrentState();
    youSettings = YouSettings.getCurrentState();
    updateTemplatePresentation(persona.getServiceType());
    this.persona = persona;
    unsavedServiceType = persona.getServiceType();

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

  @Override
  protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
    var presentation = ((ComboBoxButton) button).getPresentation();
    var actionGroup = new DefaultActionGroup();
    actionGroup.addSeparator("OpenAI");
    List.of(
        OpenAIChatCompletionModel.GPT_4_0125_128k,
        OpenAIChatCompletionModel.GPT_3_5_0125_16k,
        OpenAIChatCompletionModel.GPT_4_32k,
        OpenAIChatCompletionModel.GPT_4,
        OpenAIChatCompletionModel.GPT_3_5)
        .forEach(model -> actionGroup.add(createOpenAIModelAction(model, presentation)));
    actionGroup.addSeparator("Custom OpenAI Service");
    actionGroup.add(createModelAction(
        CUSTOM_OPENAI,
        CustomServiceSettings.getCurrentState().getTemplate().getName(),
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

    if (YouUserManager.getInstance().isSubscribed()) {
      actionGroup.addSeparator("You.com");
      List.of(
          YouCompletionMode.DEFAULT,
          YouCompletionMode.AGENT,
          YouCompletionMode.RESEARCH)
          .forEach(mode -> actionGroup.add(createYouModeAction(mode, presentation)));
      List.of(
          YouCompletionCustomModel.values())
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
      MessageBusConnection messageBusConnection) {
    messageBusConnection.subscribe(
        SignedOutNotifier.SIGNED_OUT_TOPIC,
        (SignedOutNotifier) () -> {
          var youSettings = YouSettings.getCurrentState();
          if (!YouUserManager.getInstance().isSubscribed()
              && youSettings.getChatMode() != YouCompletionMode.DEFAULT) {
            youSettings.setChatMode(YouCompletionMode.DEFAULT);
            updateTemplatePresentation(this.persona.getServiceType());
          }
        });
  }

  private void updateTemplatePresentation(ServiceType selectedService) {
    var templatePresentation = getTemplatePresentation();
    switch (selectedService) {
      case OPENAI:
        templatePresentation.setIcon(Icons.OpenAI);
        templatePresentation.setText(
            OpenAIChatCompletionModel.findByCode(openAISettings.getModel()).getDescription());
        break;
      case CUSTOM_OPENAI:
        templatePresentation.setIcon(Icons.OpenAI);
        templatePresentation.setText(CustomServiceSettings.getCurrentState()
            .getTemplate()
            .getName());
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
        templatePresentation.setIcon(Icons.YouSmall);
        templatePresentation.setText(
            youSettings.getChatMode() == YouCompletionMode.CUSTOM
                ? youSettings.getCustomModel().getDescription()
                : youSettings.getChatMode().getDescription());
        break;
      case LLAMA_CPP:
        templatePresentation.setText(getLlamaCppPresentationText());
        templatePresentation.setIcon(Icons.Llama);
        break;
      default:
    }
  }

  public void setPersona(Persona persona) {
    this.persona = persona;
    unsavedServiceType = persona.getServiceType();
    if (persona.getServiceType() == ServiceType.OPENAI) {
      openAISettings.setModel(persona.getModel());
    }
    updateTemplatePresentation(unsavedServiceType);
  }

  public ServiceType getSelectedService() {
    return unsavedServiceType;
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
    return new DumbAwareAction(label, "", icon) {
      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        handleProviderChange(serviceType, label, icon, comboBoxPresentation);
      }

      @Override
      public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
      }
    };
  }

  private void handleProviderChange(
      ServiceType serviceType,
      String label,
      Icon icon,
      Presentation comboBoxPresentation) {
    comboBoxPresentation.setIcon(icon);
    comboBoxPresentation.setText(label);
    // persona.setServiceType(serviceType);
    unsavedServiceType = serviceType;
    System.out.println("Service type changed to: " + serviceType);

    // NOTE: I don't want to update the conversation state here. I want to update
    // the persona state.
    // var currentConversation = ConversationsState.getCurrentConversation();
    // if (currentConversation != null &&
    // !currentConversation.getMessages().isEmpty()) {
    // onAddNewTab.run();
    // } else {
    // ConversationService.getInstance().startConversation();
    // }
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
        openAISettings.setModel(model.getCode());
        handleProviderChange(
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
        youSettings.setChatMode(mode);
        handleProviderChange(
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
        youSettings.setCustomModel(model);
        youSettings.setChatMode(YouCompletionMode.CUSTOM);
        handleProviderChange(
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
