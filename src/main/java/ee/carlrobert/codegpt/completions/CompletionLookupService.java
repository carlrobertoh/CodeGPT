package ee.carlrobert.codegpt.completions;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManagerListener;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
public final class CompletionLookupService {

  private final Project project;

  private CompletionLookupService(Project project) {
    this.project = project;
  }

  public void subscribeToLookupTopic() {
    project.getMessageBus()
        .connect()
        .subscribe(LookupManagerListener.TOPIC, getLookupManagerListener());
  }

  private @Nullable String getCompletionResponse(String prompt) {
    var selectedService = SettingsState.getInstance().getSelectedService();
    switch (selectedService) {
      case OPENAI:
      case AZURE:
        return Optional.ofNullable(CompletionClientProvider.getOpenAIClient()
                .getChatCompletion(
                    CompletionRequestProvider.buildOpenAILookupGeneratorCompletionRequest(prompt))
                .getChoices())
            .map(choices -> choices.get(0).getMessage().getContent())
            .orElse(null);
      // TODO
      /*case LLAMA_CPP:
        var request = CompletionRequestProvider.buildLlamaLookupGeneratorCompletionRequest(prompt);
        return CompletionClientProvider.getLlamaClient()
            .getChatCompletion(request)
            .getContent();*/
      default:
        return null;
    }
  }

  private void addNewLookupValues(LookupImpl lookup, Application application, String prompt) {
    Optional.ofNullable(getCompletionResponse(prompt))
        .ifPresent(response -> {
          for (var value : response.split(",")) {
            application.runReadAction(() -> {
              lookup.addItem(
                  LookupElementBuilder.create(value.trim()).withIcon(Icons.SparkleIcon),
                  PrefixMatcher.ALWAYS_TRUE);
            });
            application.invokeLater(() -> lookup.refreshUi(true, true));
          }
        });
  }

  private LookupManagerListener getLookupManagerListener() {
    var application = ApplicationManager.getApplication();
    var configuration = ConfigurationState.getInstance();
    var credentialsManager = OpenAICredentialsManager.getInstance();
    return (oldLookup, newLookup) -> {
      if (!configuration.isMethodNameGenerationEnabled()
          || !credentialsManager.isApiKeySet()
          || !(newLookup instanceof LookupImpl)) {
        return;
      }

      var lookup = (LookupImpl) newLookup;
      Optional.ofNullable(lookup.getPsiElement())
          .map(PsiElement::getContext)
          .ifPresent(context ->
              application.runReadAction(() -> {
                var type = PsiUtilCore.getElementType(context);
                if ("METHOD".equals(type.toString())) {
                  var selection = context.getText();
                  application.executeOnPooledThread(
                      () -> addNewLookupValues(lookup, application, selection));
                }
              }));
    };
  }
}
