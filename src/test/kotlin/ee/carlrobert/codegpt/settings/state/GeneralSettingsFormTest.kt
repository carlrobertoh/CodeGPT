package ee.carlrobert.codegpt.settings.state

import com.intellij.openapi.Disposable
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import ee.carlrobert.codegpt.credentials.Credential.Companion.getCredential
import ee.carlrobert.codegpt.credentials.Credential.Companion.getPassword
import ee.carlrobert.codegpt.credentials.Credential.Companion.sanitize
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.ANTHROPIC_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.LLAMA_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.YOU_ACCOUNT_PASSWORD
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettingsConfigurable
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettingsForm
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettingsState
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsState
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceForm
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettingsState
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsState
import ee.carlrobert.codegpt.settings.service.you.YouSettings
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm
import ee.carlrobert.codegpt.settings.service.you.YouSettingsState
import org.assertj.core.api.Assertions.assertThat
import java.util.stream.Stream

class GeneralSettingsFormTest : BasePlatformTestCase() {

  private val generalSettingsConfigurable = GeneralSettingsConfigurable()

  fun testAnthropicSettingsApiKey() {
    Stream.of("", "  ", " abc ", null).forEach { anthropicSettingsApiKey(it) }
  }

  fun testAzureSettingsApiKey() {
    Stream.of("", "  ", " abc ", null).forEach { azureSettingsApiKey(it) }
  }

  fun testCustomSettingsApiKey() {
    Stream.of("", "  ", " abc ", null).forEach { customSettingsApiKey(it) }
  }

  fun testLlamaSettingsApiKey() {
    Stream.of("", "  ", " abc ", null).forEach { llamaSettingsApiKey(it) }
  }

  fun testOpenAISettingsApiKey() {
    Stream.of("", "  ", " abc ", null).forEach { openAISettingsApiKey(it) }
  }

  fun testYouSettingsPassword() {
    Stream.of("", "  ", " abc ", null).forEach { youSettingsApiPassword(it) }
  }

