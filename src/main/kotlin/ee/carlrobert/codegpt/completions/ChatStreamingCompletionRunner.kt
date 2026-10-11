package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.util.ReasoningFrameTextAdapter
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

internal object ChatStreamingCompletionRunner : CompletionRunner {

    override fun run(request: CompletionRunnerRequest): CancellableRequest {
        val chatRequest = request as? CompletionRunnerRequest.Chat
            ?: throw IllegalArgumentException("ChatStreamingCompletionRunner can only run Chat requests")
        return executeAsync(chatRequest)
    }

    private fun executeAsync(request: CompletionRunnerRequest.Chat): CancellableRequest {
        val asyncRequest = AsyncRequestContext()
        val scope = asyncRequest.scope

        request.eventListener.onOpen()
        val job = scope.launch {
            val messageBuilder = StringBuilder()
            try {
                streamOrFallback(request, messageBuilder)

                if (asyncRequest.isCancelled()) {
                    request.eventListener.onCancelled(StringBuilder(messageBuilder))
                    return@launch
                }
                request.eventListener.onComplete(StringBuilder(messageBuilder))
            } catch (_: CancellationException) {
                request.eventListener.onCancelled(StringBuilder(messageBuilder))
            } catch (exception: Throwable) {
                logger.error(exception) { "Chat streaming completion failed" }
                request.eventListener.onError(
                    ChatError(exception.message ?: "Failed to complete request"),
                    exception
                )
            } finally {
                runCatching { request.executor.close() }
                scope.cancel()
            }
        }
        asyncRequest.attach(job)

        return asyncRequest.cancellableRequest
    }

    private suspend fun streamOrFallback(
        request: CompletionRunnerRequest.Chat,
        messageBuilder: StringBuilder
    ) {
        val frameAdapter = ReasoningFrameTextAdapter()
        var emittedAny = false

        runCatching {
            request.executor.executeStreaming(request.prompt, request.model, emptyList())
                .collect { frame ->
                    frameAdapter.consume(frame).forEach { chunk ->
                        emittedAny = true
                        emit(chunk, request, messageBuilder)
                    }
                }
        }.getOrElse { throwable ->
            logger.warn(throwable) {
                "Streaming failed for model ${request.model.id}, falling back to non-streaming"
            }
            if (!emittedAny) {
                val response = request.executor.execute(request.prompt, request.model, emptyList())
                val text = CompletionTextExtractor.extract(listOf(response))
                emit(text, request, messageBuilder)
            } else {
                throw throwable
            }
        }
    }

    private fun emit(
        text: String,
        request: CompletionRunnerRequest.Chat,
        messageBuilder: StringBuilder
    ) {
        messageBuilder.append(text)
        request.eventListener.onMessage(text)
    }
}
