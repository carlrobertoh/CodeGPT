package ee.carlrobert.codegpt.completions;

import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;

public enum HuggingFaceModel {

  CODE_LLAMA_7B_Q3(7, 3, "CodeLlama-7B-Instruct-GGUF"),
  CODE_LLAMA_7B_Q4(7, 4, "CodeLlama-7B-Instruct-GGUF"),
  CODE_LLAMA_7B_Q5(7, 5, "CodeLlama-7B-Instruct-GGUF"),
  CODE_LLAMA_13B_Q3(13, 3, "CodeLlama-13B-Instruct-GGUF"),
  CODE_LLAMA_13B_Q4(13, 4, "CodeLlama-13B-Instruct-GGUF"),
  CODE_LLAMA_13B_Q5(13, 5, "CodeLlama-13B-Instruct-GGUF"),
  CODE_LLAMA_34B_Q3(34, 3, "CodeLlama-34B-Instruct-GGUF"),
  CODE_LLAMA_34B_Q4(34, 4, "CodeLlama-34B-Instruct-GGUF"),
  CODE_LLAMA_34B_Q5(34, 5, "CodeLlama-34B-Instruct-GGUF"),

  CODE_BOOGA_34B_Q3(34, 3, "CodeBooga-34B-v0.1-GGUF"),
  CODE_BOOGA_34B_Q4(34, 4, "CodeBooga-34B-v0.1-GGUF"),
  CODE_BOOGA_34B_Q5(34, 5, "CodeBooga-34B-v0.1-GGUF"),

  PHIND_CODE_LLAMA_34B_Q3(34, 3, "Phind-CodeLlama-34B-v2-GGUF"),
  PHIND_CODE_LLAMA_34B_Q4(34, 4, "Phind-CodeLlama-34B-v2-GGUF"),
  PHIND_CODE_LLAMA_34B_Q5(34, 5, "Phind-CodeLlama-34B-v2-GGUF"),

  WIZARD_CODER_PYTHON_7B_Q3(7, 3, "WizardCoder-Python-7B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_7B_Q4(7, 4, "WizardCoder-Python-7B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_7B_Q5(7, 5, "WizardCoder-Python-7B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_13B_Q3(13, 3, "WizardCoder-Python-13B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_13B_Q4(13, 4, "WizardCoder-Python-13B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_13B_Q5(13, 5, "WizardCoder-Python-13B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_34B_Q3(34, 3, "WizardCoder-Python-34B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_34B_Q4(34, 4, "WizardCoder-Python-34B-V1.0-GGUF"),
  WIZARD_CODER_PYTHON_34B_Q5(34, 5, "WizardCoder-Python-34B-V1.0-GGUF");

  private final int parameterSize;
  private final int quantization;
  private final String modelName;

  HuggingFaceModel(int parameterSize, int quantization, String modelName) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.modelName = modelName;
  }

  public int getParameterSize() {
    return parameterSize;
  }

  public int getQuantization() {
    return quantization;
  }

  public String getCode() {
    return name();
  }

  public String getFileName() {
    return modelName.toLowerCase().replace("-gguf", format(".Q%d_K_M.gguf", quantization));
  }

  public URL getFileURL() {
    try {
      return new URL(
          format("https://huggingface.co/TheBloke/%s/resolve/main/%s", modelName, getFileName()));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public URL getHuggingFaceURL() {
    try {
      return new URL("https://huggingface.co/TheBloke/" + modelName);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String toString() {
    return format("%d-bit precision", quantization);
  }
}
