package ee.carlrobert.codegpt.completions

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupManagerListener
import com.intellij.codeInsight.lookup.impl.LookupImpl
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiUtilCore
import ee.carlrobert.codegpt.CodeGPTKeys.IS_PROMPT_TEXT_FIELD_DOCUMENT
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MethodNameCompletionLookupListener : LookupManagerListener {

    companion object {
        private val logger = thisLogger()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun activeLookupChanged(oldLookup: Lookup?, newLookup: Lookup?) {
        if (newLookup !is LookupImpl) {
            return
        }

        val isPromptTextFieldDocument =
            IS_PROMPT_TEXT_FIELD_DOCUMENT[newLookup.editor.document] ?: false
        val featureEnabled = service<ConfigurationSettings>().state.methodNameGenerationEnabled
        if (isPromptTextFieldDocument || !featureEnabled || !CompletionRequestService.isRequestAllowed()) {
            return
        }

        newLookup.psiElement?.context?.let { context ->
            val elementType = PsiUtilCore.getElementType(context)
            if (PSIMethodMapping.contains(elementType)) {
                val selection = runReadAction { context.text }
                scope.launch {
                    try {
                        val lookupCompletion = service<CompletionRequestService>()
                            .getLookupCompletion(LookupCompletionParameters(selection))
                        if (lookupCompletion.isNotEmpty()) {
                            addCompletionLookupValues(newLookup, lookupCompletion)
                        }
                    } catch (ex: CodeGPTException) {
                        OverlayUtil.showNotification(ex.detail, NotificationType.ERROR)
                    } catch (ex: Exception) {
                        logger.error(
                            "Something went wrong while requesting completion lookup values.",
                            ex
                        )
                    }
                }
            }
        }
    }

    private fun addCompletionLookupValues(lookup: LookupImpl, response: String) {
        val values = response.split(",".toRegex())
            .map { it.trim() }
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        for (value in values) {
            runInEdt {
                lookup.addItem(
                    LookupElementBuilder
                        .create(value)
                        .withIcon(Icons.DefaultSmall),
                    PrefixMatcher.ALWAYS_TRUE
                )
                lookup.refreshUi(true, true)
            }
        }
    }

    private enum class PSIMethodMapping(val types: List<String>) {
        GO(listOf("FILE", "METHOD_DECLARATION|FUNCTION_DECLARATION")),
        JAVA(listOf("java.FILE", "METHOD")),
        PY(listOf("FILE", "Py:FUNCTION_DECLARATION")),
        JAVASCRIPT(listOf("JS:FUNCTION_DECLARATION", "JS:TYPESCRIPT_FUNCTION")),
        CS(listOf("FILE", "DUMMY_TYPE_DECLARATION", "DUMMY_BLOCK")),
        PHP(listOf("FILE", "Class method|function|Function")),
        KOTLIN(listOf("FUN")),
        DEFAULT(listOf("FILE", "METHOD_DECLARATION"));


        companion object {
            fun contains(type: IElementType): Boolean {
                return entries.any { it.types.contains(type.toString()) }
            }
        }
    }
}
