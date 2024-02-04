package ee.carlrobert.codegpt.settings.state;

import static ee.carlrobert.codegpt.completions.llama.HuggingFaceModel.CODE_LLAMA_7B_Q3;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.completions.llama.CustomLlamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

public class SettingsStateTest extends BasePlatformTestCase {

  public void testOpenAISettingsSync() {
    var openAISettings = OpenAISettingsState.getInstance();
    openAISettings.setModel(OpenAIChatCompletionModel.GPT_3_5);
    var conversation = new Conversation();
    conversation.setModel("gpt-4");
    conversation.setClientCode("chat.completion");
    var settings = GeneralSettingsState.getInstance();

    settings.sync(conversation);

    assertThat(settings.getSelectedService()).isEqualTo(ServiceType.OPENAI);
    assertThat(openAISettings.getModel()).isEqualTo(OpenAIChatCompletionModel.GPT_4);
  }

  public void testAzureSettingsSync() {
    var settings = GeneralSettingsState.getInstance();
    var conversation = new Conversation();
    conversation.setModel("gpt-4");
    conversation.setClientCode("azure.chat.completion");

    settings.sync(conversation);

    assertThat(settings.getSelectedService()).isEqualTo(ServiceType.AZURE);
    assertThat(AzureSettingsState.getInstance().getModel()).isEqualTo(
        OpenAIChatCompletionModel.GPT_4);
  }

  public void testYouSettingsSync() {
    var settings = GeneralSettingsState.getInstance();
    var conversation = new Conversation();
    conversation.setModel("YouCode");
    conversation.setClientCode("you.chat.completion");

    settings.sync(conversation);

    assertThat(settings.getSelectedService()).isEqualTo(ServiceType.YOU);
  }

  public void testLlamaSettingsModelPathSync() {
    var llamaSettings = LlamaCppSettingsState.getInstance().getLocalSettings();
    llamaSettings.setModel(HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3);
    var conversation = new Conversation();
    conversation.setModel("TEST_LLAMA_MODEL_PATH");
    conversation.setClientCode("llama.chat.completion");
    var settings = GeneralSettingsState.getInstance();

    settings.sync(conversation);

    assertThat(settings.getSelectedService()).isEqualTo(ServiceType.LLAMA_CPP);
    LlamaCompletionModel model = llamaSettings.getModel();
    assertTrue(model instanceof CustomLlamaModel);
    assertThat(((CustomLlamaModel) model).getModel()).isEqualTo("TEST_LLAMA_MODEL_PATH");
  }

  public void testLlamaSettingsHuggingFaceModelSync() {
    var llamaSettings = LlamaCppSettingsState.getInstance().getLocalSettings();
    llamaSettings.setModel(HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3);
    var conversation = new Conversation();
    conversation.setModel("CODE_LLAMA_7B_Q3");
    conversation.setClientCode("llama.chat.completion");
    var settings = GeneralSettingsState.getInstance();

    settings.sync(conversation);

    assertThat(settings.getSelectedService()).isEqualTo(ServiceType.LLAMA_CPP);
    assertThat(llamaSettings.getModel()).isEqualTo(CODE_LLAMA_7B_Q3);
  }
}
