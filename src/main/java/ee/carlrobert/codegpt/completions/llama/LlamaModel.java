package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public enum LlamaModel {
  CODE_LLAMA(
      "Code Llama",
      PromptTemplate.LLAMA,
      List.of(
          HuggingFaceModel.CODE_LLAMA_7B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_7B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_7B_Q5_K_M,
          HuggingFaceModel.CODE_LLAMA_13B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_13B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_13B_Q5_K_M,
          HuggingFaceModel.CODE_LLAMA_34B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_34B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_34B_Q5_K_M)
  ),
  CODE_LLAMA_PYTHON(
      "Code Llama - Python",
      PromptTemplate.LLAMA,
      List.of(
          HuggingFaceModel.CODE_LLAMA_PYTHON_7B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_7B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_7B_Q5_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_13B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_13B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_13B_Q5_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_34B_Q3_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_34B_Q4_K_M,
          HuggingFaceModel.CODE_LLAMA_PYTHON_34B_Q5_K_M)),
  PHIND_CODE_LLAMA(
      "Phind Code Llama",
      PromptTemplate.ALPACA,
      List.of(
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q3_K_M,
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q4_K_M,
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q5_K_M)),
  WIZARD_CODER_PYTHON(
      "WizardCoder - Python",
      PromptTemplate.ALPACA,
      List.of(
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q4_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q5_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q3_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q4_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q5_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q3_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q4_K_M,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q5_K_M)),
  TORA_CODE(
      "ToRA Code",
      PromptTemplate.TORA,
      List.of(
          HuggingFaceModel.TORA_CODE_7B_Q3_K_M,
          HuggingFaceModel.TORA_CODE_7B_Q4_K_M,
          HuggingFaceModel.TORA_CODE_7B_Q5_K_M,
          HuggingFaceModel.TORA_CODE_13B_Q3_K_M,
          HuggingFaceModel.TORA_CODE_13B_Q4_K_M,
          HuggingFaceModel.TORA_CODE_13B_Q5_K_M,
          HuggingFaceModel.TORA_CODE_34B_Q3_K_M,
          HuggingFaceModel.TORA_CODE_34B_Q4_K_M,
          HuggingFaceModel.TORA_CODE_34B_Q5_K_M));

  private final String label;
  private final PromptTemplate promptTemplate;
  private final List<HuggingFaceModel> huggingFaceModels;

  LlamaModel(
      String label,
      PromptTemplate promptTemplate,
      List<HuggingFaceModel> huggingFaceModels) {
    this.label = label;
    this.promptTemplate = promptTemplate;
    this.huggingFaceModels = huggingFaceModels;
  }

  public static @NotNull LlamaModel findByHuggingFaceModel(HuggingFaceModel huggingFaceModel) {
    for (var llamaModel : LlamaModel.values()) {
      if (llamaModel.getHuggingFaceModels().contains(huggingFaceModel)) {
        return llamaModel;
      }
    }

    throw new RuntimeException("Unable to find correct LLM");
  }

  public String getLabel() {
    return label;
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  @Override
  public String toString() {
    return label;
  }

  public List<HuggingFaceModel> getHuggingFaceModels() {
    return huggingFaceModels;
  }
}