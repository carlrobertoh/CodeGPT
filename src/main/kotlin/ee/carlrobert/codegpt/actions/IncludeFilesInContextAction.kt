package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.ui.CheckboxTreeListener
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UI.PanelFactory
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.settings.IncludedFilesSettings
import ee.carlrobert.codegpt.settings.IncludedFilesSettingsState
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.codegpt.ui.checkbox.FileCheckboxTree
import ee.carlrobert.codegpt.ui.checkbox.VirtualFileCheckboxTree
import ee.carlrobert.codegpt.util.file.FileUtil.convertLongValue
import java.awt.Dimension
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.SwingUtilities

class IncludeFilesInContextAction @JvmOverloads constructor(
  customTitleKey: String? = "action.includeFilesInContext.title") :
  AnAction(CodeGPTBundle.get(customTitleKey!!)) {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return

    val checkboxTree = getCheckboxTree(e.dataContext) ?: throw RuntimeException("Could not obtain file tree")

    val totalTokensLabel = TotalTokensLabel(checkboxTree.referencedFiles)
    checkboxTree.addCheckboxTreeListener(object : CheckboxTreeListener {
      override fun nodeStateChanged(node: CheckedTreeNode) {
        totalTokensLabel.updateState(node)
      }
    })

    val includedFilesSettings = IncludedFilesSettings.getCurrentState()
    val promptTemplateTextArea = UIUtil.createTextArea(includedFilesSettings.promptTemplate)
    val repeatableContextTextArea = UIUtil.createTextArea(includedFilesSettings.repeatableContext)
    val show = showMultiFilePromptDialog(
      project,
      promptTemplateTextArea,
      repeatableContextTextArea,
      totalTokensLabel,
      checkboxTree)
    if (show == DialogWrapper.OK_EXIT_CODE) {
      project.putUserData(CodeGPTKeys.SELECTED_FILES, checkboxTree.referencedFiles)
      project.messageBus
        .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
        .filesIncluded(checkboxTree.referencedFiles)
      includedFilesSettings.promptTemplate = promptTemplateTextArea.text
      includedFilesSettings.repeatableContext = repeatableContextTextArea.text
    }
  }

  private fun getCheckboxTree(dataContext: DataContext): FileCheckboxTree? {
    return CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext)?.let(::VirtualFileCheckboxTree)
  }

  private class TotalTokensLabel(referencedFiles: List<ReferencedFile>) : JBLabel() {
    private var fileCount = referencedFiles.size
    private var totalTokens: Int

    init {
      totalTokens = calculateTotalTokens(referencedFiles)
      updateText()
    }

    fun updateState(checkedNode: CheckedTreeNode) {
      val fileContent = getNodeFileContent(checkedNode) ?: return
      val tokenCount = encodingManager.countTokens(fileContent)
      if (checkedNode.isChecked) {
        totalTokens += tokenCount
        fileCount++
      } else {
        totalTokens -= tokenCount
        fileCount--
      }
      SwingUtilities.invokeLater(::updateText)
    }

    private fun getNodeFileContent(checkedNode: CheckedTreeNode): String? {
      val userObject = checkedNode.userObject
      if (userObject is PsiElement) {
        return userObject.containingFile?.virtualFile?.let { getVirtualFileContent(it) }
      }
      return (userObject as? VirtualFile)?.let { getVirtualFileContent(it) }
    }

    private fun getVirtualFileContent(virtualFile: VirtualFile): String? {
      try {
        return String(Files.readAllBytes(Paths.get(virtualFile.path)))
      } catch (ex: IOException) {
        LOG.error(ex)
      }
      return null
    }

    private fun updateText() {
      text = String.format(
        "<html><strong>%d</strong> %s totaling <strong>%s</strong> tokens</html>",
        fileCount,
        if (fileCount == 1) "file" else "files",
        convertLongValue(totalTokens.toLong()))
    }

    private fun calculateTotalTokens(referencedFiles: List<ReferencedFile>): Int {
      return referencedFiles.stream().mapToInt { encodingManager.countTokens(it.fileContent) }.sum()
    }

    companion object {
      private val encodingManager
        get() = EncodingManager.getInstance()
    }
  }

  companion object {
    private val LOG = Logger.getInstance(IncludeFilesInContextAction::class.java)

    private fun showMultiFilePromptDialog(
      project: Project,
      promptTemplateTextArea: JBTextArea,
      repeatableContextTextArea: JBTextArea,
      totalTokensLabel: JBLabel,
      component: JComponent
    ): Int {
      val dialogBuilder = DialogBuilder(project)
      dialogBuilder.setTitle(CodeGPTBundle.get("action.includeFilesInContext.dialog.title"))
      dialogBuilder.setActionDescriptors()
      val fileTreeScrollPane = ScrollPaneFactory.createScrollPane(component)
      fileTreeScrollPane.preferredSize = Dimension(480, component.preferredSize.height + 48)
      dialogBuilder.setNorthPanel(
        FormBuilder.createFormBuilder()
          .addLabeledComponent(
            CodeGPTBundle.get("shared.promptTemplate"),
            PanelFactory.panel(promptTemplateTextArea).withComment(
              "<html><p>The template that will be used to create the final prompt. "
                      + "The <strong>{REPEATABLE_CONTEXT}</strong> placeholder must be included "
                      + "to correctly map the file contents.</p></html>"
            )
              .createPanel(),
            true
          )
          .addVerticalGap(4)
          .addLabeledComponent(
            CodeGPTBundle.get("action.includeFilesInContext.dialog.repeatableContext.label"),
            PanelFactory.panel(repeatableContextTextArea).withComment(
              "<html><p>The context that will be repeated for each included file. "
                      + "Acceptable placeholders include <strong>{FILE_PATH}</strong> and "
                      + "<strong>{FILE_CONTENT}</strong>.</p></html>"
            )
              .createPanel(),
            true
          )
          .addComponent(JBUI.Panels.simplePanel()
              .addToRight(getRestoreButton(promptTemplateTextArea, repeatableContextTextArea))
          )
          .addVerticalGap(16)
          .addComponent(JBLabel(CodeGPTBundle.get("action.includeFilesInContext.dialog.description"))
              .setCopyable(false)
              .setAllowAutoWrapping(true)
          )
          .addVerticalGap(4)
          .addLabeledComponent(totalTokensLabel, fileTreeScrollPane, true)
          .addVerticalGap(16)
          .panel
      )
      dialogBuilder.addOkAction().setText(CodeGPTBundle.get("dialog.continue"))
      dialogBuilder.addCancelAction()
      return dialogBuilder.show()
    }

    private fun getRestoreButton(promptTemplateTextArea: JBTextArea,
      repeatableContextTextArea: JBTextArea): JButton {
      val restoreButton = JButton(
        CodeGPTBundle.get("action.includeFilesInContext.dialog.restoreToDefaults.label"))
      restoreButton.addActionListener { _ ->
        val includedFilesSettings = IncludedFilesSettings.getCurrentState()
        includedFilesSettings.promptTemplate = IncludedFilesSettingsState.DEFAULT_PROMPT_TEMPLATE
        includedFilesSettings.repeatableContext = IncludedFilesSettingsState.DEFAULT_REPEATABLE_CONTEXT
        promptTemplateTextArea.text = IncludedFilesSettingsState.DEFAULT_PROMPT_TEMPLATE
        repeatableContextTextArea.text = IncludedFilesSettingsState.DEFAULT_REPEATABLE_CONTEXT
      }
      return restoreButton
    }
  }
}
