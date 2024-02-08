package ee.carlrobert.codegpt.settings.service.ollama;

import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import java.util.Objects;

public class OllamaSettingsState {

  private HuggingFaceModel huggingFaceModel = HuggingFaceModel.CODE_LLAMA_7B_Q4;
  private String baseHost = "http://localhost:8080";
  private int topK = 40;
  private double topP = 0.9;
  private double repeatPenalty = 1.1;

  public HuggingFaceModel getHuggingFaceModel() {
    return huggingFaceModel;
  }

  public void setHuggingFaceModel(HuggingFaceModel huggingFaceModel) {
    this.huggingFaceModel = huggingFaceModel;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public int getTopK() {
    return topK;
  }

  public void setTopK(int topK) {
    this.topK = topK;
  }

  public double getTopP() {
    return topP;
  }

  public void setTopP(double topP) {
    this.topP = topP;
  }

  public double getRepeatPenalty() {
    return repeatPenalty;
  }

  public void setRepeatPenalty(double repeatPenalty) {
    this.repeatPenalty = repeatPenalty;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OllamaSettingsState that = (OllamaSettingsState) o;
    return topK == that.topK
        && Double.compare(that.topP, topP) == 0
        && Double.compare(that.repeatPenalty, repeatPenalty) == 0
        && huggingFaceModel == that.huggingFaceModel
        && Objects.equals(baseHost, that.baseHost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(huggingFaceModel, baseHost, topK, topP, repeatPenalty);
  }
}
