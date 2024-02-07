package ee.carlrobert.codegpt.settings.state;

public class IncludedFilesSettingsState {

  public static final String DEFAULT_PROMPT_TEMPLATE =
      "Use the following context to answer question at the end:\n\n"
          + "{REPEATABLE_CONTEXT}\n\n"
          + "Question: {QUESTION}";
  public static final String DEFAULT_REPEATABLE_CONTEXT =
      "File Path: {FILE_PATH}\n"
          + "File Content:\n"
          + "{FILE_CONTENT}";

  private String promptTemplate = DEFAULT_PROMPT_TEMPLATE;
  private String repeatableContext = DEFAULT_REPEATABLE_CONTEXT;

  public String getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(String promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public String getRepeatableContext() {
    return repeatableContext;
  }

  public void setRepeatableContext(String repeatableContext) {
    this.repeatableContext = repeatableContext;
  }
}
