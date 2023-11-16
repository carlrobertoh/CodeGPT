package ee.carlrobert.codegpt.toolwindow.chat.standard;

enum EditorAction {
  FIND_BUGS(
      "Find Bugs",
      "Find bugs in the following code",
      "Find bugs and output code with bugs fixed in the following code: {{selectedCode}}"),
  WRITE_TESTS(
      "Write Tests",
      "Write Tests for the following code",
      "Write Tests for the following code: {{selectedCode}}"),
  EXPLAIN(
      "Explain",
      "Explain the following code",
      "Explain the following code: {{selectedCode}}"),
  REFACTOR(
      "Refactor",
      "Refactor the following code",
      "Refactor the following code: {{selectedCode}}"),
  OPTIMIZE(
      "Optimize",
      "Optimize the following code",
      "Optimize the following code: {{selectedCode}}");

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
