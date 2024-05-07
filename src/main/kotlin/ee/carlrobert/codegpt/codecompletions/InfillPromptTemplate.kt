package ee.carlrobert.codegpt.codecompletions

enum class InfillPromptTemplate(val label: String, val stopTokens: List<String>?) {

    OPENAI("OpenAI", null) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<|fim_prefix|> $prefix <|fim_suffix|>$suffix <|fim_middle|>"
        }
    },
    CODE_LLAMA("Code Llama", listOf("<EOT>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<PRE> $prefix <SUF>$suffix <MID>"
        }
    },
    CODE_GEMMA(
        "CodeGemma Instruct",
        listOf("<|file_separator|>", "<|fim_prefix|>", "<|fim_suffix|>", "<|fim_middle|>", "<eos>")
    ) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<|fim_prefix|>$prefix<|fim_suffix|>$suffix<|fim_middle|>"
        }
    },
    STABILITY("Stability AI", listOf("<|endoftext|>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<fim_prefix>$prefix<fim_suffix>$suffix<fim_middle>"
        }
    },
    DEEPSEEK_CODER("DeepSeek Coder", listOf("<|EOT|>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<｜fim▁begin｜>$prefix<｜fim▁hole｜>$suffix<｜fim▁end｜>"
        }
    };

    abstract fun buildPrompt(prefix: String, suffix: String): String

    override fun toString(): String {
        return label
    }
}
