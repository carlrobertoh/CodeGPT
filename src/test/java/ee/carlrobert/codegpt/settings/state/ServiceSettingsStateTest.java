package ee.carlrobert.codegpt.settings.state;

import static ee.carlrobert.codegpt.CodeGPTPlugin.CODEGPT_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginSetBuilder;
import com.intellij.ide.plugins.RawPluginDescriptor;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.you.YouSettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.nio.file.Path;
import java.util.List;
import org.junit.Test;

public class ServiceSettingsStateTest {

  @Test
  public void testLlamaSettingsStateIsModified() {
    mockCodeGptPluginPath();
    LlamaSettingsState settings = new LlamaSettingsState();
    LlamaSettingsState modifiedSettings = new LlamaSettingsState();
    // Port is always initialized randomly, therefore reset to be the same
    modifiedSettings.getLocalSettings().setServerPort(settings.getLocalSettings().getServerPort());
    assertThat(settings.isModified(modifiedSettings)).isFalse();

    modifiedSettings.getRequestSettings().setRepeatPenalty(100);

    assertThat(settings.isModified(modifiedSettings)).isTrue();
  }

  @Test
  public void testOpenAISettingsStateIsModified() {
    OpenAISettingsState settings = new OpenAISettingsState();
    OpenAISettingsState modifiedSettings = new OpenAISettingsState();
    assertThat(settings.isModified(modifiedSettings)).isFalse();

    modifiedSettings.setModel(OpenAIChatCompletionModel.GPT_4);

    assertThat(settings.isModified(modifiedSettings)).isTrue();
  }

  @Test
  public void testAzureSettingsStateIsModified() {
    AzureSettingsState settings = new AzureSettingsState();
    AzureSettingsState modifiedSettings = new AzureSettingsState();
    assertThat(settings.isModified(modifiedSettings)).isFalse();

    modifiedSettings.setApiVersion("foo");

    assertThat(settings.isModified(modifiedSettings)).isTrue();
  }

  private static void mockCodeGptPluginPath() {
    RawPluginDescriptor raw = new RawPluginDescriptor();
    raw.id = CODEGPT_ID.getIdString();
    IdeaPluginDescriptorImpl pluginDescriptor = new IdeaPluginDescriptorImpl(raw, Path.of(""),
        false, CODEGPT_ID, null, false);
    pluginDescriptor.setEnabled(true);
    PluginManagerCore.setPluginSet(
        new PluginSetBuilder(List.of(pluginDescriptor)).createPluginSetWithEnabledModulesMap());
  }

}