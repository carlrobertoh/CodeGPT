package ee.carlrobert.codegpt.completions.llama;

import static java.lang.String.format;

import ee.carlrobert.codegpt.completions.LlmModel;
import java.net.MalformedURLException;
import java.net.URL;

public enum HuggingFaceModel implements LlmModel {

  CODE_LLAMA_7B_Q3(7, 3, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct-q3_K_M"),
  CODE_LLAMA_7B_Q4(7, 4, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct-q4_K_M"),
  CODE_LLAMA_7B_Q5(7, 5, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct-q5_K_M"),

  CODE_LLAMA_13B_Q3(13, 3, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct-q3_K_M"),
  CODE_LLAMA_13B_Q4(13, 4, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct-q4_K_M"),
  CODE_LLAMA_13B_Q5(13, 5, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct-q5_K_M"),

  CODE_LLAMA_34B_Q3(34, 3, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct-q3_K_M"),
  CODE_LLAMA_34B_Q4(34, 4, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct-q4_K_M"),
  CODE_LLAMA_34B_Q5(34, 5, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct-q5_K_M"),

  CODE_BOOGA_34B_Q3(34, 3, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1-q3_K_M"),
  CODE_BOOGA_34B_Q4(34, 4, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1-q4_K_M"),
  CODE_BOOGA_34B_Q5(34, 5, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1-q5_K_M"),

  DEEPSEEK_CODER_1_3B_Q3(1, 3, "deepseek-coder-1.3b-instruct-GGUF", "deepseek-coder:1.3b-instruct-q3_K_M"),
  DEEPSEEK_CODER_1_3B_Q4(1, 4, "deepseek-coder-1.3b-instruct-GGUF", "deepseek-coder:1.3b-instruct-q4_K_M"),
  DEEPSEEK_CODER_1_3B_Q5(1, 5, "deepseek-coder-1.3b-instruct-GGUF", "deepseek-coder:1.3b-instruct-q5_K_M"),

  DEEPSEEK_CODER_6_7B_Q3(7, 3, "deepseek-coder-6.7b-instruct-GGUF", "deepseek-coder:6.7b-instruct-q3_K_M"),
  DEEPSEEK_CODER_6_7B_Q4(7, 4, "deepseek-coder-6.7b-instruct-GGUF", "deepseek-coder:6.7b-instruct-q4_K_M"),
  DEEPSEEK_CODER_6_7B_Q5(7, 5, "deepseek-coder-6.7b-instruct-GGUF", "deepseek-coder:6.7b-instruct-q5_K_M"),

  DEEPSEEK_CODER_33B_Q3(33, 3, "deepseek-coder-33b-instruct-GGUF", "deepseek-coder:33b-instruct-q3_K_M"),
  DEEPSEEK_CODER_33B_Q4(33, 4, "deepseek-coder-33b-instruct-GGUF", "deepseek-coder:33b-instruct-q4_K_M"),
  DEEPSEEK_CODER_33B_Q5(33, 5, "deepseek-coder-33b-instruct-GGUF", "deepseek-coder:33b-instruct-q5_K_M"),

  PHIND_CODE_LLAMA_34B_Q3(34, 3, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-v2-q3_K_M"),
  PHIND_CODE_LLAMA_34B_Q4(34, 4, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-v2-q4_K_M"),
  PHIND_CODE_LLAMA_34B_Q5(34, 5, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-v2-q5_K_M"),

  WIZARD_CODER_PYTHON_7B_Q3(7, 3, "WizardCoder-Python-7B-V1.0-GGUF", "wizardcoder:7b-v1.1-q3_K_M"),
  WIZARD_CODER_PYTHON_7B_Q4(7, 4, "WizardCoder-Python-7B-V1.0-GGUF", "wizardcoder:7b-v1.1-q4_K_M"),
  WIZARD_CODER_PYTHON_7B_Q5(7, 5, "WizardCoder-Python-7B-V1.0-GGUF", "wizardcoder:7b-v1.1-q5_K_M"),

  WIZARD_CODER_PYTHON_13B_Q3(13, 3, "WizardCoder-Python-13B-V1.0-GGUF", "wizardcoder:13b-v1.1-q3_K_M"),
  WIZARD_CODER_PYTHON_13B_Q4(13, 4, "WizardCoder-Python-13B-V1.0-GGUF", "wizardcoder:13b-v1.1-q4_K_M"),
  WIZARD_CODER_PYTHON_13B_Q5(13, 5, "WizardCoder-Python-13B-V1.0-GGUF", "wizardcoder:13b-v1.1-q5_K_M"),

  WIZARD_CODER_PYTHON_34B_Q3(34, 3, "WizardCoder-Python-34B-V1.0-GGUF", "wizardcoder:34b-v1.1-q3_K_M"),
  WIZARD_CODER_PYTHON_34B_Q4(34, 4, "WizardCoder-Python-34B-V1.0-GGUF", "wizardcoder:34b-v1.1-q4_K_M"),
  WIZARD_CODER_PYTHON_34B_Q5(34, 5, "WizardCoder-Python-34B-V1.0-GGUF", "wizardcoder:34b-v1.1-q5_K_M");

  private final int parameterSize;
  private final int quantization;
  private final String modelFileName;
  private final String modelTag;

  HuggingFaceModel(int parameterSize, int quantization, String modelFileName, String modelTag) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.modelFileName = modelFileName;
    this.modelTag = modelTag;
  }

  @Override
  public int getParameterSize() {
    return parameterSize;
  }

  @Override
  public int getQuantization() {
    return quantization;
  }

  public String getCode() {
    return name();
  }

  public String getModelTag() {
    return modelTag;
  }

  public String getModelFileName() {
    return modelFileName.toLowerCase().replace("-gguf", format(".Q%d_K_M.gguf", quantization));
  }

  @Override
  public URL getModelUrl() {
    try {
      return new URL(
          format("https://huggingface.co/TheBloke/%s/resolve/main/%s", modelFileName, getModelFileName()));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String toString() {
    return format("%d-bit precision", quantization);
  }
}
