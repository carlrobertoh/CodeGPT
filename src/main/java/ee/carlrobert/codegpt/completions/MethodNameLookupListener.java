package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.CodeGPTKeys.IS_PROMPT_TEXT_FIELD_DOCUMENT;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManagerListener;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MethodNameLookupListener implements LookupManagerListener {

  private static final Logger LOG = Logger.getInstance(MethodNameLookupListener.class);

  @Override
  public void activeLookupChanged(@Nullable Lookup oldLookup, @Nullable Lookup newLookup) {
    if (!(newLookup instanceof LookupImpl lookup)) {
      return;
    }

    Boolean isPromptTextFieldDocument = IS_PROMPT_TEXT_FIELD_DOCUMENT.get(
        lookup.getEditor().getDocument());
    if ((isPromptTextFieldDocument != null && isPromptTextFieldDocument)
        || !ConfigurationSettings.getState().getMethodNameGenerationEnabled()
        || !CompletionRequestService.isRequestAllowed()) {
      return;
    }

    var application = ApplicationManager.getApplication();
    Optional.ofNullable(lookup.getPsiElement())
        .map(PsiElement::getContext)
        .ifPresent(context ->
            application.runReadAction(() -> {
              var type = PsiUtilCore.getElementType(context);
              if (PSIMethodMapping.contains(type)) {
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
            application.invokeLater(() -> application.runReadAction(() -> {
              lookup.addItem(
                  LookupElementBuilder.create(value.trim()).withIcon(Icons.Sparkle),
                  PrefixMatcher.ALWAYS_TRUE);
              lookup.refreshUi(true, true);
            }));
          }
        });
  }

  private enum PSIMethodMapping {
    GO(List.of("FILE", "METHOD_DECLARATION|FUNCTION_DECLARATION")),
    JAVA(List.of("java.FILE", "METHOD")),
    PY(List.of("FILE", "Py:FUNCTION_DECLARATION")),
    JAVASCRIPT(List.of("JS:FUNCTION_DECLARATION", "JS:TYPESCRIPT_FUNCTION")),
    CS(List.of("FILE", "DUMMY_TYPE_DECLARATION", "DUMMY_BLOCK")),
    PHP(List.of("FILE", "Class method|function|Function")),
    KOTLIN(List.of("FUN")),
    DEFAULT(List.of("FILE", "METHOD_DECLARATION"));

    private final List<String> types;

    PSIMethodMapping(List<String> codes) {
      this.types = codes;
    }

    public static boolean contains(IElementType type) {
      LOG.info("Trying to find method mapping for type: " + type.toString());
      for (var value : PSIMethodMapping.values()) {
        if (value.types.contains(type.toString())) {
          return true;
        }
      }
      return false;
    }
  }
}
