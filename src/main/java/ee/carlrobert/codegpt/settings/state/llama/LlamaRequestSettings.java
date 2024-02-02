package ee.carlrobert.codegpt.settings.state.llama;

/**
 * All settings for Llama completion requests
 */
public class LlamaRequestSettings {

  private int topK = 40;
  private double topP = 0.9;
  private double minP = 0.05;
  private double repeatPenalty = 1.1;

  public LlamaRequestSettings() {
  }

  public LlamaRequestSettings(int topK, double topP, double minP, double repeatPenalty) {
    this.topK = topK;
    this.topP = topP;
    this.minP = minP;
    this.repeatPenalty = repeatPenalty;
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

  public double getMinP() {
    return minP;
  }

  public void setMinP(double minP) {
    this.minP = minP;
  }

  public double getRepeatPenalty() {
    return repeatPenalty;
  }

  public void setRepeatPenalty(double repeatPenalty) {
    this.repeatPenalty = repeatPenalty;
  }

  public boolean isModified(LlamaRequestSettings llamaRequestSettings) {
    return topK != llamaRequestSettings.getTopK()
        || topP != llamaRequestSettings.getTopP()
        || minP != llamaRequestSettings.getMinP()
        || repeatPenalty != llamaRequestSettings.getRepeatPenalty();
  }
}
