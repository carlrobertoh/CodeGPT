package ee.carlrobert.codegpt.codecompletions

import ee.carlrobert.codegpt.codecompletions.psi.filePath
import ee.carlrobert.codegpt.codecompletions.psi.readText

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
            return if (infillDetails.context == null || infillDetails.context.contextElements.isEmpty()) {
                infillPrompt
            } else {
                infillDetails.context.contextElements.map {
                    "<|file_separator|>${it.filePath()} \n" +
                            it.text
                }.joinToString("") { it + "\n" } +
                        "<|file_separator|>${infillDetails.context.enclosingElement.filePath()} \n" +
                        infillPrompt
            }
        }
    },
    CODE_QWEN("CodeQwen1.5", listOf("<|endoftext|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://github.com/QwenLM/CodeQwen1.5?tab=readme-ov-file#2-file-level-code-completion-fill-in-the-middle
            val infillPrompt =
                "<fim_prefix>${infillDetails.prefix}<fim_suffix>${infillDetails.suffix}<fim_middle>"
            return if (infillDetails.context == null || infillDetails.context.contextElements.isEmpty()) {
                infillPrompt
            } else {
                "<reponame>${infillDetails.context.getRepoName()}\n" +
                        infillDetails.context.contextElements.map {
                            "<file_sep>${it.filePath()} \n" +
                                    it.readText()
                        }.joinToString("") { it + "\n" } +
                        "<file_sep>${infillDetails.context.enclosingElement.filePath()} \n" +
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
            return if (infillDetails.context == null || infillDetails.context.contextElements.isEmpty()) {
                infillPrompt
            } else {
                infillDetails.context.contextElements.map { "#${it.filePath()}\n" + it.readText() }
                    .joinToString("") { it + "\n" } +
                        "#${infillDetails.context.enclosingElement.filePath()}\n" +
                        infillPrompt
            }
        }
    },
    STAR_CODER("StarCoder2", listOf("<|endoftext|>")) {
        override fun buildPrompt(infillDetails: InfillRequestDetails): String {
            // see https://huggingface.co/spaces/bigcode/bigcode-playground/blob/main/app.py
            val infillPrompt =
                "<fim_prefix>${infillDetails.prefix} <fim_suffix> ${infillDetails.suffix}<fim_middle>"
            return if (infillDetails.context == null || infillDetails.context.contextElements.isEmpty()) {
                infillPrompt
            } else {
                "<reponame>${infillDetails.context.getRepoName()}" +
                        infillDetails.context.contextElements.map {
                            "<filename>${it.filePath()}\n" +
                                    it.readText() + "<|endoftext|>"
                        }.joinToString("") { it + "\n" } +
                        "<filename>${infillDetails.context.enclosingElement.filePath()} \n" +
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
