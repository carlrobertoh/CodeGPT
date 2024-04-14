package ee.carlrobert.codegpt.credentials

import com.intellij.ide.passwordSafe.PasswordSafe
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.credentialAttributes
import java.util.Optional

class Credential {
  companion object {
    /**
     * Sanitized credential from in-memory store (not blank, otherwise null)
     */
    @JvmStatic
    fun getCredential(key: CredentialKey): String? {
      return sanitize(CredentialsStore.getCredential(key))
    }

    /**
     * Sanitized password from persisted store (not blank, otherwise null)
     */
    @JvmStatic
    fun getPassword(key: CredentialKey): String? {
      return sanitize(PasswordSafe.instance.getPassword(credentialAttributes(key)))
    }

    /**
     * Sanitized credential (not blank, otherwise null)
     */
    @JvmStatic
    fun sanitize(credential: String?): String? {
      return Optional.ofNullable(credential)
        .filter(String::isNotBlank).orElse(null)
    }
  }
}
