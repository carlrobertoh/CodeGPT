package ee.carlrobert.codegpt.settings.service.ollama.form.model;

import static java.lang.String.format;

import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel.OllamaModelDetails;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class OllamaChildModel {

  private final String tag;
  private final OllamaModelDetails details;

  public OllamaChildModel(String tag, OllamaModelDetails details) {
    this.tag = tag;
    this.details = details;
  }


  public int getParameterSize() {
    return Integer.parseInt(details.getParameterSize().split("B")[0]);
  }

  public int getQuantization() {
    return Character.getNumericValue(details.getQuantizationLevel().charAt(1));
  }

  public PromptTemplate getPromptTemplate() {
    return LlamaModel.findByHuggingFaceModel(
            HuggingFaceModel.findByOllamaTag(getTag()))
        .getPromptTemplate();
  }

  public InfillPromptTemplate getInfillPromptTemplate() {
    return LlamaModel.findByHuggingFaceModel(
            HuggingFaceModel.findByOllamaTag(getTag()))
        .getInfillPromptTemplate();
  }

  public String getDescription() {
    return format(
        "%s %dB (Q%d)",
        getTag(),
        getParameterSize(),
        getQuantization());
  }

  public URL getModelLink() {
    try {
      return URI.create("https://ollama.ai/library/" + getTag()).toURL();
    } catch (MalformedURLException e) {
      return null;
    }
  }


  public String getTag() {
    return tag;
  }

  public OllamaModelDetails getDetails() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OllamaChildModel that = (OllamaChildModel) o;
    return Objects.equals(tag, that.tag);
  }

  public static OllamaChildModel codellama() {
    OllamaModelDetails details = new OllamaModelDetails();
    details.setFamilies(List.of());
    details.setFamily("llama");
    details.setFormat("gguf");
    details.setParameterSize("7B");
    details.setQuantizationLevel("Q4_K_M");
    return new OllamaChildModel("codellama:7b-instruct-q4_K_M", details);
  }

  public static OllamaChildModel fromTag(String ollamaTag) {
    String[] split = ollamaTag.split(":");
    String family = split[0];
    String rest = split[1];
    int size = Integer.parseInt(rest.split("b")[0]);
    OllamaModelDetails details = new OllamaModelDetails();
    details.setFamilies(List.of());
    details.setFamily(family);
    details.setFormat("");
    details.setParameterSize(size + "B");
    details.setQuantizationLevel(rest.substring(rest.indexOf("-q") + 1).toUpperCase());
    return new OllamaChildModel(ollamaTag, details);
  }
}
