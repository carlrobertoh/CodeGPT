package ee.carlrobert.codegpt.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import ee.carlrobert.codegpt.credentials.Credential.Companion.getPassword
import ee.carlrobert.codegpt.credentials.Credential.Companion.sanitize

object CredentialsStore {

    private val credentialsMap = mutableMapOf<CredentialKey, String?>()

    fun loadAll() = CredentialKey.entries.forEach { setCredential(it, getPassword(it), false) }

    fun getCredential(key: CredentialKey): String? = credentialsMap[key]

    fun setCredential(key: CredentialKey, password: String?) = setCredential(key, password, true)

    fun isCredentialSet(key: CredentialKey): Boolean = !getCredential(key).isNullOrBlank()

    private fun setCredential(key: CredentialKey, password: String?, persist: Boolean) {
        credentialsMap[key] = sanitize(password)
        if (persist) {
            PasswordSafe.instance.setPassword(credentialAttributes(key), sanitize(password))
        }
    }

    fun credentialAttributes(key: CredentialKey) =
        CredentialAttributes(generateServiceName("CodeGPT", key.name))

    enum class CredentialKey {
        OPENAI_API_KEY,
        CUSTOM_SERVICE_API_KEY,
        ANTHROPIC_API_KEY,
        AZURE_OPENAI_API_KEY,
        AZURE_ACTIVE_DIRECTORY_TOKEN,
        YOU_ACCOUNT_PASSWORD,
        LLAMA_API_KEY
    }
}