  private fun anthropicSettingsApiKey(apiKey: String?) {
    assertThat(getCredential(ANTHROPIC_API_KEY)).isNotEqualTo(apiKey)

    // new form reads from store
    setCredential(ANTHROPIC_API_KEY, apiKey)
    val expected = sanitize(apiKey)
    assertThat(getPassword(ANTHROPIC_API_KEY)).isEqualTo(expected)
    val form = AnthropicSettingsForm(AnthropicSettingsState())
    assertThat(form.apiKey).isEqualTo(expected)

    // reset form from store
    setCredential(ANTHROPIC_API_KEY, "123")
    assertThat(getPassword(ANTHROPIC_API_KEY)).isEqualTo("123")
    val newState = AnthropicSettingsState()
    AnthropicSettings.getInstance().loadState(newState)
    assertThat(AnthropicSettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.apiKey).isEqualTo(expected)
    form.resetForm()
    assertThat(form.apiKey).isEqualTo("123")

    // change text in form field only, not store
    form.apiKeyField.text = "456"
    assertThat(form.apiKey).isEqualTo("456")
    assertThat(getPassword(ANTHROPIC_API_KEY)).isEqualTo("123")

    // state is equal, but form is modified (password changed)
    assertThat(AnthropicSettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(AnthropicSettings.getInstance().isModified(form)).isTrue()

    // apply form stores changed password
    generalSettingsConfigurable.applyAnthropicSettings(form)
    assertThat(getPassword(ANTHROPIC_API_KEY)).isEqualTo("456")
  }

  private fun azureSettingsApiKey(apiKey: String?) {
    val activeDirectoryToken = apiKey
    assertThat(getCredential(AZURE_OPENAI_API_KEY)).isNotEqualTo(apiKey)
    assertThat(getCredential(AZURE_ACTIVE_DIRECTORY_TOKEN)).isNotEqualTo(activeDirectoryToken)

    // new form reads from store
    setCredential(AZURE_OPENAI_API_KEY, apiKey)
    setCredential(AZURE_ACTIVE_DIRECTORY_TOKEN, activeDirectoryToken)
    val expected = sanitize(apiKey)
    val expectedToken = sanitize(activeDirectoryToken)
    assertThat(getPassword(AZURE_OPENAI_API_KEY)).isEqualTo(expected)
    assertThat(getPassword(AZURE_ACTIVE_DIRECTORY_TOKEN)).isEqualTo(expectedToken)
    val form = AzureSettingsForm(AzureSettingsState())
    assertThat(form.apiKey).isEqualTo(expected)
    assertThat(form.activeDirectoryToken).isEqualTo(expectedToken)

    // reset form from store
    setCredential(AZURE_OPENAI_API_KEY, "123")
    setCredential(AZURE_ACTIVE_DIRECTORY_TOKEN, "123b")
    assertThat(getPassword(AZURE_OPENAI_API_KEY)).isEqualTo("123")
    assertThat(getPassword(AZURE_ACTIVE_DIRECTORY_TOKEN)).isEqualTo("123b")
    val newState = AzureSettingsState()
    AzureSettings.getInstance().loadState(newState)
    assertThat(AzureSettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.apiKey).isEqualTo(expected)
    assertThat(form.activeDirectoryToken).isEqualTo(expectedToken)
    form.resetForm()
    assertThat(form.apiKey).isEqualTo("123")
    assertThat(form.activeDirectoryToken).isEqualTo("123b")

    // change text in form field only, not store
    form.apiKeyField.text = "456"
    form.activeDirectoryTokenField.text = "456b"
    assertThat(form.apiKey).isEqualTo("456")
    assertThat(form.activeDirectoryToken).isEqualTo("456b")
    assertThat(getPassword(AZURE_OPENAI_API_KEY)).isEqualTo("123")
    assertThat(getPassword(AZURE_ACTIVE_DIRECTORY_TOKEN)).isEqualTo("123b")

    // state is equal, but form is modified (passwords changed)
    assertThat(AzureSettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(AzureSettings.getInstance().isModified(form)).isTrue()

    // apply form stores changed passwords
    generalSettingsConfigurable.applyAzureSettings(form)
    assertThat(getPassword(AZURE_OPENAI_API_KEY)).isEqualTo("456")
    assertThat(getPassword(AZURE_ACTIVE_DIRECTORY_TOKEN)).isEqualTo("456b")
  }

  private fun customSettingsApiKey(apiKey: String?) {
    assertThat(getCredential(CUSTOM_SERVICE_API_KEY)).isNotEqualTo(apiKey)

    // new form reads from store
    setCredential(CUSTOM_SERVICE_API_KEY, apiKey)
    val expected = sanitize(apiKey)
    assertThat(getPassword(CUSTOM_SERVICE_API_KEY)).isEqualTo(expected)
    val form = CustomServiceForm(CustomServiceSettingsState())
    assertThat(form.apiKey).isEqualTo(expected)

    // reset form from store
    setCredential(CUSTOM_SERVICE_API_KEY, "123")
    assertThat(getPassword(CUSTOM_SERVICE_API_KEY)).isEqualTo("123")
    val newState = CustomServiceSettingsState()
    CustomServiceSettings.getInstance().loadState(newState)
    assertThat(CustomServiceSettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.apiKey).isEqualTo(expected)
    form.resetForm()
    assertThat(form.apiKey).isEqualTo("123")

    // change text in form field only, not store
    form.apiKeyField.text = "456"
    assertThat(form.apiKey).isEqualTo("456")
    assertThat(getPassword(CUSTOM_SERVICE_API_KEY)).isEqualTo("123")

    // state is equal, but form is modified (password changed)
    assertThat(CustomServiceSettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(CustomServiceSettings.getInstance().isModified(form)).isTrue()

    // apply form stores changed password
    generalSettingsConfigurable.applyCustomOpenAISettings(form)
    assertThat(getPassword(CUSTOM_SERVICE_API_KEY)).isEqualTo("456")
  }

  private fun llamaSettingsApiKey(apiKey: String?) {
    assertThat(getCredential(LLAMA_API_KEY)).isNotEqualTo(apiKey)

    // new form reads from store
    setCredential(LLAMA_API_KEY, apiKey)
    val expected = sanitize(apiKey)
    assertThat(getPassword(LLAMA_API_KEY)).isEqualTo(expected)
    val form = LlamaSettingsForm(LlamaSettingsState())
    assertThat(form.llamaServerPreferencesForm.apiKey).isEqualTo(expected)

    // reset form from store
    setCredential(LLAMA_API_KEY, "123")
    assertThat(getPassword(LLAMA_API_KEY)).isEqualTo("123")
    val newState = LlamaSettingsState()
    LlamaSettings.getInstance().loadState(newState)
    assertThat(LlamaSettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.llamaServerPreferencesForm.apiKey).isEqualTo(expected)
    form.llamaServerPreferencesForm.resetForm(newState)
    assertThat(form.llamaServerPreferencesForm.apiKey).isEqualTo("123")

    // change text in form field only, not store
    form.llamaServerPreferencesForm.apiKeyField.text = "456"
    assertThat(form.llamaServerPreferencesForm.apiKey).isEqualTo("456")
    assertThat(getPassword(LLAMA_API_KEY)).isEqualTo("123")

    // state is equal, but form is modified (password changed)
    assertThat(LlamaSettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(LlamaSettings.getInstance().isModified(form)).isTrue()

    // apply form stores changed password
    generalSettingsConfigurable.applyLlamaSettings(form)
    assertThat(getPassword(LLAMA_API_KEY)).isEqualTo("456")
  }

  private fun openAISettingsApiKey(apiKey: String?) {
    assertThat(getCredential(OPENAI_API_KEY)).isNotEqualTo(apiKey)

    // new form reads from store
    setCredential(OPENAI_API_KEY, apiKey)
    val expected = sanitize(apiKey)
    assertThat(getPassword(OPENAI_API_KEY)).isEqualTo(expected)
    val form = OpenAISettingsForm(OpenAISettingsState())
    assertThat(form.apiKey).isEqualTo(expected)

    // reset form from store
    setCredential(OPENAI_API_KEY, "123")
    assertThat(getPassword(OPENAI_API_KEY)).isEqualTo("123")
    val newState = OpenAISettingsState()
    OpenAISettings.getInstance().loadState(newState)
    assertThat(OpenAISettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.apiKey).isEqualTo(expected)
    form.resetForm()
    assertThat(form.apiKey).isEqualTo("123")

    // change text in form field only, not store
    form.apiKeyField.text = "456"
    assertThat(form.apiKey).isEqualTo("456")
    assertThat(getPassword(OPENAI_API_KEY)).isEqualTo("123")

    // state is equal, but form is modified (password changed)
    assertThat(OpenAISettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(OpenAISettings.getInstance().isModified(form)).isTrue()

    // apply form stores changed password
    generalSettingsConfigurable.applyOpenAISettings(form)
    assertThat(getPassword(OPENAI_API_KEY)).isEqualTo("456")
  }

  private fun youSettingsApiPassword(password: String?) {
    assertThat(getCredential(YOU_ACCOUNT_PASSWORD)).isNotEqualTo(password)

    // new form reads from store if email is not blank
    setCredential(YOU_ACCOUNT_PASSWORD, password)
    val state = YouSettingsState()
    state.email = "you@mail.ai"
    val expected = sanitize(password)
    assertThat(getPassword(YOU_ACCOUNT_PASSWORD)).isEqualTo(expected)
    val form = YouSettingsForm(state, Disposable { })
    assertThat(form.password).isEqualTo(expected)

    // reset form from store
    setCredential(YOU_ACCOUNT_PASSWORD, "123")
    assertThat(getPassword(YOU_ACCOUNT_PASSWORD)).isEqualTo("123")
    val newState = YouSettingsState()
    newState.email = "you@mail.ai"
    YouSettings.getInstance().loadState(newState)
    assertThat(YouSettings.getCurrentState()).isEqualTo(newState)
    assertThat(form.password).isEqualTo(expected)
    form.resetForm()
    assertThat(form.password).isEqualTo("123")

    // change text in form field only, not store
    form.passwordField.text = "456"
    assertThat(form.password).isEqualTo("456")
    assertThat(getPassword(YOU_ACCOUNT_PASSWORD)).isEqualTo("123")

    // blank email resets form to null (not from store)
    newState.email = "   "
    form.resetForm()
    assertThat(form.password).isNull()
    assertThat(getPassword(YOU_ACCOUNT_PASSWORD)).isEqualTo("123")

    // Attention: password changed, but form is not modified!
    assertThat(YouSettings.getCurrentState()).isEqualTo(form.currentState)
    assertThat(YouSettings.getInstance().isModified(form)).isFalse()

    // Attention: apply form DOES NOT store changed password!
    generalSettingsConfigurable.applyYouSettings(form)
    assertThat(getPassword(YOU_ACCOUNT_PASSWORD)).isEqualTo("123")
  }
}
