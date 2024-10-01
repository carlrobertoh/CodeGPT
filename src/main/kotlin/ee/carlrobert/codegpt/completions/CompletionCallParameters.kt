package ee.carlrobert.codegpt.completions

interface CompletionCallParameters

data class ChatCompletionRequestParameters(
    val callParameters: CallParameters
) : CompletionCallParameters

data class CommitMessageRequestParameters(
    val gitDiff: String,
    val systemPrompt: String
) : CompletionCallParameters

data class LookupRequestCallParameters(val prompt: String) : CompletionCallParameters

data class EditCodeRequestParameters(
    val prompt: String,
    val selectedText: String
) : CompletionCallParameters