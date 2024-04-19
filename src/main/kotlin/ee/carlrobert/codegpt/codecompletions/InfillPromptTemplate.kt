package ee.carlrobert.codegpt.codecompletions

enum class InfillPromptTemplate(val label: String, val stopTokens: List<String>?) {

    OPENAI("OpenAI", null) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<|fim_prefix|> $prefix <|fim_suffix|>$suffix <|fim_middle|>"
        }
    },
    LLAMA("Llama", listOf("<EOT>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<PRE> $prefix <SUF>$suffix <MID>"
        }
    },
    LLAMA_3("Llama 3", listOf("<|eot_id|>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "<|start_header_id|>user<|end_header_id|>\n\n$prefix\n$suffix<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n"
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
