package ee.carlrobert.codegpt.completions;

import static java.lang.String.format;

import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import java.net.MalformedURLException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public enum HuggingFaceModel {

  CODE_LLAMA_7B_Q3(7, 3, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct"),
  CODE_LLAMA_7B_Q4(7, 4, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct"),
  CODE_LLAMA_7B_Q5(7, 5, "CodeLlama-7B-Instruct-GGUF", "codellama:7b-instruct"),
  CODE_LLAMA_13B_Q3(13, 3, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct"),
  CODE_LLAMA_13B_Q4(13, 4, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct"),
  CODE_LLAMA_13B_Q5(13, 5, "CodeLlama-13B-Instruct-GGUF", "codellama:13b-instruct"),
  CODE_LLAMA_34B_Q3(34, 3, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct"),
  CODE_LLAMA_34B_Q4(34, 4, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct"),
  CODE_LLAMA_34B_Q5(34, 5, "CodeLlama-34B-Instruct-GGUF", "codellama:34b-instruct"),

  CODE_BOOGA_34B_Q3(34, 3, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1"),
  CODE_BOOGA_34B_Q4(34, 4, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1"),
  CODE_BOOGA_34B_Q5(34, 5, "CodeBooga-34B-v0.1-GGUF", "codebooga:34b-v0.1"),

  DEEPSEEK_CODER_1_3B_Q3(1, 3, "deepseek-coder-1.3b-instruct-GGUF",
      "deepseek-coder:1.3b-instruct"),
  DEEPSEEK_CODER_1_3B_Q4(1, 4, "deepseek-coder-1.3b-instruct-GGUF",
      "deepseek-coder:1.3b-instruct"),
  DEEPSEEK_CODER_1_3B_Q5(1, 5, "deepseek-coder-1.3b-instruct-GGUF",
      "deepseek-coder:1.3b-instruct"),
  DEEPSEEK_CODER_6_7B_Q3(7, 3, "deepseek-coder-6.7b-instruct-GGUF",
      "deepseek-coder:6.7b-instruct"),
  DEEPSEEK_CODER_6_7B_Q4(7, 4, "deepseek-coder-6.7b-instruct-GGUF",
      "deepseek-coder:6.7b-instruct"),
  DEEPSEEK_CODER_6_7B_Q5(7, 5, "deepseek-coder-6.7b-instruct-GGUF",
      "deepseek-coder:6.7b-instruct"),
  DEEPSEEK_CODER_33B_Q3(33, 3, "deepseek-coder-33b-instruct-GGUF",
      "deepseek-coder:33b-instruct"),
  DEEPSEEK_CODER_33B_Q4(33, 4, "deepseek-coder-33b-instruct-GGUF",
      "deepseek-coder:33b-instruct"),
  DEEPSEEK_CODER_33B_Q5(33, 5, "deepseek-coder-33b-instruct-GGUF",
      "deepseek-coder:33b-instruct"),

  PHIND_CODE_LLAMA_34B_Q3(34, 3, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-q3_K_M"),
  PHIND_CODE_LLAMA_34B_Q4(34, 4, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-q4_K_M"),
  PHIND_CODE_LLAMA_34B_Q5(34, 5, "Phind-CodeLlama-34B-v2-GGUF", "phind-codellama:34b-q5_K_M"),

  WIZARD_CODER_PYTHON_7B_Q3(7, 3, "WizardCoder-Python-7B-V1.0-GGUF",
      "wizardcoder:7b-python-q3_K_M"),
  WIZARD_CODER_PYTHON_7B_Q4(7, 4, "WizardCoder-Python-7B-V1.0-GGUF",
      "wizardcoder:7b-python-q4_K_M"),
  WIZARD_CODER_PYTHON_7B_Q5(7, 5, "WizardCoder-Python-7B-V1.0-GGUF",
      "wizardcoder:7b-python-q5_K_M"),
  WIZARD_CODER_PYTHON_13B_Q3(13, 3, "WizardCoder-Python-13B-V1.0-GGUF",
      "wizardcoder:13b-python-q3_K_M"),
  WIZARD_CODER_PYTHON_13B_Q4(13, 4, "WizardCoder-Python-13B-V1.0-GGUF",
      "wizardcoder:13b-python-q4_K_M"),
  WIZARD_CODER_PYTHON_13B_Q5(13, 5, "WizardCoder-Python-13B-V1.0-GGUF",
      "wizardcoder:13b-python-q5_K_M"),
  WIZARD_CODER_PYTHON_34B_Q3(34, 3, "WizardCoder-Python-34B-V1.0-GGUF",
      "wizardcoder:34b-python-q3_K_M"),
  WIZARD_CODER_PYTHON_34B_Q4(34, 4, "WizardCoder-Python-34B-V1.0-GGUF",
      "wizardcoder:34b-python-q4_K_M"),
  WIZARD_CODER_PYTHON_34B_Q5(34, 5, "WizardCoder-Python-34B-V1.0-GGUF",
      "wizardcoder:34b-python-q5_K_M");

  private final int parameterSize;
  private final int quantization;
  private final String modelName;
  private final String modelTag;

  HuggingFaceModel(int parameterSize, int quantization, String modelName, String modelTag) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.modelName = modelName;
    this.modelTag = modelTag;
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
    return modelName.replace("-gguf", format(".Q%d_K_M.gguf", quantization));
  }

  public String getOllamaTag() {
    return modelTag + format("-q%d_K_M", quantization);
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

  public String toText() {
    return format(
        "%s %dB (Q%d)",
        LlamaModel.findByHuggingFaceModel(this).getLabel(),
        getParameterSize(),
        getQuantization());
  }

  public static @NotNull HuggingFaceModel findByOllamaTag(String ollamaTag) {
    for (var llamaModel : HuggingFaceModel.values()) {
      if (llamaModel.getOllamaTag().equals(ollamaTag)) {
        return llamaModel;
      }
    }

    throw new RuntimeException(
        format("Unable to find correct HuggingFaceModel for ollamaTag: %s", ollamaTag));
  }
}
