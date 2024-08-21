package ee.carlrobert.codegpt.util

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
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
                    document.replaceString(
                        0,
                        document.textLength,
                        StringUtil.convertLineSeparators(content)
                    )
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
    fun replaceEditorSelection(editor: Editor, text: String) {
        val selectionModel = editor.selectionModel
        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

        replaceTextAndReformat(editor, startOffset, endOffset, text).also {
            editor.contentComponent.requestFocus()
            selectionModel.removeSelection()
        }
    }

    @JvmStatic
    fun reformatDocument(
        project: Project,
        document: Document,
        startOffset: Int,
        endOffset: Int
    ) {
        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        psiDocumentManager.commitDocument(document)
        psiDocumentManager.getPsiFile(document)?.let { file ->
            CodeStyleManager.getInstance(project).reformatText(file, startOffset, endOffset)
        }
    }

    @JvmStatic
    fun disableHighlighting(project: Project, document: Document) {
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
        psiFile?.let {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false)
        }
    }

    private fun replaceTextAndReformat(
        editor: Editor,
        startOffset: Int,
        endOffset: Int,
        newText: String
    ) {
        editor.project?.let { project ->
            runUndoTransparentWriteAction {
                val document = editor.document
                document.replaceString(
                    startOffset,
                    endOffset,
                    StringUtil.convertLineSeparators(newText)
                )

                if (service<ConfigurationSettings>().state.autoFormattingEnabled) {
                    reformatDocument(
                        project,
                        document,
                        startOffset,
                        endOffset
                    )
                }
            }
        }
    }
}
