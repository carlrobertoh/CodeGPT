package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManagerListener;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MethodNameLookupListener implements LookupManagerListener {

  @Override
  public void activeLookupChanged(@Nullable Lookup oldLookup, @Nullable Lookup newLookup) {
    var application = ApplicationManager.getApplication();
    var configuration = ConfigurationState.getInstance();
    var credentialsManager = OpenAICredentialsManager.getInstance();

    if (!configuration.isMethodRefactoringEnabled()
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
                application.executeOnPooledThread(() ->
                    addCompletionLookupValues(lookup, application, selection));
              }
            }));
  }

  private void addCompletionLookupValues(
      LookupImpl lookup,
      Application application,
      String prompt) {
    Optional.ofNullable(getCompletionResponse(prompt))
        .ifPresent(response -> {
          for (var value : response.split(",")) {
            application.runReadAction(() -> {
              lookup.addItem(
                  LookupElementBuilder.create(value.trim()).withIcon(Icons.Sparkle),
                  PrefixMatcher.ALWAYS_TRUE);
              application.invokeLater(() -> lookup.refreshUi(true, true));
            });
          }
        });
  }

  private @Nullable String getCompletionResponse(String prompt) {
    var selectedService = SettingsState.getInstance().getSelectedService();
    if (selectedService == OPENAI || selectedService == AZURE) {
      return Optional.ofNullable(CompletionClientProvider.getOpenAIClient()
              .getChatCompletion(
                  CompletionRequestProvider.buildOpenAILookupCompletionRequest(prompt))
              .getChoices())
          .map(choices -> choices.get(0).getMessage().getContent())
          .orElse(null);
    }
    return null;
  }
}
