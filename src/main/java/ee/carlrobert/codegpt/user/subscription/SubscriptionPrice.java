package ee.carlrobert.codegpt.user.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionPrice {

  private final String id;
  private final String productId;
  private final boolean active;
  private final SubscriptionProduct products;

  public SubscriptionPrice(
      @JsonProperty("id") String id,
      @JsonProperty("product_id") String productId,
      @JsonProperty("active") boolean active,
      @JsonProperty("products") SubscriptionProduct products) {
    this.id = id;
    this.productId = productId;
    this.active = active;
    this.products = products;
  }

  public String getId() {
    return id;
  }

  public String getProductId() {
    return productId;
  }

  public boolean isActive() {
    return active;
  }

  public SubscriptionProduct getProducts() {
    return products;
  }
}
