package ee.carlrobert.codegpt.predictions

import com.intellij.codeInsight.lookup.LookupEvent
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION
import ee.carlrobert.codegpt.CodeGPTKeys.PENDING_PREDICTION_CALL
import ee.carlrobert.codegpt.codecompletions.CodeCompletionProgressNotifier
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.settings.prompts.PromptsSettings
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.codegpt.util.EditorUtil
import ee.carlrobert.codegpt.util.GitUtil
import ee.carlrobert.llm.client.codegpt.request.prediction.AutocompletionPredictionRequest
import ee.carlrobert.llm.client.codegpt.request.prediction.DirectPredictionRequest
import ee.carlrobert.llm.client.codegpt.request.prediction.PredictionRequest
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException
import ee.carlrobert.llm.client.codegpt.response.PredictionResponse
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage
import okhttp3.Request

@Service
class PredictionService {

    fun displayDirectPrediction(editor: Editor) {
        val request = CompletionClientProvider.getCodeGPTClient()
            .buildDirectPredictionRequest(createDirectPredictionRequest(editor))
        displayInlineDiff(editor, request)
    }

    fun displayAutocompletePrediction(editor: Editor, textToInsert: String, beforeApply: String) {
        val request = CompletionClientProvider.getCodeGPTClient()
            .buildAutocompletionPredictionRequest(
                createAutocompletePredictionRequest(editor, textToInsert, beforeApply)
            )
        displayInlineDiff(editor, request)
    }

    fun displayLookupPrediction(editor: Editor, event: LookupEvent, beforeApply: String, cursorOffset: Int) {
        val request = CompletionClientProvider.getCodeGPTClient()
            .buildLookupPredictionRequest(
                createAutocompletePredictionRequest(
                    editor,
                    event.item?.lookupString ?: "",
                    beforeApply,
                    cursorOffset
                )
            )
        displayInlineDiff(editor, request)
    }

    private fun displayInlineDiff(editor: Editor, request: Request) {
        val prediction = getPrediction(editor, request)
        if (prediction != null && !prediction.nextRevision.isNullOrEmpty()) {
            runInEdt {
                CodeSuggestionDiffViewer.displayInlineDiff(editor, prediction.nextRevision)
            }
        }
    }

    private fun getPrediction(editor: Editor, request: Request): PredictionResponse? {
        editor.project?.let {
            CodeCompletionProgressNotifier.startLoading(it)
        }

        val pendingCall = PENDING_PREDICTION_CALL.get(editor)
        if (pendingCall != null) {
            pendingCall.cancel()
            return null
        }

        try {
            val client = CompletionClientProvider.getCodeGPTClient()
            val call = client.createNewCall(request)
            PENDING_PREDICTION_CALL.set(editor, call)
            return client.getPrediction(call)
        } catch (e: CodeGPTException) {
            OverlayUtil.showNotification(e.detail, NotificationType.ERROR)
            service<CodeGPTServiceSettings>().state.codeAssistantEnabled = false
            return null
        } catch (e: Exception) {
            if (e.cause?.message != "Canceled") {
                throw e
            }
            return null
        } finally {
            IS_FETCHING_COMPLETION.set(editor, false)
            PENDING_PREDICTION_CALL.set(editor, null)
            editor.project?.let {
                CodeCompletionProgressNotifier.stopLoading(it)
            }
        }
    }

    private fun createAutocompletePredictionRequest(
        editor: Editor,
        textToInsert: String,
        beforeApply: String,
        cursorOffset: Int? = null,
    ): AutocompletionPredictionRequest {
        val predictionRequest = AutocompletionPredictionRequest()
        predictionRequest.appliedCompletion = textToInsert
        predictionRequest.previousRevision = beforeApply
        setDefaultParams(editor, predictionRequest, cursorOffset)
        return predictionRequest
    }

    private fun createDirectPredictionRequest(editor: Editor): DirectPredictionRequest {
        val predictionRequest = DirectPredictionRequest()
        setDefaultParams(editor, predictionRequest)
        return predictionRequest
    }

    private fun setDefaultParams(editor: Editor, request: PredictionRequest, offset: Int? = null) {
        val messages: MutableList<OpenAIChatCompletionStandardMessage> = mutableListOf()
        ConversationsState.getInstance().currentConversation?.messages?.forEach {
            messages.add(OpenAIChatCompletionStandardMessage("user", it.prompt))
            messages.add(OpenAIChatCompletionStandardMessage("assistant", it.response))
        }
        request.apply {
            currentRevision = runReadAction { editor.document.text }
            customPrompt =
                service<PromptsSettings>().state.coreActions.codeAssistant.instructions
            cursorOffset = offset ?: runReadAction { editor.caretModel.offset }
            gitChanges = GitUtil.getCurrentChanges(editor.project!!)
            openFiles = EditorUtil.getOpenFiles(editor.project!!)
            conversationMessages = messages.toList()
        }
    }
}