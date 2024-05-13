package ee.carlrobert.codegpt.settings.service.codegpt

object CodeGPTAvailableModels {

    @JvmStatic
    val CHAT_MODELS: List<CodeGPTModel> = listOf(
        CodeGPTModel("Llama 3 (70B)", "meta-llama/Llama-3-70b-chat-hf"),
        CodeGPTModel("Llama 3 (8B)", "meta-llama/Llama-3-8b-chat-hf"),
        CodeGPTModel("Code Llama (70B)", "codellama/CodeLlama-70b-Instruct-hf"),
        CodeGPTModel("Mixtral (8x22B)", "mistralai/Mixtral-8x22B-Instruct-v0.1"),
        CodeGPTModel("DBRX (132B)", "databricks/dbrx-instruct"),
        CodeGPTModel("DeepSeek Coder (33B)", "deepseek-ai/deepseek-coder-33b-instruct"),
        CodeGPTModel("WizardLM-2 (8x22B)", "microsoft/WizardLM-2-8x22B")
    )

    @JvmStatic
    val CODE_MODELS: List<CodeGPTModel> = listOf(
        CodeGPTModel("StarCoder (16B)", "starcoder-16b"),
        CodeGPTModel("Code Llama (70B)", "codellama/CodeLlama-70b-hf"),
        CodeGPTModel("Code Llama Python (70B)", "codellama/CodeLlama-70b-Python-hf"),
        CodeGPTModel("WizardCoder Python (34B)", "WizardLM/WizardCoder-Python-34B-V1.0"),
        CodeGPTModel("Phind Code LLaMA v2 (34B)", "Phind/Phind-CodeLlama-34B-v2")
    )

    @JvmStatic
    fun findByCode(code: String?): CodeGPTModel? {
        return CHAT_MODELS.union(CODE_MODELS).firstOrNull { it.code == code }
    }
}

data class CodeGPTModel(val name: String, val code: String)