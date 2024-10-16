package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.conversations.Conversation
import ee.carlrobert.codegpt.conversations.message.Message
import java.util.*

interface CompletionParameters

class ChatCompletionParameters private constructor(
    val conversation: Conversation,
    val conversationType: ConversationType,
    val message: Message,
    var sessionId: UUID?,
    var highlightedText: String?,
    var retry: Boolean,
    var imageMediaType: String?,
    var imageData: ByteArray?,
    var referencedFiles: List<ReferencedFile>?
) : CompletionParameters {

    fun toBuilder(): Builder {
        return Builder(conversation, message).apply {
            sessionId(this@ChatCompletionParameters.sessionId)
            conversationType(this@ChatCompletionParameters.conversationType)
            highlightedText(this@ChatCompletionParameters.highlightedText)
            retry(this@ChatCompletionParameters.retry)
            imageMediaType(this@ChatCompletionParameters.imageMediaType)
            imageData(this@ChatCompletionParameters.imageData)
            referencedFiles(this@ChatCompletionParameters.referencedFiles)
        }
    }

    class Builder(private val conversation: Conversation, private val message: Message) {
        private var sessionId: UUID? = null
        private var conversationType: ConversationType = ConversationType.DEFAULT
        private var highlightedText: String? = null
        private var retry: Boolean = false
        private var imageMediaType: String? = null
        private var imageData: ByteArray? = null
        private var referencedFiles: List<ReferencedFile>? = null

        fun sessionId(sessionId: UUID?) = apply { this.sessionId = sessionId }
        fun conversationType(conversationType: ConversationType) =
            apply { this.conversationType = conversationType }

        fun highlightedText(highlightedText: String?) =
            apply { this.highlightedText = highlightedText }

        fun retry(retry: Boolean) = apply { this.retry = retry }
        fun imageMediaType(imageMediaType: String?) = apply { this.imageMediaType = imageMediaType }
        fun imageData(imageData: ByteArray?) = apply { this.imageData = imageData }
        fun referencedFiles(referencedFiles: List<ReferencedFile>?) =
            apply { this.referencedFiles = referencedFiles }

        fun build(): ChatCompletionParameters {
            return ChatCompletionParameters(
                conversation,
                conversationType,
                message,
                sessionId,
                highlightedText,
                retry,
                imageMediaType,
                imageData,
                referencedFiles
            )
        }
    }

    companion object {
        @JvmStatic
        fun builder(conversation: Conversation, message: Message) = Builder(conversation, message)
    }
}

data class CommitMessageCompletionParameters(
    val gitDiff: String,
    val systemPrompt: String
) : CompletionParameters

data class LookupCompletionParameters(val prompt: String) : CompletionParameters

data class EditCodeCompletionParameters(
    val prompt: String,
    val selectedText: String
) : CompletionParameters