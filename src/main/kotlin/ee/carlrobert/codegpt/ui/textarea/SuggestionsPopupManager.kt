package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.vcsUtil.showAbove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.DefaultListModel
import javax.swing.Icon
import javax.swing.JComponent

enum class DefaultAction(val displayName: String, val icon: Icon) {
    ATTACH_IMAGE("Attach image", AllIcons.FileTypes.Image),
    SEARCH_WEB("Search web", AllIcons.General.Web),
}

sealed class SuggestionItem {
    data class FileItem(val file: File) : SuggestionItem()
    data class ActionItem(val action: DefaultAction) : SuggestionItem()
}

class SuggestionsPopupManager(
    private val project: Project,
    private val onSelected: (filePath: String) -> Unit
) {

    private var popup: JBPopup? = null
    private val listModel = DefaultListModel<SuggestionItem>()
    private val list = SuggestionList(listModel) {
        if (it is SuggestionItem.FileItem) {
            onSelected(it.file.path)
        } else if (it is SuggestionItem.ActionItem) {
            when (it.action) {
                DefaultAction.ATTACH_IMAGE -> {} // todo
                DefaultAction.SEARCH_WEB -> {} // todo
            }
        }
    }

    fun showPopup(component: JComponent) {
        popup = createPopup(component)
        popup?.showAbove(component)

        val projectFileIndex = project.service<ProjectFileIndex>()
        CoroutineScope(Dispatchers.Default).launch {
            val openFilePaths = project.service<FileEditorManager>().openFiles
                .filter { readAction { projectFileIndex.isInContent(it) } }
                .take(6)
                .map { it.path }
            updateSuggestions(openFilePaths)
        }
    }

    fun hidePopup() {
        popup?.cancel()
    }

    fun isPopupVisible(): Boolean {
        return popup?.isVisible ?: false
    }

    fun updateSuggestions(filePaths: List<String>) {
        listModel.clear()
        listModel.addAll(filePaths.map { SuggestionItem.FileItem(File(it)) })
    }

    fun requestFocus() {
        list.requestFocus()
    }

    fun selectNext() {
        list.selectNext()
    }

    private fun createPopup(preferableFocusComponent: JComponent? = null): JBPopup =
        service<JBPopupFactory>()
            .createComponentPopupBuilder(list, preferableFocusComponent)
            .setMovable(true)
            .setCancelOnClickOutside(true)
            .setCancelOnWindowDeactivation(false)
            .setRequestFocus(true)
            .setCancelCallback {
                listModel.removeAllElements()
                true
            }
            .createPopup()
}