package ee.carlrobert.codegpt.completions.you;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouSubscription(String service, String tier, String month) {
}
