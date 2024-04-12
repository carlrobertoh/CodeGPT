package ee.carlrobert.codegpt.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object CredentialsStore {

    private val credentialsMap = mutableMapOf<CredentialKey, String?>()

    fun loadAll() {
        CredentialKey.entries.forEach {
            val credentialAttributes = CredentialAttributes(generateServiceName("CodeGPT", it.name))
            val password = PasswordSafe.instance.getPassword(credentialAttributes)
            setCredential(it, password)
        }
    }

    fun getCredential(key: CredentialKey): String? = credentialsMap[key]

    fun setCredential(key: CredentialKey, password: String?) {
        credentialsMap[key] = password
    }

    fun isCredentialSet(key: CredentialKey): Boolean = !getCredential(key).isNullOrBlank()

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
