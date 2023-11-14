package testsupport;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.llm.client.mixin.ExternalServiceTestMixin;
import org.junit.jupiter.api.AfterEach;
import testsupport.mixin.ShortcutsTestMixin;

public class IntegrationTest extends BasePlatformTestCase implements
    ExternalServiceTestMixin,
    ShortcutsTestMixin {

  static {
    ExternalServiceTestMixin.init();
  }

  @AfterEach
  public void cleanUpEach() {
    ExternalServiceTestMixin.clearAll();
  }
}
