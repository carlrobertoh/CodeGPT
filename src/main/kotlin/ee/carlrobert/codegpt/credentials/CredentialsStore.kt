package ee.carlrobert.codegpt.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object CredentialsStore {

    private val credentialsMap = mutableMapOf<CredentialKey, String?>()

    fun loadAll() {
        CredentialKey.values().forEach {
            val credentialAttributes = CredentialAttributes(generateServiceName("CodeGPT", it.name))
            val password = PasswordSafe.instance.getPassword(credentialAttributes)

            // Avoid calling setCredential here since it will persist
            // the password back into the PasswordSafe unnecessarily.
            credentialsMap[it] = password
        }
    }

    fun getCredential(key: CredentialKey): String? = credentialsMap[key]

    fun setCredential(key: CredentialKey, password: String?) {
        val prevPassword = credentialsMap[key]
        credentialsMap[key] = password

        if (prevPassword != password) {
            val credentialAttributes =
                CredentialAttributes(generateServiceName("CodeGPT", key.name))
            PasswordSafe.instance.setPassword(credentialAttributes, password)
        }
    }

    fun isCredentialSet(key: CredentialKey): Boolean = !getCredential(key).isNullOrEmpty()

    enum class CredentialKey {
        CODEGPT_API_KEY,
        OPENAI_API_KEY,
        CUSTOM_SERVICE_API_KEY,
        ANTHROPIC_API_KEY,
        AZURE_OPENAI_API_KEY,
        AZURE_ACTIVE_DIRECTORY_TOKEN,
        YOU_ACCOUNT_PASSWORD,
        LLAMA_API_KEY,
        GOOGLE_API_KEY
    }
}