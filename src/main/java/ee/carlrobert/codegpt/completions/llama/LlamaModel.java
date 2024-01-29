package ee.carlrobert.codegpt.completions.llama;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import ee.carlrobert.codegpt.completions.LlmModel;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public enum LlamaModel {
  CODE_LLAMA(
      "Code Llama",
      "Code Llama is a family of large language models for code based on Llama 2 "
          + "providing state-of-the-art performance among open models, infilling capabilities, "
          + "support for large input contexts, and zero-shot instruction following ability for "
          + "programming tasks.",
      PromptTemplate.LLAMA,
      List.of(
          LlamaHuggingFaceModel.CODE_LLAMA_7B_Q3,
          LlamaHuggingFaceModel.CODE_LLAMA_7B_Q4,
          LlamaHuggingFaceModel.CODE_LLAMA_7B_Q5,
          LlamaHuggingFaceModel.CODE_LLAMA_13B_Q3,
          LlamaHuggingFaceModel.CODE_LLAMA_13B_Q4,
          LlamaHuggingFaceModel.CODE_LLAMA_13B_Q5,
          LlamaHuggingFaceModel.CODE_LLAMA_34B_Q3,
          LlamaHuggingFaceModel.CODE_LLAMA_34B_Q4,
          LlamaHuggingFaceModel.CODE_LLAMA_34B_Q5)
  ),
  CODE_BOOGA(
      "CodeBooga",
      "CodeBooga is a high-performing code instruct model created by merging two existing"
          + " code models: "
          + "<ol><li>Phind-CodeLlama-34B-v2</li><li>WizardCoder-Python-34B-V1.0</li></ol>",
      PromptTemplate.ALPACA,
      List.of(
          LlamaHuggingFaceModel.CODE_BOOGA_34B_Q3,
          LlamaHuggingFaceModel.CODE_BOOGA_34B_Q4,
          LlamaHuggingFaceModel.CODE_BOOGA_34B_Q5)),
  DEEPSEEK_CODER(
      "Deepseek Coder",
      "Deepseek Coder is composed of a series of code language models, each trained "
          + "from scratch on 2T tokens, with a composition of 87% code and 13% natural language "
          + "in both English and Chinese. It achieves state-of-the-art performance among "
          + "open-source code models on multiple programming languages and various benchmarks.",
      PromptTemplate.ALPACA,
      List.of(
          LlamaHuggingFaceModel.DEEPSEEK_CODER_1_3B_Q3,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_1_3B_Q4,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_1_3B_Q5,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_6_7B_Q3,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_6_7B_Q4,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_6_7B_Q5,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_33B_Q3,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_33B_Q4,
          LlamaHuggingFaceModel.DEEPSEEK_CODER_33B_Q5)),
  PHIND_CODE_LLAMA(
      "Phind Code Llama",
      "This model is fine-tuned from Phind-CodeLlama-34B-v1 on an additional 1.5B tokens "
          + "high-quality programming-related data, achieving 73.8% pass@1 on HumanEval. "
          + "It's the current state-of-the-art amongst open-source models.",
      PromptTemplate.ALPACA,
      List.of(
          LlamaHuggingFaceModel.PHIND_CODE_LLAMA_34B_Q3,
          LlamaHuggingFaceModel.PHIND_CODE_LLAMA_34B_Q4,
          LlamaHuggingFaceModel.PHIND_CODE_LLAMA_34B_Q5)),
  WIZARD_CODER_PYTHON(
      "WizardCoder - Python",
      "WizardCoder, a Code Evol-Instruct fine-tuned Code LLM, which achieves "
          + "the 73.2 pass@1 and surpasses GPT4 (2023/03/15), ChatGPT-3.5, "
          + "and Claude2 on the HumanEval Benchmarks.",
      PromptTemplate.ALPACA,
      List.of(
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q4,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q5,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q3,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q4,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q5,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q3,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q4,
          LlamaHuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q5));

  private final String label;
  private final String description;
  private final PromptTemplate promptTemplate;
  private final List<LlamaHuggingFaceModel> llamaHuggingFaceModels;

  LlamaModel(
      String label,
      String description,
      PromptTemplate promptTemplate,
      List<LlamaHuggingFaceModel> llamaHuggingFaceModels) {
    this.label = label;
    this.description = description;
    this.promptTemplate = promptTemplate;
    this.llamaHuggingFaceModels = llamaHuggingFaceModels;
  }

  public static @NotNull LlamaModel findByHuggingFaceModel(
      LlamaHuggingFaceModel llamaHuggingFaceModel) {
    for (var llamaModel : LlamaModel.values()) {
      if (llamaModel.getHuggingFaceModels().contains(llamaHuggingFaceModel)) {
        return llamaModel;
      }
    }

    throw new RuntimeException("Unable to find correct LLM");
  }

  @Override
  public String toString() {
    return String.join(" ", label, getFormattedModelSizeRange());
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public List<LlamaHuggingFaceModel> getHuggingFaceModels() {
    return llamaHuggingFaceModels;
  }

  public String getFormattedModelSizeRange() {
    var parameters = llamaHuggingFaceModels.stream()
        .map(LlamaHuggingFaceModel::getParameterSize)
        .collect(toSet());
    if (parameters.size() == 1) {
      return parameters.iterator().next() + "B";
    }
    return format("(%dB - %dB)", Collections.min(parameters), Collections.max(parameters));
  }

  public List<Integer> getSortedUniqueModelSizes() {
    return llamaHuggingFaceModels.stream()
        .map(LlamaHuggingFaceModel::getParameterSize)
        .collect(toSet())
        .stream()
        .sorted()
        .collect(toList());
  }
}