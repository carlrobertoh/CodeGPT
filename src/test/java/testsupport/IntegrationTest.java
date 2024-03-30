package testsupport;

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
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, Collections.emptyList());
    super.tearDown();
  }
}
