package ee.carlrobert.codegpt.completions;

import static java.lang.String.format;

public enum HuggingFaceModel {
  CODE_LLAMA_7B_Q3_K_M(
      7,
      3,
      5.80,
      3.30,
      "codellama-7b-instruct.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_7B_Q4_K_M(
      7,
      4,
      6.58,
      4.08,
      "codellama-7b-instruct.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_7B_Q5_K_M(
      7,
      5,
      7.28,
      4.78,
      "codellama-7b-instruct.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Instruct-GGUF/resolve/main/codellama-7b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_13B_Q3_K_M(
      13,
      3,
      8.84,
      6.34,
      "codellama-13b-instruct.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Instruct-GGUF/resolve/main/codellama-13b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_13B_Q4_K_M(
      13,
      4,
      10.37,
      7.87,
      "codellama-13b-instruct.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Instruct-GGUF/resolve/main/codellama-13b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_13B_Q5_K_M(
      13,
      5,
      11.73,
      9.23,
      "codellama-13b-instruct.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Instruct-GGUF/resolve/main/codellama-13b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_34B_Q3_K_M(
      34,
      3,
      18.78,
      16.28,
      "codellama-34b-instruct.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Instruct-GGUF/resolve/main/codellama-34b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_34B_Q4_K_M(
      34,
      4,
      22.72,
      20.22,
      "codellama-34b-instruct.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Instruct-GGUF/resolve/main/codellama-34b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_34B_Q5_K_M(
      34,
      5,
      26.34,
      23.84,
      "codellama-34b-instruct.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Instruct-GGUF/resolve/main/codellama-34b-instruct.Q5_K_M.gguf"),

  CODE_LLAMA_PYTHON_7B_Q3_K_M(
      7,
      3,
      5.8,
      3.3,
      "codellama-7b-python.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Python-GGUF/resolve/main/codellama-7b-python.Q3_K_M.gguf"),
  CODE_LLAMA_PYTHON_7B_Q4_K_M(
      7,
      4,
      6.58,
      4.08,
      "codellama-7b-python.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Python-GGUF/resolve/main/codellama-7b-python.Q4_K_M.gguf"),
  CODE_LLAMA_PYTHON_7B_Q5_K_M(
      7,
      5,
      7.28,
      4.78,
      "codellama-7b-python.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-7B-Python-GGUF/resolve/main/codellama-7b-python.Q5_K_M.gguf"),
  CODE_LLAMA_PYTHON_13B_Q3_K_M(
      13,
      3,
      8.84,
      6.34,
      "codellama-13b-python.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Python-GGUF/resolve/main/codellama-13b-python.Q3_K_M.gguf"),
  CODE_LLAMA_PYTHON_13B_Q4_K_M(
      13,
      4,
      10.37,
      7.87,
      "codellama-13b-python.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Python-GGUF/resolve/main/codellama-13b-python.Q4_K_M.gguf"),
  CODE_LLAMA_PYTHON_13B_Q5_K_M(
      13,
      5,
      11.73,
      9.23,
      "codellama-13b-python.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-13B-Python-GGUF/resolve/main/codellama-13b-python.Q5_K_M.gguf"),
  CODE_LLAMA_PYTHON_34B_Q3_K_M(
      34,
      3,
      18.78,
      16.28,
      "codellama-34b-python.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Python-GGUF/resolve/main/codellama-34b-python.Q3_K_M.gguf"),
  CODE_LLAMA_PYTHON_34B_Q4_K_M(
      34,
      4,
      22.72,
      20.22,
      "codellama-34b-python.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Python-GGUF/resolve/main/codellama-34b-python.Q4_K_M.gguf"),
  CODE_LLAMA_PYTHON_34B_Q5_K_M(
      34,
      5,
      26.34,
      23.84,
      "codellama-34b-python.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/CodeLlama-34B-Python-GGUF/resolve/main/codellama-34b-python.Q5_K_M.gguf"),

