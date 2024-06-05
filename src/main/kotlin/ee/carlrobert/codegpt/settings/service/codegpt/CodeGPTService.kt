package ee.carlrobert.codegpt.settings.service.codegpt

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTKeys.CODEGPT_USER_DETAILS
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CODEGPT_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTUserDetailsNotifier.Companion.CODEGPT_USER_DETAILS_TOPIC
import kotlinx.coroutines.*

@Service
class CodeGPTService private constructor(val project: Project) {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun syncUserDetailsAsync() {
        syncUserDetailsAsync(getCredential(CODEGPT_API_KEY))
    }

    fun syncUserDetailsAsync(apiKey: String?) {
        serviceScope.launch {
            val userDetails = withContext(Dispatchers.IO) {
                if (apiKey.isNullOrEmpty()) null
                else CompletionClientProvider.getCodeGPTClient().getUserDetails(apiKey)
            }
            if (userDetails != null && userDetails.pricingPlan != null) {
                CODEGPT_USER_DETAILS.set(project, userDetails)
                if (!userDetails.fullName.isNullOrEmpty()) {
                    service<GeneralSettings>().state.displayName = userDetails.fullName
                }
            }
            project.messageBus
                .syncPublisher<CodeGPTUserDetailsNotifier>(CODEGPT_USER_DETAILS_TOPIC)
                .userDetailsObtained(userDetails)
        }
    }
}