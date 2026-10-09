package ee.carlrobert.codegpt.settings.service.anthropic

import ee.carlrobert.codegpt.settings.service.FeatureType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AnthropicSettingsStateTest {

    @Test
    fun featureEffortOverridesDefaultEffort() {
        val state = AnthropicSettingsState().apply {
            reasoningEffort = "medium"
            setFeatureReasoningEffort(FeatureType.AGENT, "xhigh")
        }

        assertThat(state.getEffectiveReasoningEffort(FeatureType.AGENT)).isEqualTo("xhigh")
        assertThat(state.getEffectiveReasoningEffort(FeatureType.CHAT)).isEqualTo("medium")
    }

    @Test
    fun clearingFeatureEffortFallsBackToModelDefault() {
        val state = AnthropicSettingsState().apply {
            setFeatureReasoningEffort(FeatureType.CHAT, "low")
            setFeatureReasoningEffort(FeatureType.CHAT, null)
        }

        assertThat(state.getEffectiveReasoningEffort(FeatureType.CHAT)).isNull()
        assertThat(state.featureReasoningEfforts).isEmpty()
    }
}
