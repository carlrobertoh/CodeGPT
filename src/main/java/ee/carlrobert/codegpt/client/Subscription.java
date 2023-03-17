package ee.carlrobert.codegpt.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {

  private final String accountName;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Subscription(@JsonProperty("account_name") String accountName) {
    this.accountName = accountName;
  }

  public String getAccountName() {
    return accountName;
  }
}
