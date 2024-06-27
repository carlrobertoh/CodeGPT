package ee.carlrobert.codegpt.util

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.diff.tools.fragmented.UnifiedDiffChange
import com.intellij.diff.util.DiffDrawUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.LightVirtualFile
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object EditorUtil {
    @JvmStatic
    fun createEditor(project: Project, fileExtension: String, code: String): Editor {
        val timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
        val fileName = "temp_$timestamp$fileExtension"
        val lightVirtualFile = LightVirtualFile(
            String.format("%s/%s", PathManager.getTempPath(), fileName),
            code
        )
        val existingDocument = FileDocumentManager.getInstance().getDocument(lightVirtualFile)
        val document = existingDocument ?: EditorFactory.getInstance().createDocument(code)

        disableHighlighting(project, document)

        return EditorFactory.getInstance().createEditor(
            document,
            project,
            lightVirtualFile,
            true,
            EditorKind.MAIN_EDITOR
        )
    }

    @JvmStatic
    fun updateEditorDocument(editor: Editor, content: String) {
        val document = editor.document
        val application = ApplicationManager.getApplication()
        val updateDocumentRunnable = Runnable {
            application.runWriteAction {
                WriteCommandAction.runWriteCommandAction(editor.project) {
                    document.replaceString(0, document.textLength, content)
                    editor.component.repaint()
                    editor.component.revalidate()
                }
            }
        }

        if (application.isUnitTestMode) {
            application.invokeAndWait(updateDocumentRunnable)
        } else {
            application.invokeLater(updateDocumentRunnable)
        }
    }

    @JvmStatic
    fun hasSelection(editor: Editor?): Boolean {
        return editor?.selectionModel?.hasSelection() == true
    }

    @JvmStatic
    fun getSelectedEditor(project: Project): Editor? {
        val editorManager = FileEditorManager.getInstance(project)
        return editorManager?.selectedTextEditor
    }

    @JvmStatic
    fun getSelectedEditorSelectedText(project: Project): String? {
        val selectedEditor = getSelectedEditor(project)
        return selectedEditor?.selectionModel?.selectedText
    }

    @JvmStatic
    fun isSelectedEditor(editor: Editor): Boolean {
        val project = editor.project
        if (project != null && !project.isDisposed) {
            val editorManager = FileEditorManager.getInstance(project) ?: return false
            if (editorManager is FileEditorManagerImpl) {
                return editor == editorManager.getSelectedTextEditor(true)
            }
            val current = editorManager.selectedEditor
            return (current is TextEditor) && editor == current.editor
        }
        return false
    }

    @JvmStatic
    fun isMainEditorTextSelected(project: Project): Boolean {
        return hasSelection(getSelectedEditor(project))
    }

    @JvmStatic
    fun replaceMainEditorSelection(project: Project, text: String) {
        replaceEditorSelection(getSelectedEditor(project)!!, text)
    }

    fun replaceEditorSelection(editor: Editor, text: String) {
        val selectionModel = editor.selectionModel
        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

        replaceTextAndReformat(editor, startOffset, endOffset, text).also {
            editor.contentComponent.requestFocus()
            selectionModel.removeSelection()
        }
    }

    private fun replaceTextAndReformat(
        editor: Editor,
        startOffset: Int,
        endOffset: Int,
        newText: String
    ) {
        return runUndoTransparentWriteAction {
            val document = editor.document
            val originalText = document.getText(TextRange(startOffset, endOffset))

            document.replaceString(startOffset, endOffset, newText)

            if (ConfigurationSettings.getCurrentState().isAutoFormattingEnabled) {
                reformatDocument(
                    editor.project!!,
                    document,
                    originalText,
                    startOffset,
                    endOffset
                )
            }
        }
    }

    @JvmStatic
    fun reformatDocument(
        project: Project,
        document: Document,
        originalText: String,
        startOffset: Int,
        endOffset: Int
    ): TextRange? {
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        psiDocumentManager.commitDocument(document)
        val psiFile = psiDocumentManager.getPsiFile(document)
        return psiFile?.let { file ->
            CodeStyleManager.getInstance(project).adjustLineIndent(file, TextRange(startOffset, endOffset))

            val documentText = document.text
            val newEndOffset = (startOffset until documentText.length)
                .asSequence()
                .filter { !documentText[it].isWhitespace() }
                .take(originalText.count { !it.isWhitespace() })
                .lastOrNull()
                ?.plus(1)
                ?: startOffset
            return TextRange(startOffset, newEndOffset)
        }
    }

    @JvmStatic
    fun disableHighlighting(project: Project, document: Document) {
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
        psiFile?.let {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false)
        }
    }

    fun highlightDifferences(
        editor: Editor,
        selectedText: String,
        modifiedContent: String
    ): List<RangeHighlighter> =
        DiffUtil.calculateDifferences(editor, selectedText, modifiedContent)
            .map {
                val blockStart = it.startLine1
                val insertedStart = it.startLine2
                val insertedEnd = it.endLine2

                UnifiedDiffChange(blockStart, insertedStart, insertedEnd, it)
                    .run {
                        DiffDrawUtil.createUnifiedChunkHighlighters(
                            editor,
                            deletedRange,
                            insertedRange,
                            isExcluded,
                            isSkipped,
                            lineFragment.innerFragments
                        )
                    }
            }
            .flatten()
}
