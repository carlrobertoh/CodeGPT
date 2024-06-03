package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.HuggingFaceModel.Model.CST;
import static ee.carlrobert.codegpt.completions.HuggingFaceModel.Model.P3M;
import static ee.carlrobert.codegpt.completions.HuggingFaceModel.Model.SC3;
import static ee.carlrobert.codegpt.completions.llama.LlamaModel.getDownloadedMarker;
import static ee.carlrobert.codegpt.completions.llama.LlamaModel.getLlamaModelsPath;
import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;

public enum HuggingFaceModel {

  CODE_LLAMA_7B_Q3(7, 3, "CodeLlama-7B-Instruct-GGUF", "codellama-7b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_7B_Q4(7, 4, "CodeLlama-7B-Instruct-GGUF", "codellama-7b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_7B_Q5(7, 5, "CodeLlama-7B-Instruct-GGUF", "codellama-7b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_13B_Q3(13, 3, "CodeLlama-13B-Instruct-GGUF", "codellama-13b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_13B_Q4(13, 4, "CodeLlama-13B-Instruct-GGUF", "codellama-13b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_13B_Q5(13, 5, "CodeLlama-13B-Instruct-GGUF", "codellama-13b-instruct.Q5_K_M.gguf"),
  CODE_LLAMA_34B_Q3(34, 3, "CodeLlama-34B-Instruct-GGUF", "codellama-34b-instruct.Q3_K_M.gguf"),
  CODE_LLAMA_34B_Q4(34, 4, "CodeLlama-34B-Instruct-GGUF", "codellama-34b-instruct.Q4_K_M.gguf"),
  CODE_LLAMA_34B_Q5(34, 5, "CodeLlama-34B-Instruct-GGUF", "codellama-34b-instruct.Q5_K_M.gguf"),

  CODE_BOOGA_34B_Q3(34, 3, "CodeBooga-34B-v0.1-GGUF", "codebooga-34b-v0.1.Q3_K_M.gguf"),
  CODE_BOOGA_34B_Q4(34, 4, "CodeBooga-34B-v0.1-GGUF", "codebooga-34b-v0.1.Q4_K_M.gguf"),
  CODE_BOOGA_34B_Q5(34, 5, "CodeBooga-34B-v0.1-GGUF", "codebooga-34b-v0.1.Q5_K_M.gguf"),

  DEEPSEEK_CODER_1_3B_Q3(1, 3, "deepseek-coder-1.3b-instruct-GGUF",
          "deepseek-coder-1.3b-instruct.Q3_K_M.gguf", 0.705),
  DEEPSEEK_CODER_1_3B_Q4(1, 4, "deepseek-coder-1.3b-instruct-GGUF",
          "deepseek-coder-1.3b-instruct.Q4_K_M.gguf", 0.874),
  DEEPSEEK_CODER_1_3B_Q5(1, 5, "deepseek-coder-1.3b-instruct-GGUF",
          "deepseek-coder-1.3b-instruct.Q5_K_M.gguf", 1.0),
  DEEPSEEK_CODER_6_7B_Q3(7, 3, "deepseek-coder-6.7b-instruct-GGUF",
          "deepseek-coder-6.7b-instruct.Q3_K_M.gguf"),
  DEEPSEEK_CODER_6_7B_Q4(7, 4, "deepseek-coder-6.7b-instruct-GGUF",
          "deepseek-coder-6.7b-instruct.Q4_K_M.gguf"),
  DEEPSEEK_CODER_6_7B_Q5(7, 5, "deepseek-coder-6.7b-instruct-GGUF",
          "deepseek-coder-6.7b-instruct.Q5_K_M.gguf"),
  DEEPSEEK_CODER_33B_Q3(33, 3, "deepseek-coder-33b-instruct-GGUF",
          "deepseek-coder-33b-instruct.Q3_K_M.gguf", 16.1),
  DEEPSEEK_CODER_33B_Q4(33, 4, "deepseek-coder-33b-instruct-GGUF",
          "deepseek-coder-33b-instruct.Q4_K_M.gguf", 19.9),
  DEEPSEEK_CODER_33B_Q5(33, 5, "deepseek-coder-33b-instruct-GGUF",
          "deepseek-coder-33b-instruct.Q5_K_M.gguf", 23.5),

  PHIND_CODE_LLAMA_34B_Q3(34, 3, "Phind-CodeLlama-34B-v2-GGUF",
          "phind-codellama-34b-v2.Q3_K_M.gguf"),
  PHIND_CODE_LLAMA_34B_Q4(34, 4, "Phind-CodeLlama-34B-v2-GGUF",
          "phind-codellama-34b-v2.Q4_K_M.gguf"),
  PHIND_CODE_LLAMA_34B_Q5(34, 5, "Phind-CodeLlama-34B-v2-GGUF",
          "phind-codellama-34b-v2.Q5_K_M.gguf"),

  WIZARD_CODER_PYTHON_7B_Q3(7, 3, "WizardCoder-Python-7B-V1.0-GGUF",
          "wizardcoder-python-7b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q4(7, 4, "WizardCoder-Python-7B-V1.0-GGUF",
          "wizardcoder-python-7b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_7B_Q5(7, 5, "WizardCoder-Python-7B-V1.0-GGUF",
          "wizardcoder-python-7b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q3(13, 3, "WizardCoder-Python-13B-V1.0-GGUF",
          "wizardcoder-python-13b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q4(13, 4, "WizardCoder-Python-13B-V1.0-GGUF",
          "wizardcoder-python-13b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_13B_Q5(13, 5, "WizardCoder-Python-13B-V1.0-GGUF",
          "wizardcoder-python-13b-v1.0.Q5_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q3(34, 3, "WizardCoder-Python-34B-V1.0-GGUF",
          "wizardcoder-python-34b-v1.0.Q3_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q4(34, 4, "WizardCoder-Python-34B-V1.0-GGUF",
          "wizardcoder-python-34b-v1.0.Q4_K_M.gguf"),
  WIZARD_CODER_PYTHON_34B_Q5(34, 5, "WizardCoder-Python-34B-V1.0-GGUF",
          "wizardcoder-python-34b-v1.0.Q5_K_M.gguf"),

  LLAMA_3_8B_IQ3_M(8, 3, "Meta-Llama-3-8B-Instruct-GGUF", "Meta-Llama-3-8B-Instruct-IQ3_M.gguf",
          "lmstudio-community", 3.78),
  LLAMA_3_8B_Q4_K_M(8, 4, "Meta-Llama-3-8B-Instruct-GGUF", "Meta-Llama-3-8B-Instruct-Q4_K_M.gguf",
          "lmstudio-community", 4.92),
  LLAMA_3_8B_Q5_K_M(8, 5, "Meta-Llama-3-8B-Instruct-GGUF", "Meta-Llama-3-8B-Instruct-Q5_K_M.gguf",
          "lmstudio-community", 5.73),
  LLAMA_3_8B_Q6_K(8, 6, "Meta-Llama-3-8B-Instruct-GGUF", "Meta-Llama-3-8B-Instruct-Q6_K.gguf",
          "lmstudio-community", 6.6),
  LLAMA_3_8B_Q8_0(8, 8, "Meta-Llama-3-8B-Instruct-GGUF", "Meta-Llama-3-8B-Instruct-Q8_0.gguf",
          "lmstudio-community", 8.54),
  LLAMA_3_70B_IQ1(70, 1, "Meta-Llama-3-70B-Instruct-GGUF", "Meta-Llama-3-70B-Instruct-IQ1_M.gguf",
          "lmstudio-community", 16.8),
  LLAMA_3_70B_IQ2_XS(70, 2, "Meta-Llama-3-70B-Instruct-GGUF",
          "Meta-Llama-3-70B-Instruct-IQ2_XS.gguf", "lmstudio-community", 21.1),
  LLAMA_3_70B_Q4_K_M(70, 4, "Meta-Llama-3-70B-Instruct-GGUF",
          "Meta-Llama-3-70B-Instruct-Q4_K_M.gguf", "lmstudio-community", 42.5),

  PHI_3_3_8B_4K_IQ4_NL(4, 4, "Phi-3-mini-4k-instruct-GGUF", "Phi-3-mini-4k-instruct-IQ4_NL.gguf",
          "lmstudio-community", 2.18),
  PHI_3_3_8B_4K_Q5_K_M(4, 5, "Phi-3-mini-4k-instruct-GGUF", "Phi-3-mini-4k-instruct-Q5_K_M.gguf",
          "lmstudio-community", 2.64),
  PHI_3_3_8B_4K_Q6_K(4, 6, "Phi-3-mini-4k-instruct-GGUF", "Phi-3-mini-4k-instruct-Q6_K.gguf",
          "lmstudio-community", 3.14),
  PHI_3_3_8B_4K_Q8_0(4, 8, "Phi-3-mini-4k-instruct-GGUF", "Phi-3-mini-4k-instruct-Q8_0.gguf",
          "lmstudio-community", 4.06),
  PHI_3_3_8B_4K_FP16(4, 16, "Phi-3-mini-4k-instruct-GGUF", "Phi-3-mini-4k-instruct-fp16.gguf",
          "lmstudio-community", 7.64),

  CODE_GEMMA_7B_Q3_K_L(7, 3, "codegemma-1.1-7b-it-GGUF", "codegemma-1.1-7b-it-Q3_K_L.gguf",
          "lmstudio-community", 4.71),
  CODE_GEMMA_7B_Q4_K_M(7, 4, "codegemma-1.1-7b-it-GGUF", "codegemma-1.1-7b-it-Q4_K_M.gguf",
          "lmstudio-community", 5.33),
  CODE_GEMMA_7B_Q5_K_M(7, 5, "codegemma-1.1-7b-it-GGUF", "codegemma-1.1-7b-it-Q5_K_M.gguf",
          "lmstudio-community", 6.14),
  CODE_GEMMA_7B_Q6_K(7, 6, "codegemma-1.1-7b-it-GGUF", "codegemma-1.1-7b-it-Q6_K.gguf",
          "lmstudio-community", 7.01),
  CODE_GEMMA_7B_Q8_0(7, 8, "codegemma-1.1-7b-it-GGUF", "codegemma-1.1-7b-it-Q8_0.gguf",
          "lmstudio-community", 9.08),

  CODE_QWEN_1_5_7B_Q3_K_M(7, 3, "Qwen_-_CodeQwen1.5-7B-Chat-gguf",
          "CodeQwen1.5-7B-Chat.Q3_K_M.gguf", "RichardErkhov", 3.81),
  CODE_QWEN_1_5_7B_Q4_K_M(7, 4, "Qwen_-_CodeQwen1.5-7B-Chat-gguf",
          "CodeQwen1.5-7B-Chat.Q4_K_M.gguf", "RichardErkhov", 4.74),
  CODE_QWEN_1_5_7B_Q5_K_M(7, 5, "Qwen_-_CodeQwen1.5-7B-Chat-gguf",
          "CodeQwen1.5-7B-Chat.Q5_K_M.gguf", "RichardErkhov", 5.43),
  CODE_QWEN_1_5_7B_Q6_K(7, 6, "Qwen_-_CodeQwen1.5-7B-Chat-gguf",
          "CodeQwen1.5-7B-Chat.Q6_K.gguf", "RichardErkhov", 6.38),

  STABLE_CODE_3B_Q3_K_M(SC3, 3, "stable-code-instruct-3b-Q3_K_M.gguf", 1.39),
  STABLE_CODE_3B_Q4_K_M(SC3, 4, "stable-code-instruct-3b-Q4_K_M.gguf", 1.71),
  STABLE_CODE_3B_Q5_K_M(SC3, 5, "stable-code-instruct-3b-Q5_K_M.gguf", 1.99),
  STABLE_CODE_3B_Q6_K(SC3, 6, "stable-code-instruct-3b-Q6_K.gguf", 2.3),
  STABLE_CODE_3B_Q8_0(SC3, 8, "stable-code-instruct-3b-Q8_0.gguf", 2.97),

  PHI_3_14B_128K_IQ3_M(P3M, 3, "Phi-3-medium-128k-instruct-IQ3_M.gguf", 6.47),
  PHI_3_14B_128K_Q3_K_M(P3M, 3, "Phi-3-medium-128k-instruct-Q3_K_M.gguf", 6.92),
  PHI_3_14B_128K_IQ4_NL(P3M, 4, "Phi-3-medium-128k-instruct-IQ4_NL.gguf", 7.9),
  PHI_3_14B_128K_Q4_K_M(P3M, 4, "Phi-3-medium-128k-instruct-Q4_K_M.gguf", 8.57),
  PHI_3_14B_128K_Q5_K_M(P3M, 5, "Phi-3-medium-128k-instruct-Q5_K_M.gguf", 10.1),
  PHI_3_14B_128K_Q6_K(P3M, 6, "Phi-3-medium-128k-instruct-Q6_K.gguf", 11.5),
  PHI_3_14B_128K_Q8_0(P3M, 8, "Phi-3-medium-128k-instruct-Q8_0.gguf", 14.8),

  CODESTRAL_22B_32K_Q3_K_M(CST, 3, "Codestral-22B-v0.1-Q3_K_M.gguf", 10.8),
  CODESTRAL_22B_32K_Q4_K_M(CST, 4, "Codestral-22B-v0.1-Q4_K_M.gguf", 13.3),
  CODESTRAL_22B_32K_Q5_K_M(CST, 5, "Codestral-22B-v0.1-Q5_K_M.gguf", 15.7),
  CODESTRAL_22B_32K_Q6_K(CST, 6, "Codestral-22B-v0.1-Q6_K.gguf", 18.3),
  CODESTRAL_22B_32K_Q8_0(CST, 8, "Codestral-22B-v0.1-Q8_0.gguf", 23.6),
  ;

  enum Model {
    SC3("bartowski", 3, "stable-code-instruct-3b-GGUF"),
    P3M("bartowski", 14, "Phi-3-medium-128k-instruct-GGUF"),
    CST("bartowski", 22, "Codestral-22B-v0.1-GGUF"),
    ;

    private final String user;
    private final int parameterSize;
    private final String directory;
    private final String prefix;

    Model(String user, int parameterSize, String directory) {
      this(user, parameterSize, directory, null);
    }

    Model(String user, int parameterSize, String directory, String prefix) {
      this.user = user;
      this.parameterSize = parameterSize;
      this.directory = directory;
      this.prefix = prefix;
    }
  }

  private final int parameterSize;
  private final int quantization;
  private final String directory;
  private final String fileName;
  private final String user;
  private final Double downloadSize; // in GB

  HuggingFaceModel(int parameterSize, int quantization, String directory, String fileName) {
    this(parameterSize, quantization, directory, fileName, "TheBloke", null);
  }

  HuggingFaceModel(int parameterSize, int quantization, String directory, String fileName,
                   Double downloadSize) {
    this(parameterSize, quantization, directory, fileName, "TheBloke", downloadSize);
  }

  HuggingFaceModel(Model m, int quantization, String fileName, Double downloadSize) {
    this(m.parameterSize, quantization, m.directory, fileName, m.user, downloadSize);
  }

  HuggingFaceModel(int parameterSize, int quantization, String directory, String fileName,
                   String user, Double downloadSize) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.directory = directory;
    this.fileName = fileName;
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
    return fileName;
  }

  public URL getFileURL() {
    try {
      return new URL(
          "https://huggingface.co/%s/%s/resolve/main/%s".formatted(user, directory, fileName));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public URL getHuggingFaceURL() {
    try {
      return new URL("https://huggingface.co/%s/%s".formatted(user, directory));
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String getQuantizationLabel() {
    return format("%s %d-bit precision", downloaded(), quantization);
  }

  public boolean isDownloaded() {
    return getLlamaModelsPath().resolve(fileName).toFile().exists();
  }

  private @NotNull String downloaded() {
    return getDownloadedMarker(isDownloaded());
  }
}
