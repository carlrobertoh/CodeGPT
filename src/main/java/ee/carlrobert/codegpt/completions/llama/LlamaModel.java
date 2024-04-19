package ee.carlrobert.codegpt.completions.llama;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
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
      InfillPromptTemplate.LLAMA,
      List.of(
          HuggingFaceModel.CODE_LLAMA_7B_Q3,
          HuggingFaceModel.CODE_LLAMA_7B_Q4,
          HuggingFaceModel.CODE_LLAMA_7B_Q5,
          HuggingFaceModel.CODE_LLAMA_13B_Q3,
          HuggingFaceModel.CODE_LLAMA_13B_Q4,
          HuggingFaceModel.CODE_LLAMA_13B_Q5,
          HuggingFaceModel.CODE_LLAMA_34B_Q3,
          HuggingFaceModel.CODE_LLAMA_34B_Q4,
          HuggingFaceModel.CODE_LLAMA_34B_Q5)
  ),
  CODE_BOOGA(
      "CodeBooga",
      "CodeBooga is a high-performing code instruct model created by merging two existing"
          + " code models: "
          + "<ol><li>Phind-CodeLlama-34B-v2</li><li>WizardCoder-Python-34B-V1.0</li></ol>",
      PromptTemplate.ALPACA,
      List.of(
          HuggingFaceModel.CODE_BOOGA_34B_Q3,
          HuggingFaceModel.CODE_BOOGA_34B_Q4,
          HuggingFaceModel.CODE_BOOGA_34B_Q5)),
  DEEPSEEK_CODER(
      "Deepseek Coder",
      "Deepseek Coder is composed of a series of code language models, each trained "
          + "from scratch on 2T tokens, with a composition of 87% code and 13% natural language "
          + "in both English and Chinese. It achieves state-of-the-art performance among "
          + "open-source code models on multiple programming languages and various benchmarks.",
      PromptTemplate.ALPACA,
      InfillPromptTemplate.DEEPSEEK_CODER,
      List.of(
          HuggingFaceModel.DEEPSEEK_CODER_1_3B_Q3,
          HuggingFaceModel.DEEPSEEK_CODER_1_3B_Q4,
          HuggingFaceModel.DEEPSEEK_CODER_1_3B_Q5,
          HuggingFaceModel.DEEPSEEK_CODER_6_7B_Q3,
          HuggingFaceModel.DEEPSEEK_CODER_6_7B_Q4,
          HuggingFaceModel.DEEPSEEK_CODER_6_7B_Q5,
          HuggingFaceModel.DEEPSEEK_CODER_33B_Q3,
          HuggingFaceModel.DEEPSEEK_CODER_33B_Q4,
          HuggingFaceModel.DEEPSEEK_CODER_33B_Q5)),
  PHIND_CODE_LLAMA(
      "Phind Code Llama",
      "This model is fine-tuned from Phind-CodeLlama-34B-v1 on an additional 1.5B tokens "
          + "high-quality programming-related data, achieving 73.8% pass@1 on HumanEval. "
          + "It's the current state-of-the-art amongst open-source models.",
      PromptTemplate.ALPACA,
      List.of(
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q3,
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q4,
          HuggingFaceModel.PHIND_CODE_LLAMA_34B_Q5)),
  WIZARD_CODER_PYTHON(
      "WizardCoder - Python",
      "WizardCoder, a Code Evol-Instruct fine-tuned Code LLM, which achieves "
          + "the 73.2 pass@1 and surpasses GPT4 (2023/03/15), ChatGPT-3.5, "
          + "and Claude2 on the HumanEval Benchmarks.",
      PromptTemplate.ALPACA,
      List.of(
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3,
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q4,
          HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q5,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q3,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q4,
          HuggingFaceModel.WIZARD_CODER_PYTHON_13B_Q5,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q3,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q4,
          HuggingFaceModel.WIZARD_CODER_PYTHON_34B_Q5)),
  LLAMA_3(
      "Llama 3",
      "Llama 3 is a family of large language models (LLMs), a collection of pretrained and "
          + "instruction tuned generative text models in 8 and 70B sizes. The Llama 3 instruction "
          + "tuned models are optimized for dialogue use cases and outperform many of the available"
          + " open source chat models on common industry benchmarks. Further, in developing these "
          + "models, we took great care to optimize helpfulness and safety.",
      PromptTemplate.LLAMA,
      InfillPromptTemplate.LLAMA_3,
      List.of(
          HuggingFaceModel.LLAMA_3_8B_Q2_K,
          HuggingFaceModel.LLAMA_3_8B_Q3_K_L,
          HuggingFaceModel.LLAMA_3_8B_Q3_K_M,
          HuggingFaceModel.LLAMA_3_8B_Q3_K_S,
          HuggingFaceModel.LLAMA_3_8B_Q4_0,
          HuggingFaceModel.LLAMA_3_8B_Q4_1,
          HuggingFaceModel.LLAMA_3_8B_Q4_K_M,
          HuggingFaceModel.LLAMA_3_8B_Q4_K_S,
          HuggingFaceModel.LLAMA_3_8B_Q5_0,
          HuggingFaceModel.LLAMA_3_8B_Q5_1,
          HuggingFaceModel.LLAMA_3_8B_Q5_K_M,
          HuggingFaceModel.LLAMA_3_8B_Q5_K_S,
          HuggingFaceModel.LLAMA_3_8B_Q6_K,
          HuggingFaceModel.LLAMA_3_8B_Q8_0));

  private final String label;
  private final String description;
  private final PromptTemplate promptTemplate;
  private final InfillPromptTemplate infillPromptTemplate;
  private final List<HuggingFaceModel> huggingFaceModels;

  LlamaModel(
      String label,
      String description,
      PromptTemplate promptTemplate,
      List<HuggingFaceModel> huggingFaceModels) {
    this(label, description, promptTemplate, null, huggingFaceModels);
  }

  LlamaModel(
      String label,
      String description,
      PromptTemplate promptTemplate,
      InfillPromptTemplate infillPromptTemplate,
      List<HuggingFaceModel> huggingFaceModels) {
    this.label = label;
    this.description = description;
    this.promptTemplate = promptTemplate;
    this.infillPromptTemplate = infillPromptTemplate;
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

  public InfillPromptTemplate getInfillPromptTemplate() {
    return infillPromptTemplate;
  }

  public List<HuggingFaceModel> getHuggingFaceModels() {
    return huggingFaceModels;
  }

  public String getFormattedModelSizeRange() {
    var parameters = huggingFaceModels.stream()
        .map(HuggingFaceModel::getParameterSize)
        .collect(toSet());
    if (parameters.size() == 1) {
      return parameters.iterator().next() + "B";
    }
    return format("(%dB - %dB)", Collections.min(parameters), Collections.max(parameters));
  }

  public List<Integer> getSortedUniqueModelSizes() {
    return huggingFaceModels.stream()
        .map(HuggingFaceModel::getParameterSize)
        .collect(toSet())
        .stream()
        .sorted()
        .toList();
  }
}
