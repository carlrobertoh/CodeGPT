package ee.carlrobert.codegpt.settings;

import java.util.Map;

public enum CustomServicePreset {

  ANTHROPIC(new CustomServicePresetDetails(
      "Anthropic Completion",
      "https://api.anthropic.com/v1/complete",
      "POST",
      Map.of(
          "anthropic-version", "2023-06-01",
          "content-type", "application/json",
          "x-api-key", "MY_SECRET_KEY"
      ),
      Map.of(),
      "{\n" +
          "  \"model\": \"claude-2\",\n" +
          "  \"prompt\": {{PROMPT}}\n" +
          "  \"max_tokens_to_sample\": 256,\n" +
          "  \"stream\": true\n" +
          "}",
      "{\n" +
          "  \"completion\": {{COMPLETION}}\n" +
          "}")),
  HUGGINGFACE(new CustomServicePresetDetails("Huggingface Completion", "", "POST", Map.of(), Map.of(), "", "")),
  LLAMA_2(new CustomServicePresetDetails("Llama 2 Completion", "", "POST", Map.of(), Map.of(), "", "")),
  OPENAI(new CustomServicePresetDetails("OpenAI Completion",
      "https://api.openai.com/v1/chat/completions",
      "POST",
      Map.of(
          "Content-Type", "application/json",
          "Authorization", "Bearer MY_SECRET_KEY"),
      Map.of(),
      "{\n" +
          "  \"model\": \"gpt-3.5-turbo\",\n" +
          "  \"messages\": [\n" +
          "    {\n" +
          "      \"role\": \"system\",\n" +
          "      \"content\": \"You are a helpful assistant.\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"role\": \"user\",\n" +
          "      \"content\": \"Hello!\"\n" +
          "    }\n" +
          "  ]\n" +
          "}",
      "{\n" +
          "  \"choices\": [{\n" +
          "    \"index\": 0,\n" +
          "    \"message\": {\n" +
          "      \"role\": \"assistant\",\n" +
          "      \"content\": \"\\n\\nHello there, how may I assist you today?\",\n" +
          "    }\n" +
          "  }]\n" +
          "}")),
  AZURE_OPENAI(new CustomServicePresetDetails("Azure OpenAI Completion", "", "POST", Map.of(), Map.of(), "", ""));

  private final CustomServicePresetDetails presetDetails;

  CustomServicePreset(CustomServicePresetDetails presetDetails) {
    this.presetDetails = presetDetails;
  }

  public CustomServicePresetDetails getPresetDetails() {
    return presetDetails;
  }

  @Override
  public String toString() {
    return presetDetails.getDescription();
  }
}
