package ee.carlrobert.codegpt.user.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionUser {

  private final UUID id;
  private final String role;
  private final String email;

  public SessionUser(
      @JsonProperty("id") UUID id,
      @JsonProperty("role") String role,
      @JsonProperty("email") String email) {
    this.id = id;
    this.role = role;
    this.email = email;
  }

  public UUID getId() {
    return id;
  }

  public String getRole() {
    return role;
  }

  public String getEmail() {
    return email;
  }
}
