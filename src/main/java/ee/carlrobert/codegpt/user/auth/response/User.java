package ee.carlrobert.codegpt.user.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

  private final List<Email> emails;
  private final Name name;
  private final String userId;

  public User(
      @JsonProperty("emails") List<Email> emails,
      @JsonProperty("name") Name name,
      @JsonProperty("user_id") String userId) {
    this.emails = emails;
    this.name = name;
    this.userId = userId;
  }

  public List<Email> getEmails() {
    return emails;
  }

  public Name getName() {
    return name;
  }

  public String getUserId() {
    return userId;
  }
}
