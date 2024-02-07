package ee.carlrobert.codegpt.settings.service.llama.form;

import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.conversations.message.Message;
import java.util.List;

public class ChatPromptTemplatePanel extends BasePromptTemplatePanel<PromptTemplate> {

  public ChatPromptTemplatePanel(PromptTemplate initiallySelectedTemplate, boolean enabled) {
    super(
        PromptTemplate.class,
        initiallySelectedTemplate,
        enabled,
        "settingsConfigurable.service.llama.promptTemplate.comment");
  }

  @Override
  protected String buildPromptDescription(PromptTemplate template) {
    return template.buildPrompt(
        "SYSTEM_PROMPT",
        "USER_PROMPT",
        List.of(new Message("PREV_PROMPT", "PREV_RESPONSE")));
  }
}
