package testsupport;

import com.intellij.openapi.util.Key;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.CodeGPTKeys;
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
    clearKeys();
    super.tearDown();
  }

  private void clearKeys() {
    putUserData(CodeGPTKeys.SELECTED_FILES, Collections.emptyList());
    putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, "");
    putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, "");
  }

  private <T> void putUserData(Key<T> key, T value) {
    getProject().putUserData(key, value);
  }
}
