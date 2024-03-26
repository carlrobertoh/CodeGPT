package testsupport;

import static ee.carlrobert.codegpt.codecompletions.CodeCompletionService.APPLY_INLAY_ACTION_ACCEPT_ALL_ID;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.codecompletions.CodeGPTEditorManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin;
import java.util.Collections;
import testsupport.mixin.ShortcutsTestMixin;

public class IntegrationTest extends BasePlatformTestCase implements
    ExternalServiceTestMixin,
    ShortcutsTestMixin {

  static {
    ExternalServiceTestMixin.init();
  }

  @Override
  protected void tearDown() throws Exception {
    ExternalServiceTestMixin.clearAll();
    ConfigurationSettings.getCurrentState().setCodeCompletionsEnabled(false);
    CodeGPTEditorManager.getInstance().disposeAllInlays(getProject());
    ActionManager.getInstance().unregisterAction(APPLY_INLAY_ACTION_ACCEPT_ALL_ID);
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, Collections.emptyList());
    super.tearDown();
  }
}
