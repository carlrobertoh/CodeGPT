package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.vfs.readText

enum class InfillPromptTemplate(val label: String, val stopTokens: List<String>?) {

    OPENAI("OpenAI", null) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            return "<|fim_prefix|> ${infillDetails.prefix} <|fim_suffix|>${infillDetails.suffix} <|fim_middle|>"
        }
    },
    CODE_LLAMA("Code Llama", listOf("<EOT>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            return "<PRE> ${infillDetails.prefix} <SUF>${infillDetails.suffix} <MID>"
        }
    },
    CODE_GEMMA(
        "CodeGemma Instruct",
        listOf("<|file_separator|>", "<|fim_prefix|>", "<|fim_suffix|>", "<|fim_middle|>", "<eos>")
    ) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://huggingface.co/google/codegemma-7b#for-code-completion
            val infillPrompt =
                "<|fim_prefix|>${infillDetails.prefix}<|fim_suffix|>${infillDetails.suffix}<|fim_middle|>"
            return if (infillDetails.context == null || infillDetails.context.contextFiles.isNullOrEmpty()) {
                infillPrompt
            } else {
                infillDetails.context.contextFiles.map {
                    "<|file_separator|>${it.path} \n" +
                            it.readText()
                }.joinToString { it + "\n" } +
                        "<|file_separator|>${infillDetails.context.file.path} \n" +
                        infillPrompt
            }
        }
    },
    CODE_QWEN("CodeQwen1.5", listOf("<|endoftext|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://github.com/QwenLM/CodeQwen1.5?tab=readme-ov-file#2-file-level-code-completion-fill-in-the-middle
            val infillPrompt =
                "<fim_prefix>${infillDetails.prefix}<fim_suffix>${infillDetails.suffix}<fim_middle>"
            return if (infillDetails.context == null || infillDetails.context.contextFiles.isNullOrEmpty()) {
                infillPrompt
            } else {
                "<reponame>${infillDetails.context.repoName}\n" +
                        infillDetails.context.contextFiles.map {
                            "<file_sep>${it.path} \n" +
                                    it.readText()
                        }.joinToString { it + "\n" } +
                        "<file_sep>${infillDetails.context.file.path} \n" +
                        infillPrompt
            }
        }
    },
    STABILITY("Stability AI", listOf("<|endoftext|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            return "<fim_prefix>${infillDetails.prefix}<fim_suffix>${infillDetails.suffix}<fim_middle>"
        }
    },
    DEEPSEEK_CODER("DeepSeek Coder", listOf("<|EOT|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://github.com/deepseek-ai/DeepSeek-Coder?tab=readme-ov-file#2-code-insertion
            val infillPrompt =
                "<｜fim▁begin｜>${infillDetails.prefix}<｜fim▁hole｜>${infillDetails.suffix}<｜fim▁end｜>"
            return if (infillDetails.context == null || infillDetails.context.contextFiles.isNullOrEmpty()) {
                infillPrompt
            } else {
                infillDetails.context.contextFiles.map { "#${it.path}\n" + it.readText() }
                    .joinToString { it + "\n" } +
                        "#${infillDetails.context.file.path}\n" +
                        infillPrompt
            }
        }
    },
    STAR_CODER("StarCoder2", listOf("<|endoftext|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://huggingface.co/spaces/bigcode/bigcode-playground/blob/main/app.py
            val infillPrompt =
                "<fim_prefix>${infillDetails.prefix} <fim_suffix> ${infillDetails.suffix}<fim_middle>"
            return if (infillDetails.context == null || infillDetails.context.contextFiles.isNullOrEmpty()) {
                infillPrompt
            } else {
                "<reponame>${infillDetails.context.repoName}" +
                        infillDetails.context.contextFiles.map {
                            "<filename>${it.path}\n" +
                                    it.readText() + "<|endoftext|>"
                        }.joinToString { it + "\n" } +
                        "<filename>${infillDetails.context.file.path} \n" +
                        infillPrompt
            }
        }
    },
    CODESTRAL("Codestral", listOf("</s>")) {
        override fun buildPrompt(prefix: String, suffix: String): String {
            return "[SUFFIX]$suffix[PREFIX] $prefix"
        }
    },
    ;

    abstract fun buildPrompt(infillDetails: InfillRequestDetails): String

    override fun toString(): String {
        return label
    }
}
