package ee.carlrobert.codegpt.completions.you;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouSubscription {

  private final String service;
  private final String tier;
  private final String month;

  public YouSubscription(
      @JsonProperty("service") String service,
      @JsonProperty("tier") String tier,
      @JsonProperty("month") String month) {
    this.service = service;
    this.tier = tier;
    this.month = month;
  }

  public String getService() {
    return service;
  }

  public String getTier() {
    return tier;
  }

  public String getMonth() {
    return month;
  }
}

