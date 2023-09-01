package ee.carlrobert.codegpt.settings;

import java.util.Map;

public enum CustomServicePreset {

  ANTHROPIC(new CustomServicePresetDetails(
      "Anthropic",
      "https://api.anthropic.com/v1/complete",
      "POST",
      Map.of(
          "Accept", "text/event-stream",
          "Anthropic-Version", "2023-06-01",
          "Content-Type", "application/json",
          "X-Api-Key", "MY_SECRET_KEY"
      ),
      Map.of(),
      "{\n" +
          "  \"model\": \"claude-2\",\n" +
          "  \"prompt\": {{PROMPT}},\n" +
          "  \"max_tokens_to_sample\": 256,\n" +
          "  \"stream\": true\n" +
          "}",
      "{\n" +
          "  \"completion\": {{COMPLETION}}\n" +
          "}")),
  HUGGINGFACE(new CustomServicePresetDetails("Huggingface", "", "POST", Map.of(), Map.of(), "", "")),
  LLAMA_2(new CustomServicePresetDetails(
      "Replicate",
      "https://api.replicate.com/v1/predictions",
      "POST",
      Map.of(
          "Accept", "text/event-stream",
          "Authorization", "Token $REPLICATE_API_TOKEN"),
      Map.of(),
      "{\n" +
          "  \"version\": \"2796ee9483c3fd7aa2e171d38f4ca12251a30609463dcfd4cd76703f22e96cdf\",\n" +
          "  \"input\": {\n" +
          "    \"prompt\": {{PROMPT}}\n" +
          "  },\n" +
          "  \"stream\": true\n" +
          "}",
      "data: Once upon a time...")),
  OPENAI(new CustomServicePresetDetails("OpenAI",
      "https://api.openai.com/v1/chat/completions",
      "POST",
      Map.of(
          "Accept", "text/event-stream",
          "Content-Type", "application/json",
          "Authorization", "Bearer MY_SECRET_KEY"),
      Map.of(),
      "{\n" +
          "  \"model\": \"gpt-3.5-turbo\",\n" +
          "  \"messages\": [\n" +
          "    {\n" +
          "      \"role\": \"system\",\n" +
          "      \"content\": {{SYSTEM_PROMPT}}\n" +
          "    },\n" +
          "    {\n" +
          "      \"role\": \"user\",\n" +
          "      \"content\": {{PROMPT_WITH_HISTORY}}\n" +
          "    }\n" +
          "  ]\n" +
          "}",
      "{\n" +
          "  \"choices\": [{\n" +
          "    \"index\": 0,\n" +
          "    \"message\": {\n" +
          "      \"role\": \"assistant\",\n" +
          "      \"content\": {{COMPLETION}},\n" +
          "    }\n" +
          "  }]\n" +
          "}")),
  AZURE_OPENAI(new CustomServicePresetDetails(
      "Azure OpenAI",
      "",
      "POST",
      Map.of("Accept", "text/event-stream"),
      Map.of(),
      "",
      ""));

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
