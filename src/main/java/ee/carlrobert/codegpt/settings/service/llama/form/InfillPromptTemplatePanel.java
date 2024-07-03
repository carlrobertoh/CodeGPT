package ee.carlrobert.codegpt.settings.service.llama.form;

import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.codecompletions.InfillRequestDetails;

public class InfillPromptTemplatePanel extends BasePromptTemplatePanel<InfillPromptTemplate> {

  public InfillPromptTemplatePanel(
      InfillPromptTemplate initiallySelectedTemplate,
      boolean enabled) {
    super(
        InfillPromptTemplate.class,
        initiallySelectedTemplate,
        enabled,
        "settingsConfigurable.service.llama.infillTemplate.comment");
  }

  @Override
  protected String buildPromptDescription(InfillPromptTemplate template) {
    return template.buildPrompt(new InfillRequestDetails("PREFIX", "SUFFIX", null));
  }
}
