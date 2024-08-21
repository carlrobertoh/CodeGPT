package ee.carlrobert.codegpt.completions;

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
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MethodNameLookupListener implements LookupManagerListener {

  @Override
  public void activeLookupChanged(@Nullable Lookup oldLookup, @Nullable Lookup newLookup) {
    if (!ConfigurationSettings.getState().getMethodNameGenerationEnabled()
        || !CompletionRequestService.isRequestAllowed()
        || !(newLookup instanceof LookupImpl lookup)) {
      return;
    }

    var application = ApplicationManager.getApplication();
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
    CompletionRequestService.getInstance().getLookupCompletion(prompt)
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
}
