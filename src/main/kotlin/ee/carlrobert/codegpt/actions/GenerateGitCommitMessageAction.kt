package ee.carlrobert.codegpt.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.ui.CommitMessage
import com.intellij.vcs.commit.CommitWorkflowUi
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.completions.CompletionRequestService.isRequestAllowed
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.configuration.CommitMessageTemplate
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.ServiceType.YOU
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.Objects
import java.util.Optional
import java.util.stream.Collectors.joining

class GenerateGitCommitMessageAction : AnAction(
  CodeGPTBundle.get("action.generateCommitMessage.title"),
  CodeGPTBundle.get("action.generateCommitMessage.description"),
  Icons.Sparkle) {

  private val encodingManager: EncodingManager = EncodingManager.getInstance()

  override fun update(event: AnActionEvent) {
    val commitWorkflowUi = event.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
    if (GeneralSettings.isSelected(YOU) || commitWorkflowUi == null) {
      event.presentation.isVisible = false
      return
    }

    val callAllowed = isRequestAllowed()
    event.presentation.isEnabled = callAllowed
            && CommitWorkflowChanges(commitWorkflowUi).isFilesSelected
    event.presentation.text = CodeGPTBundle.get(
      if (callAllowed)
        "action.generateCommitMessage.title"
      else "action.generateCommitMessage.missingCredentials")
  }

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    project.basePath ?: return

    val gitDiff = getGitDiff(event, project)
    val tokenCount = encodingManager.countTokens(gitDiff)
    if (tokenCount > MAX_TOKEN_COUNT_WARNING
      && OverlayUtil.showTokenSoftLimitWarningDialog(tokenCount) != Messages.OK) {
      return
    }

    val editor = getCommitMessageEditor(event) as? EditorEx ?: return
    editor.setCaretVisible(false)
    CompletionRequestService.getInstance().generateCommitMessageAsync(
      project.getService(CommitMessageTemplate::class.java).getSystemPrompt(),
      gitDiff,
      getEventListener(project, editor.document))
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.EDT
  }

  private fun getEventListener(project: Project, document: Document): CompletionEventListener<String> {
    return object : CompletionEventListener<String> {
      private val messageBuilder = StringBuilder()

      override fun onMessage(message: String?, eventSource: EventSource) {
        messageBuilder.append(message)
        val application = ApplicationManager.getApplication()
        application.invokeLater {
          application.runWriteAction {
            WriteCommandAction.runWriteCommandAction(project) {
              document.setText(messageBuilder)
            }
          }
        }
      }

      override fun onError(error: ErrorDetails, ex: Throwable) {
        Notifications.Bus.notify(Notification(
            "CodeGPT Notification Group",
            "CodeGPT",
            error.message,
            NotificationType.ERROR))
      }
    }
  }

  private fun getCommitMessageEditor(event: AnActionEvent): Editor? {
    return (event.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as? CommitMessage)?.editorField?.editor
  }

  private fun getGitDiff(event: AnActionEvent, project: Project): String {
    val commitWorkflowUi = Optional.ofNullable(event.getData(VcsDataKeys.COMMIT_WORKFLOW_UI))
      .orElseThrow { IllegalStateException("Could not retrieve commit workflow ui.") }
    val changes = CommitWorkflowChanges(commitWorkflowUi)
    val projectBasePath = project.basePath
    val gitDiff = getGitDiff(projectBasePath, changes.includedVersionedFilePaths, false)
    val stagedGitDiff = getGitDiff(projectBasePath, changes.includedVersionedFilePaths, true)
    val newFilesContent = getNewFilesDiff(projectBasePath, changes.includedUnversionedFilePaths)

    return mapOf(
      "Git diff" to gitDiff,
      "Staged git diff" to stagedGitDiff,
      "New files" to newFilesContent)
      .entries.stream()
      .filter { !it.value.isNullOrBlank() }
      .map { "%s:%n%s".formatted(it.key, it.value) }
      .collect(joining("\n\n"))
  }

  private fun getGitDiff(projectPath: String?, filePaths: List<String>, cached: Boolean): String {
    if (filePaths.isEmpty()) {
      return ""
    }

    val process = createGitDiffProcess(projectPath, filePaths, cached)
    return BufferedReader(InputStreamReader(process.inputStream)).lines().collect(joining("\n"))
  }

  private fun getNewFilesDiff(projectPath: String?, filePaths: List<String>): String? {
    return filePaths.stream()
      .map { pathString ->
        val filePath = Path.of(pathString)
        val relativePath = Path.of(projectPath).relativize(filePath)
        try {
          return@map """
            New file '$relativePath' content:
            ${Files.readString(filePath)}
            """.trimIndent()
        } catch (ignored: IOException) {
          return@map null
        }
      }
      .filter(Objects::nonNull)
      .collect(joining("\n"))
  }

  private fun createGitDiffProcess(projectPath: String?, filePaths: List<String>, cached: Boolean): Process {
    val command = mutableListOf("git", "diff")
    if (cached) {
      command.add("--cached")
    }
    command.addAll(filePaths)

    val processBuilder = ProcessBuilder(command)
    processBuilder.directory(File(projectPath))
    try {
      return processBuilder.start()
    } catch (ex: IOException) {
      throw RuntimeException("Unable to start git diff process", ex)
    }
  }

  internal class CommitWorkflowChanges(commitWorkflowUi: CommitWorkflowUi) {
    val includedVersionedFilePaths: List<String> = commitWorkflowUi.getIncludedChanges().stream()
      .map { it.virtualFile }
      .filter(Objects::nonNull)
      .map { it!!.path }
      .toList()
    val includedUnversionedFilePaths: List<String> =
      commitWorkflowUi.getIncludedUnversionedFiles().stream().map { it.path }.toList()

    val isFilesSelected =
      includedVersionedFilePaths.isNotEmpty() || includedUnversionedFilePaths.isNotEmpty()
  }

  companion object {
    const val MAX_TOKEN_COUNT_WARNING: Int = 4096
  }
}
