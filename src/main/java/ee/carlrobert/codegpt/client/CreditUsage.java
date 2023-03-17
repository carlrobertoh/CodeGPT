package ee.carlrobert.codegpt.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditUsage {

  private final Double totalGranted;
  private final Double totalUsed;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CreditUsage(
      @JsonProperty("total_granted") Double totalGranted,
      @JsonProperty("total_used") Double totalUsed) {
    this.totalGranted = totalGranted;
    this.totalUsed = totalUsed;
  }

  public Double getTotalGranted() {
    return totalGranted;
  }

  public Double getTotalUsed() {
    return totalUsed;
  }
}
