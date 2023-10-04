package ee.carlrobert.codegpt.completions.you.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouUser {

  private final List<YouEmail> emails;
  private final YouName name;
  private final String userId;

  public YouUser(
      @JsonProperty("emails") List<YouEmail> emails,
      @JsonProperty("name") YouName name,
      @JsonProperty("user_id") String userId) {
    this.emails = emails;
    this.name = name;
    this.userId = userId;
  }

  public List<YouEmail> getEmails() {
    return emails;
  }

  public YouName getName() {
    return name;
  }

  public String getUserId() {
    return userId;
  }
}
