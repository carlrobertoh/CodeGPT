package ee.carlrobert.codegpt.user.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionProduct {

  private final String id;
  private final boolean active;
  private final String name;
  private final String description;

  public SubscriptionProduct(
      @JsonProperty("id") String id,
      @JsonProperty("active") boolean active,
      @JsonProperty("name") String name,
      @JsonProperty("description") String description) {
    this.id = id;
    this.active = active;
    this.name = name;
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public boolean isActive() {
    return active;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
