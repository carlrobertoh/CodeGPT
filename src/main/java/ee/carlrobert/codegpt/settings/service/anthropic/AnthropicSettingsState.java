package ee.carlrobert.codegpt.settings.service.anthropic;

import ee.carlrobert.codegpt.settings.service.FeatureType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class AnthropicSettingsState {

  private String apiVersion = "2023-06-01";
  private String model = "claude-sonnet-4-20250514";
  private String baseHost = "";
  private String reasoningEffort = "";
  private boolean showReasoningSummary = true;
  private Map<String, String> featureReasoningEfforts = new HashMap<>();

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public String getReasoningEffort() {
    return reasoningEffort;
  }

  public void setReasoningEffort(String reasoningEffort) {
    this.reasoningEffort = reasoningEffort;
  }

  public Map<String, String> getFeatureReasoningEfforts() {
    return featureReasoningEfforts;
  }

  public void setFeatureReasoningEfforts(Map<String, String> featureReasoningEfforts) {
    this.featureReasoningEfforts = featureReasoningEfforts;
  }

  /**
   * Effort chosen for the feature, then the default effort, or null to keep the model default.
   */
  public @Nullable String getEffectiveReasoningEffort(FeatureType featureType) {
    var featureEffort = featureReasoningEfforts.get(featureType.name());
    if (featureEffort != null && !featureEffort.isBlank()) {
      return featureEffort;
    }
    return reasoningEffort == null || reasoningEffort.isBlank() ? null : reasoningEffort;
  }

  public void setFeatureReasoningEffort(FeatureType featureType, @Nullable String effort) {
    var updated = new HashMap<>(featureReasoningEfforts);
    if (effort == null || effort.isBlank()) {
      updated.remove(featureType.name());
    } else {
      updated.put(featureType.name(), effort);
    }
    featureReasoningEfforts = updated;
  }

  public boolean isShowReasoningSummary() {
    return showReasoningSummary;
  }

  public void setShowReasoningSummary(boolean showReasoningSummary) {
    this.showReasoningSummary = showReasoningSummary;
  }

  public boolean hasCustomBaseHost() {
    return baseHost != null && !baseHost.trim().isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnthropicSettingsState that = (AnthropicSettingsState) o;
    return Objects.equals(apiVersion, that.apiVersion)
            && Objects.equals(model, that.model)
            && Objects.equals(baseHost, that.baseHost)
            && Objects.equals(reasoningEffort, that.reasoningEffort)
            && showReasoningSummary == that.showReasoningSummary
            && Objects.equals(featureReasoningEfforts, that.featureReasoningEfforts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiVersion, model, baseHost, reasoningEffort, showReasoningSummary,
        featureReasoningEfforts);
  }
}
