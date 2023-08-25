package ee.carlrobert.codegpt.user.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {

  private final String id;
  private final String userId;
  private final String status;
  private final String priceId;
  private final SubscriptionPrice prices;

  public Subscription(
      @JsonProperty("id") String id,
      @JsonProperty("user_id") String userId,
      @JsonProperty("status") String status,
      @JsonProperty("price_id") String priceId,
      @JsonProperty("prices") SubscriptionPrice prices) {
    this.id = id;
    this.userId = userId;
    this.status = status;
    this.priceId = priceId;
    this.prices = prices;
  }

  public String getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getStatus() {
    return status;
  }

  public String getPriceId() {
    return priceId;
  }

  public SubscriptionPrice getPrices() {
    return prices;
  }
}
