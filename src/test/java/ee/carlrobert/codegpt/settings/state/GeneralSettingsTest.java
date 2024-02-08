package ee.carlrobert.codegpt.settings.state;

import static ee.carlrobert.codegpt.completions.HuggingFaceModel.CODE_LLAMA_7B_Q3;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.form.model.OllamaChildModel;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

public class GeneralSettingsTest extends BasePlatformTestCase {

  public void testOpenAISettingsSync() {
    var openAISettings = OpenAISettings.getCurrentState();
    openAISettings.setModel("gpt-3.5-turbo");
    var conversation = new Conversation();
    conversation.setModel("gpt-4");
    conversation.setClientCode("chat.completion");
    var settings = GeneralSettings.getInstance();

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.OPENAI);
    assertThat(openAISettings.getModel()).isEqualTo("gpt-4");
  }

  public void testAzureSettingsSync() {
    var settings = GeneralSettings.getInstance();
    var conversation = new Conversation();
    conversation.setModel("gpt-4");
    conversation.setClientCode("azure.chat.completion");

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.AZURE);
  }

  public void testYouSettingsSync() {
    var settings = GeneralSettings.getInstance();
    var conversation = new Conversation();
    conversation.setModel("YouCode");
    conversation.setClientCode("you.chat.completion");

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.YOU);
  }

  public void testLlamaSettingsModelPathSync() {
    var llamaSettings = LlamaSettings.getCurrentState();
    llamaSettings.setHuggingFaceModel(HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3);
    var conversation = new Conversation();
    conversation.setModel("TEST_LLAMA_MODEL_PATH");
    conversation.setClientCode("llama.chat.completion");
    var settings = GeneralSettings.getInstance();

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.LLAMA_CPP);
    assertThat(llamaSettings.getCustomLlamaModelPath()).isEqualTo("TEST_LLAMA_MODEL_PATH");
    assertThat(llamaSettings.isUseCustomModel()).isTrue();
  }

  public void testLlamaSettingsHuggingFaceModelSync() {
    var llamaSettings = LlamaSettings.getCurrentState();
    llamaSettings.setHuggingFaceModel(HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3);
    var conversation = new Conversation();
    conversation.setModel("CODE_LLAMA_7B_Q3");
    conversation.setClientCode("llama.chat.completion");
    var settings = GeneralSettings.getInstance();

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.LLAMA_CPP);
    assertThat(llamaSettings.getHuggingFaceModel()).isEqualTo(CODE_LLAMA_7B_Q3);
    assertThat(llamaSettings.isUseCustomModel()).isFalse();
  }

  public void testOllamaSettingsHuggingFaceModelSync() {
    var ollamaSettings = OllamaSettings.getCurrentState();
    ollamaSettings.setOllamaModel(OllamaChildModel.codellama());
    var conversation = new Conversation();
    conversation.setModel("codellama:7b-instruct-q4_K_M");
    conversation.setClientCode("ollama.chat.completion");
    var settings = GeneralSettings.getInstance();

    settings.sync(conversation);

    assertThat(settings.getState().getSelectedService()).isEqualTo(ServiceType.OLLAMA);
    assertThat(ollamaSettings.getOllamaModel()).isEqualTo(OllamaChildModel.codellama());
  }
}
