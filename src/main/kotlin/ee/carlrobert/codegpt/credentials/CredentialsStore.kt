package ee.carlrobert.codegpt.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread

object CredentialsStore {

    private val credentialsMap = mutableMapOf<CredentialKey, String?>()

    @JvmStatic
    @RequiresBackgroundThread
    fun getCredential(key: CredentialKey): String? =
        credentialsMap.getOrPut(key) {
            PasswordSafe.instance.getPassword(
                CredentialAttributes(
                    generateServiceName("CodeGPT", key.name)
                )
            ) ?: ""
        }.takeIf { !it.isNullOrEmpty() }

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
        LLAMA_API_KEY,
        GOOGLE_API_KEY,
        OLLAMA_API_KEY,
    }
}