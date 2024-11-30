package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vcs.VcsException
import com.intellij.refactoring.suggested.startOffset
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService
import ee.carlrobert.codegpt.codecompletions.psi.readText
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.util.GitUtil

object InfillRequestUtil {
    private val logger = thisLogger()

    suspend fun buildInfillRequest(
        request: InlineCompletionRequest,
        type: CompletionType
    ): InfillRequest {
        val caretOffset = readAction { request.editor.caretModel.offset }
        val infillRequestBuilder = InfillRequest.Builder(request.document, caretOffset, type)
            .fileDetails(
                InfillRequest.FileDetails(
                    request.document.text,
                    request.file.virtualFile.extension
                )
            )

        val project = request.editor.project ?: return infillRequestBuilder.build()
        val repository = GitUtil.getProjectRepository(project)
        if (repository != null) {
            try {
                val unstagedDiff = GitUtil.getUnstagedDiff(project, repository)
                if (unstagedDiff.isNotEmpty()) {
                    val openedEditorFileNames =
                        FileEditorManager.getInstance(project).openFiles.map { it.name }
                    val additionalContext = unstagedDiff
                        .filter {
                            it.fileName != request.file.virtualFile.name && it.fileName in openedEditorFileNames
                        }
                        .joinToString("\n") { "${it.fileName}\n${it.content}" }
                    infillRequestBuilder.additionalContext(additionalContext)
                }
            } catch (e: VcsException) {
                logger.error("Failed to get git context", e)
            }
        }

        if (service<ConfigurationSettings>().state.codeCompletionSettings.contextAwareEnabled) {
            getInfillContext(request, caretOffset)?.let { infillRequestBuilder.context(it) }
        }

        return infillRequestBuilder.build()
    }

    private fun getInfillContext(
        request: InlineCompletionRequest,
        caretOffset: Int
    ): InfillContext? {
        val infillContext =
            service<CompletionContextService>().findContext(request.editor, caretOffset)
                ?: return null
        val caretInEnclosingElement =
            caretOffset - infillContext.enclosingElement.psiElement.startOffset
        val entireText = infillContext.enclosingElement.psiElement.readText()
        val prefix = entireText.take(caretInEnclosingElement)
        val suffix =
            if (entireText.length < caretInEnclosingElement) "" else entireText.takeLast(
                entireText.length - caretInEnclosingElement
            )
        return truncateContext(prefix + suffix, infillContext)
    }

    private fun truncateContext(prompt: String, infillContext: InfillContext): InfillContext {
        var promptTokens = EncodingManager.getInstance().countTokens(prompt)
        val truncatedContextElements = infillContext.contextElements.takeWhile {
            promptTokens += it.tokens
            promptTokens <= MAX_PROMPT_TOKENS
        }.toSet()
        return InfillContext(infillContext.enclosingElement, truncatedContextElements)
    }
}