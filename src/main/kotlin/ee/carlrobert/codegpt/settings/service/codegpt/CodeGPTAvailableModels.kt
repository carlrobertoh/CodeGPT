package ee.carlrobert.codegpt.settings.service.codegpt

import ee.carlrobert.codegpt.Icons
import ee.carlrobert.llm.client.codegpt.PricingPlan
import ee.carlrobert.llm.client.codegpt.PricingPlan.*
import javax.swing.Icon

object CodeGPTAvailableModels {

    val DEFAULT_CHAT_MODEL =
        CodeGPTModel("Gemini 2.0 Flash", "gemini-flash-2.0", Icons.Google, ANONYMOUS)
    val DEFAULT_CODE_MODEL = CodeGPTModel("Codestral", "codestral", Icons.Mistral, ANONYMOUS)

    @JvmStatic
    fun getToolWindowModels(pricingPlan: PricingPlan?): List<CodeGPTModel> {
        return when (pricingPlan) {
            null, ANONYMOUS -> listOf(
                CodeGPTModel("o3-mini", "o3-mini", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("GPT-4o", "gpt-4o", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("Claude 3.5 Sonnet", "claude-3.5-sonnet", Icons.Anthropic, INDIVIDUAL),
                CodeGPTModel("DeepSeek R1", "deepseek-r1", Icons.DeepSeek, INDIVIDUAL),
                CodeGPTModel("Gemini 2.0 Flash", "gemini-flash-2.0", Icons.Google, ANONYMOUS),
                CodeGPTModel("GPT-4o mini", "gpt-4o-mini", Icons.OpenAI, ANONYMOUS),
            )

            FREE -> listOf(
                CodeGPTModel("o3-mini", "o3-mini", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("GPT-4o", "gpt-4o", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("Claude 3.5 Sonnet", "claude-3.5-sonnet", Icons.Anthropic, INDIVIDUAL),
                CodeGPTModel("DeepSeek R1", "deepseek-r1", Icons.DeepSeek, INDIVIDUAL),
                CodeGPTModel("DeepSeek V3", "deepseek-v3", Icons.DeepSeek, FREE),
                CodeGPTModel("Qwen 2.5 Coder (32B)", "qwen-2.5-32b-chat", Icons.Qwen, FREE),
                CodeGPTModel("Llama 3.1 (405B)", "llama-3.1-405b", Icons.Meta, FREE),
                CodeGPTModel("Gemini 2.0 Flash", "gemini-flash-2.0", Icons.Google, ANONYMOUS),
            )

            INDIVIDUAL -> listOf(
                CodeGPTModel("o3-mini", "o3-mini", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("GPT-4o", "gpt-4o", Icons.OpenAI, INDIVIDUAL),
                CodeGPTModel("Claude 3.5 Sonnet", "claude-3.5-sonnet", Icons.Anthropic, INDIVIDUAL),
                CodeGPTModel("DeepSeek R1", "deepseek-r1", Icons.DeepSeek, INDIVIDUAL),
                CodeGPTModel("DeepSeek V3", "deepseek-v3", Icons.DeepSeek, FREE),
                CodeGPTModel("Gemini 2.0 Flash", "gemini-flash-2.0", Icons.Google, ANONYMOUS),
            )
        }
    }

    @JvmStatic
    val ALL_CHAT_MODELS: List<CodeGPTModel> = listOf(
        CodeGPTModel("o3-mini", "o3-mini", Icons.OpenAI, INDIVIDUAL),
        CodeGPTModel("GPT-4o", "gpt-4o", Icons.OpenAI, INDIVIDUAL),
        CodeGPTModel("GPT-4o mini", "gpt-4o-mini", Icons.OpenAI, ANONYMOUS),
        CodeGPTModel("Claude 3.5 Sonnet", "claude-3.5-sonnet", Icons.Anthropic, INDIVIDUAL),
        CodeGPTModel("Gemini 1.5 Pro", "gemini-pro-1.5", Icons.Google, INDIVIDUAL),
        CodeGPTModel("Gemini 2.0 Flash", "gemini-flash-2.0", Icons.Google, ANONYMOUS),
        CodeGPTModel("Qwen 2.5 Coder (32B)", "qwen-2.5-32b-chat", Icons.Qwen, FREE),
        CodeGPTModel("Llama 3.1 (405B)", "llama-3.1-405b", Icons.Meta, FREE),
        CodeGPTModel("DeepSeek R1", "deepseek-r1", Icons.DeepSeek, INDIVIDUAL),
        CodeGPTModel("DeepSeek V3", "deepseek-v3", Icons.DeepSeek, FREE),
    )

    @JvmStatic
    val ALL_CODE_MODELS: List<CodeGPTModel> = listOf(
        DEFAULT_CODE_MODEL,
        CodeGPTModel("Qwen 2.5 Coder", "qwen-2.5-32b-code", Icons.Qwen, FREE),
        CodeGPTModel("GPT-3.5 Turbo Instruct", "gpt-3.5-turbo-instruct", Icons.OpenAI, FREE),
    )

    @JvmStatic
    fun findByCode(code: String?): CodeGPTModel? {
        return ALL_CHAT_MODELS.union(ALL_CODE_MODELS).firstOrNull { it.code == code }
    }
}

data class CodeGPTModel(
    val name: String,
    val code: String,
    val icon: Icon,
    val pricingPlan: PricingPlan
)
