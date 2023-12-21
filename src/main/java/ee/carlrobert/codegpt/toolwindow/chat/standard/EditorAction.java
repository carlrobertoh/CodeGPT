package ee.carlrobert.codegpt.toolwindow.chat.standard;

enum EditorAction {
  FIND_BUGS(
      "Find Bugs",
      "Find bugs in the selected code",
      "Find bugs and output code with bugs fixed in the selected code: {{selectedCode}}"),
  WRITE_TESTS(
      "Write Tests",
      "Write unit tests for the selected code",
      "Write unit tests for the selected code: {{selectedCode}}"),
  EXPLAIN(
      "Explain",
      "Explain the selected code",
      "Explain the selected code: {{selectedCode}}"),
  REFACTOR(
      "Refactor",
      "Refactor the selected code",
      "Refactor the selected code: {{selectedCode}}"),
  OPTIMIZE(
      "Optimize",
      "Optimize the selected code",
      "Optimize the selected code: {{selectedCode}}");

  private final String label;
  private final String userMessage;
  private final String prompt;

  EditorAction(String label, String userMessage, String prompt) {
    this.label = label;
    this.userMessage = userMessage;
    this.prompt = prompt;
  }

  public String getLabel() {
    return label;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getUserMessage() {
    return userMessage;
  }
}
