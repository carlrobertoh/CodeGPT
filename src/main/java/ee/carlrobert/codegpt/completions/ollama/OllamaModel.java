package ee.carlrobert.codegpt.completions.ollama;

import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;

public enum OllamaModel {

  CODE_LLAMA_7B_Q3(7, 3, "CodeLlama-7B-Instruct", "codellama:7b-instruct-q3_K_M"),
  CODE_LLAMA_7B_Q4(7, 4, "CodeLlama-7B-Instruct", "codellama:7b-instruct-q4_K_M"),
  CODE_LLAMA_7B_Q5(7, 5, "CodeLlama-7B-Instruct", "codellama:7b-instruct-q5_K_M"),
  CODE_LLAMA_13B_Q3(13, 3, "CodeLlama-13B-Instruct", "codellama:13b-instruct-q3_K_M"),
  CODE_LLAMA_13B_Q4(13, 4, "CodeLlama-13B-Instruct", "codellama:13b-instruct-q4_K_M"),
  CODE_LLAMA_13B_Q5(13, 5, "CodeLlama-13B-Instruct", "codellama:13b-instruct-q5_K_M"),
  CODE_LLAMA_34B_Q3(34, 3, "CodeLlama-34B-Instruct", "codellama:34b-instruct-q3_K_M"),
  CODE_LLAMA_34B_Q4(34, 4, "CodeLlama-34B-Instruct", "codellama:34b-instruct-q4_K_M"),
  CODE_LLAMA_34B_Q5(34, 5, "CodeLlama-34B-Instruct", "codellama:34b-instruct-q5_K_M");

   // TODO add other models
  // see https://ollama.ai/library/codellama/tags
  
  private final int parameterSize;
  private final int quantization;
  private final String modelName;
  private final String modelId;

  OllamaModel(int parameterSize, int quantization, String modelName, String modelId) {
    this.parameterSize = parameterSize;
    this.quantization = quantization;
    this.modelName = modelName;
    this.modelId = modelId;
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

  public String getModelName() {
    return modelName;
  }

  public String getModelId() {
    return modelId;
  }

  @Override
  public String toString() {
    return format("%d-bit precision", quantization);
  }
}
