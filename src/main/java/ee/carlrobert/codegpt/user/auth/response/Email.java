package ee.carlrobert.codegpt.user.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {

  private final String email;
  private final String emailId;
  private final boolean verified;

  public Email(
      @JsonProperty("email") String email,
      @JsonProperty("email_id") String emailId,
      @JsonProperty("verified") boolean verified) {
    this.email = email;
    this.emailId = emailId;
    this.verified = verified;
  }

  public String getEmail() {
    return email;
  }

  public String getEmailId() {
    return emailId;
  }

  public boolean isVerified() {
    return verified;
  }
}
