package testsupport;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import testsupport.mixin.ShortcutsTestMixin;

public class IntegrationTest extends BasePlatformTestCase implements
    ExternalServiceTestMixin,
    ShortcutsTestMixin {

  static {
    ExternalServiceTestMixin.init();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    getProject().putUserData(CodeGPTKeys.SELECTED_FILES, Collections.emptyList());
  }

  @AfterEach
  public void cleanUpEach() {
    ExternalServiceTestMixin.clearAll();
  }
}