  WIZARD_CODER_PYTHON_7B_Q3_K_M(
      7,
      3,
      5.8,
      3.3,
      "wizardcoder-python-7b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-7B-V1.0-GGUF/resolve/main/wizardcoder-python-7b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q4_K_M(
      7,
      4,
      6.58,
      4.08,
      "wizardcoder-python-7b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-7B-V1.0-GGUF/resolve/main/wizardcoder-python-7b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q5_K_M(
      7,
      5,
      7.28,
      4.78,
      "wizardcoder-python-7b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-7B-V1.0-GGUF/resolve/main/wizardcoder-python-7b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q3_K_M(
      13,
      3,
      8.84,
      6.34,
      "wizardcoder-python-13b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-13B-V1.0-GGUF/resolve/main/wizardcoder-python-13b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q4_K_M(
      13,
      4,
      10.37,
      7.87,
      "wizardcoder-python-13b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-13B-V1.0-GGUF/resolve/main/wizardcoder-python-13b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q5_K_M(
      13,
      5,
      11.73,
      9.23,
      "wizardcoder-python-13b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-13B-V1.0-GGUF/resolve/main/wizardcoder-python-13b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q3_K_M(
      34,
      3,
      18.78,
      16.28,
      "wizardcoder-python-34b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-34B-V1.0-GGUF/resolve/main/wizardcoder-python-34b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q4_K_M(
      34,
      4,
      22.72,
      20.22,
      "wizardcoder-python-34b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-34B-V1.0-GGUF/resolve/main/wizardcoder-python-34b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q5_K_M(
      34,
      5,
      26.34,
      23.84,
      "wizardcoder-python-34b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/WizardCoder-Python-34B-V1.0-GGUF/resolve/main/wizardcoder-python-34b-v1.0.Q5_K_M.gguf"),

  TORA_CODE_7B_Q3_K_M(
      7,
      3,
      5.8,
      3.3,
      "tora-code-7b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-7B-v1.0-GGUF/resolve/main/tora-code-7b-v1.0.Q3_K_M.gguf"),
  TORA_CODE_7B_Q4_K_M(
      7,
      4,
      6.58,
      4.08,
      "tora-code-7b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-7B-v1.0-GGUF/resolve/main/tora-code-7b-v1.0.Q4_K_M.gguf"),
  TORA_CODE_7B_Q5_K_M(
      7,
      5,
      7.28,
      4.78,
      "tora-code-7b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-7B-v1.0-GGUF/resolve/main/tora-code-7b-v1.0.Q5_K_M.gguf"),
  TORA_CODE_13B_Q3_K_M(
      13,
      3,
      8.84,
      6.34,
      "tora-code-13b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-13B-v1.0-GGUF/resolve/main/tora-code-13b-v1.0.Q3_K_M.gguf"),
  TORA_CODE_13B_Q4_K_M(
      13,
      4,
      10.37,
      7.87,
      "tora-code-13b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-13B-v1.0-GGUF/resolve/main/tora-code-13b-v1.0.Q4_K_M.gguf"),
  TORA_CODE_13B_Q5_K_M(
      13,
      5,
      11.73,
      9.23,
      "tora-code-13b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-13B-v1.0-GGUF/resolve/main/tora-code-13b-v1.0.Q5_K_M.gguf"),
  TORA_CODE_34B_Q3_K_M(
      34,
      3,
      18.78,
      16.28,
      "tora-code-34b-v1.0.Q3_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-34B-v1.0-GGUF/resolve/main/tora-code-34b-v1.0.Q3_K_M.gguf"),
  TORA_CODE_34B_Q4_K_M(
      34,
      4,
      22.72,
      20.22,
      "tora-code-34b-v1.0.Q4_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-34B-v1.0-GGUF/resolve/main/tora-code-34b-v1.0.Q4_K_M.gguf"),
  TORA_CODE_34B_Q5_K_M(
      34,
      5,
      26.34,
      23.84,
      "tora-code-34b-v1.0.Q5_K_M.gguf",
      "https://huggingface.co/TheBloke/tora-code-34B-v1.0-GGUF/resolve/main/tora-code-34b-v1.0.Q5_K_M.gguf");

  private final int parameterSize;
  private final int quantization;
  private final double maxRAMRequired;
  private final double fileSize;
  private final String fileName;
  private final String filePath;

  HuggingFaceModel(int parameterSize, int quantization, double maxRAMRequired, double fileSize, String fileName, String filePath) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.maxRAMRequired = maxRAMRequired;
    this.fileSize = fileSize;
    this.fileName = fileName;
    this.filePath = filePath;
  }

  public int getParameterSize() {
    return parameterSize;
  }

  public int getQuantization() {
    return quantization;
  }

  public double getMaxRAMRequired() {
    return maxRAMRequired;
  }

  public double getFileSize() {
    return fileSize;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  @Override
  public String toString() {
    return format("Q%d_K_M (%.2f GB)", quantization, fileSize);
  }
}
