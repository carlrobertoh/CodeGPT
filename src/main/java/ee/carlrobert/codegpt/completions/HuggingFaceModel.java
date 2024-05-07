package ee.carlrobert.codegpt.completions;

import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public enum HuggingFaceModel {

  CODE_LLAMA_7B_Q3(7, 3, "codellama-7b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_7B_Q4(7, 4, "codellama-7b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_7B_Q5(7, 5, "codellama-7b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_13B_Q3(13, 3, "codellama-13b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_13B_Q4(13, 4, "codellama-13b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_13B_Q5(13, 5, "codellama-13b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_34B_Q3(34, 3, "codellama-34b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_34B_Q4(34, 4, "codellama-34b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_34B_Q5(34, 5, "codellama-34b-instruct.Q5_K_M.gguf"),

  CODE_BOOGA_34B_Q3(34, 3, "codebooga-34b-v0.1.Q3_K_M.gguf"),
  CODE_BOOGA_34B_Q4(34, 4, "codebooga-34b-v0.1.Q4_K_M.gguf"),
  CODE_BOOGA_34B_Q5(34, 5, "codebooga-34b-v0.1.Q5_K_M.gguf"),

  DEEPSEEK_CODER_1_3B_Q3(1, 3, "deepseek-coder-1.3b-instruct.Q3_K_M.gguf", 0.705),
  DEEPSEEK_CODER_1_3B_Q4(1, 4, "deepseek-coder-1.3b-instruct.Q4_K_M.gguf", 0.874),
  DEEPSEEK_CODER_1_3B_Q5(1, 5, "deepseek-coder-1.3b-instruct.Q5_K_M.gguf", 1.0),
  DEEPSEEK_CODER_6_7B_Q3(7, 3, "deepseek-coder-6.7b-instruct.Q3_K_M.gguf"),
  DEEPSEEK_CODER_6_7B_Q4(7, 4, "deepseek-coder-6.7b-instruct.Q4_K_M.gguf"),
  DEEPSEEK_CODER_6_7B_Q5(7, 5, "deepseek-coder-6.7b-instruct.Q5_K_M.gguf"),
  DEEPSEEK_CODER_33B_Q3(33, 3, "deepseek-coder-33b-instruct.Q3_K_M.gguf", 16.1),
  DEEPSEEK_CODER_33B_Q4(33, 4, "deepseek-coder-33b-instruct.Q4_K_M.gguf", 19.9),
  DEEPSEEK_CODER_33B_Q5(33, 5, "deepseek-coder-33b-instruct.Q5_K_M.gguf", 23.5),

  PHIND_CODE_LLAMA_34B_Q3(34, 3, "phind-codellama-34b-v2.Q3_K_M.gguf"),
  PHIND_CODE_LLAMA_34B_Q4(34, 4, "phind-codellama-34b-v2.Q4_K_M.gguf"),
  PHIND_CODE_LLAMA_34B_Q5(34, 5, "phind-codellama-34b-v2.Q5_K_M.gguf"),

  WIZARD_CODER_PYTHON_7B_Q3(7, 3, "wizardcoder-python-7b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q4(7, 4, "wizardcoder-python-7b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q5(7, 5, "wizardcoder-python-7b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q3(13, 3, "wizardcoder-python-13b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q4(13, 4, "wizardcoder-python-13b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q5(13, 5, "wizardcoder-python-13b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q3(34, 3, "wizardcoder-python-34b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q4(34, 4, "wizardcoder-python-34b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q5(34, 5, "wizardcoder-python-34b-v1.0.Q5_K_M.gguf"),

  LLAMA_3_8B_IQ3_M(8, 3, "Meta-Llama-3-8B-Instruct-IQ3_M.gguf", "lmstudio-community", 3.78),
  LLAMA_3_8B_Q4_K_M(8, 4, "Meta-Llama-3-8B-Instruct-Q4_K_M.gguf", "lmstudio-community", 4.92),
  LLAMA_3_8B_Q5_K_M(8, 5, "Meta-Llama-3-8B-Instruct-Q5_K_M.gguf", "lmstudio-community", 5.73),
  LLAMA_3_8B_Q6_K(8, 6, "Meta-Llama-3-8B-Instruct-Q6_K.gguf", "lmstudio-community", 6.6),
  LLAMA_3_8B_Q8_0(8, 8, "Meta-Llama-3-8B-Instruct-Q8_0.gguf", "lmstudio-community", 8.54),
  LLAMA_3_70B_IQ1(70, 1, "Meta-Llama-3-70B-Instruct-IQ1_M.gguf", "lmstudio-community", 16.8),
  LLAMA_3_70B_IQ2_XS(70, 2, "Meta-Llama-3-70B-Instruct-IQ2_XS.gguf", "lmstudio-community", 21.1),
  LLAMA_3_70B_Q4_K_M(70, 4, "Meta-Llama-3-70B-Instruct-Q4_K_M.gguf", "lmstudio-community", 42.5),

  PHI_3_3_8B_4K_IQ4_NL(4, 4, "Phi-3-mini-4k-instruct-IQ4_NL.gguf", "lmstudio-community", 2.18),
  PHI_3_3_8B_4K_Q5_K_M(4, 5, "Phi-3-mini-4k-instruct-Q5_K_M.gguf", "lmstudio-community", 2.64),
  PHI_3_3_8B_4K_Q6_K(4, 6, "Phi-3-mini-4k-instruct-Q6_K.gguf", "lmstudio-community", 3.14),
  PHI_3_3_8B_4K_Q8_0(4, 8, "Phi-3-mini-4k-instruct-Q8_0.gguf", "lmstudio-community", 4.06),
  PHI_3_3_8B_4K_FP16(4, 16, "Phi-3-mini-4k-instruct-fp16.gguf", "lmstudio-community", 7.64),

  CODE_GEMMA_7B_Q3_K_M(7, 3, "codegemma-7b-it-Q3_K_M.gguf", "lmstudio-community", 4.37),
  CODE_GEMMA_7B_Q4_K_M(7, 4, "codegemma-7b-it-Q4_K_M.gguf", "lmstudio-community", 5.33),
  CODE_GEMMA_7B_Q5_K_M(7, 5, "codegemma-7b-it-Q5_K_M.gguf", "lmstudio-community", 6.14),
  CODE_GEMMA_7B_Q6_K(7, 6, "codegemma-7b-it-Q6_K.gguf", "lmstudio-community", 7.01),
  CODE_GEMMA_7B_Q8_0(7, 8, "codegemma-7b-it-Q8_0.gguf", "lmstudio-community", 9.08),
  ;

  private final int parameterSize;
  private final int quantization;
  private final String modelName;
  private final String user;
  private final Double downloadSize; // in GB

  HuggingFaceModel(int parameterSize, int quantization, String modelName) {
    this(parameterSize, quantization, modelName, "TheBloke", null);
  }

  HuggingFaceModel(int parameterSize, int quantization, String modelName, Double downloadSize) {
    this(parameterSize, quantization, modelName, "TheBloke", downloadSize);
  }

  HuggingFaceModel(int parameterSize, int quantization, String modelName, String user,
                   Double downloadSize) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.modelName = modelName;
    this.user = user;
    this.downloadSize = downloadSize;
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

  public Double getDownloadSize() {
    return downloadSize;
  }

  public String getFileName() {
    if ("TheBloke".equals(user)) {
      return modelName.toLowerCase().replace("-gguf", format(".Q%d_K_M.gguf", quantization));
    }
    return modelName;
  }

  public URL getFileURL() {
    try {
      return new URL(
          "https://huggingface.co/%s/%s/resolve/main/%s".formatted(user, getDirectory(), getFileName()));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public URL getHuggingFaceURL() {
    try {
      return new URL("https://huggingface.co/%s/%s".formatted(user, getDirectory()));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  private String getDirectory() {
    return modelName.replaceFirst("-[^.-]+\\.gguf$", "-GGUF");
  }

  public String getQuantizationLabel() {
    return format("%d-bit precision", quantization);
  }

  @Override
  public String toString() {
    return modelName;
  }
}
