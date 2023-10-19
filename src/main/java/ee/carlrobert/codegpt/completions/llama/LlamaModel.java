package ee.carlrobert.codegpt.completions.llama;

public enum LlamaModel {
  CODE_LLAMA_7B(
      "TheBloke/CodeLlama-7B-Instruct (7.28 GB)",
      "codellama-7b-instruct.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_13B(
      "TheBloke/CodeLlama-13B-Instruct (9.23 GB)",
      "codellama-13b-instruct.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Instruct-GGUF/resolve/main/codellama-13b-instruct.Q5_K_M.gguf"),
  MISTRAL_7B(
      "TheBloke/Mistral-7B-Instruct (5.13 GB)",
      "mistral-7b-instruct-v0.1.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.1-GGUF/resolve/main/mistral-7b-instruct-v0.1.Q5_K_M.gguf"
  );

  private final String label;
  private final String filePath;
  private final String fileName;

  LlamaModel(String label, String fileName, String filePath) {
    this.label = label;
    this.fileName = fileName;
    this.filePath = filePath;
  }

  public String getLabel() {
    return label;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  @Override
  public String toString() {
    return label;
  }
}
