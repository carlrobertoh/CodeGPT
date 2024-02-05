package ee.carlrobert.codegpt;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

/**
 * Dummy {@link PasswordSafe} implementation using a {@link HashMap}.
 */
public class TestPasswordSafe extends PasswordSafe {

  Map<CredentialAttributes, Credentials> credentials = new HashMap<>();

  @Override
  public boolean isMemoryOnly() {
    return true;
  }

  @Override
  public boolean isRememberPasswordByDefault() {
    return false;
  }

  @Override
  public void setRememberPasswordByDefault(boolean b) {

  }

  @NotNull
  @Override
  public Promise<Credentials> getAsync(@NotNull CredentialAttributes credentialAttributes) {
    return null;
  }

  @Override
  public boolean isPasswordStoredOnlyInMemory(@NotNull CredentialAttributes credentialAttributes,
      @NotNull Credentials credentials) {
    return false;
  }

  @Override
  public void set(@NotNull CredentialAttributes credentialAttributes,
      @Nullable Credentials credentials, boolean b) {
    this.credentials.put(credentialAttributes, credentials);
  }

  @Override
  public void set(@NotNull CredentialAttributes attributes, @Nullable Credentials credentials) {
    set(attributes, credentials, false);
  }

  @Override
  public @Nullable Credentials get(@NotNull CredentialAttributes attributes) {
    return this.credentials.get(attributes);
  }
}
