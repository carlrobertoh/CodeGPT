package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.ui.Messages.OK;
import static com.intellij.util.ObjectUtils.tryCast;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserBase;
import com.intellij.openapi.vcs.changes.ui.CommitDialogChangesBrowser;
import com.intellij.openapi.vcs.ui.CommitMessage;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GenerateGitCommitMessageAction extends AnAction {

  private final EncodingManager encodingManager;

  public GenerateGitCommitMessageAction() {
    super(
        CodeGPTBundle.get("action.generateCommitMessage.title"),
        CodeGPTBundle.get("action.generateCommitMessage.description"),
        Icons.Sparkle);
    encodingManager = EncodingManager.getInstance();
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    var selectedService = GeneralSettingsState.getInstance().getSelectedService();
    if (selectedService == ServiceType.OPENAI || selectedService == ServiceType.AZURE) {
      var filesSelected = !getReferencedFilePaths(event).isEmpty();
      var openAiSettings = OpenAISettingsState.getInstance();
      var callAllowed = (selectedService == ServiceType.OPENAI
          && openAiSettings.getCredentialsManager().isCredentialSet())
          || (selectedService == ServiceType.AZURE
          && AzureSettingsState.getInstance().getCredentialsManager().isCredentialSet());
      event.getPresentation().setEnabled(callAllowed && filesSelected);
      event.getPresentation().setText(CodeGPTBundle.get(callAllowed
          ? "action.generateCommitMessage.title"
          : "action.generateCommitMessage.missingCredentials"));
    } else {
      event.getPresentation().setEnabled(false);
      event.getPresentation()
          .setText(CodeGPTBundle.get("action.generateCommitMessage.serviceWarning"));
    }
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project == null || project.getBasePath() == null) {
      return;
    }

    var gitDiff = getGitDiff(project, getReferencedFilePaths(event));
    var tokenCount = encodingManager.countTokens(gitDiff);
    if (tokenCount > 4096 && OverlayUtil.showTokenSoftLimitWarningDialog(tokenCount) != OK) {
      return;
    }

    var editor = getCommitMessageEditor(event);
    if (editor != null) {
      ((EditorEx) editor).setCaretVisible(false);
      CompletionRequestService.getInstance()
          .generateCommitMessageAsync(gitDiff, getEventListener(project, editor.getDocument()));
    }
  }

  private CompletionEventListener<String> getEventListener(Project project, Document document) {
    return new CompletionEventListener<>() {
      private final StringBuilder messageBuilder = new StringBuilder();

      @Override
      public void onMessage(String message) {
        messageBuilder.append(message);
        var application = ApplicationManager.getApplication();
        application.invokeLater(() ->
            application.runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(project, () ->
                    document.setText(messageBuilder))));
      }

      @Override
      public void onError(ErrorDetails error, Throwable ex) {
        Notifications.Bus.notify(new Notification(
            "CodeGPT Notification Group",
            "CodeGPT",
            error.getMessage(),
            NotificationType.ERROR));
      }
    };
  }

  private Editor getCommitMessageEditor(AnActionEvent event) {
    var commitMessage = tryCast(
        event.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL),
        CommitMessage.class);
    return commitMessage != null ? commitMessage.getEditorField().getEditor() : null;
  }

  private String getGitDiff(Project project, List<String> filePaths) {
    var process = createGitDiffProcess(project.getBasePath(), filePaths);
    var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return reader.lines().collect(joining("\n"));
  }

  private Process createGitDiffProcess(String projectPath, List<String> filePaths) {
    var command = new ArrayList<String>();
    command.add("git");
    command.add("diff");
    command.addAll(filePaths);

    var processBuilder = new ProcessBuilder(command);
    processBuilder.directory(new File(projectPath));
    try {
      return processBuilder.start();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to start git diff process", ex);
    }
  }

  private @NotNull List<String> getReferencedFilePaths(AnActionEvent event) {
    var changesBrowserBase = event.getData(ChangesBrowserBase.DATA_KEY);
    if (changesBrowserBase == null) {
      return List.of();
    }

    var includedChanges = ((CommitDialogChangesBrowser) changesBrowserBase).getIncludedChanges();
    return includedChanges.stream()
        .filter(item -> item.getVirtualFile() != null)
        .map(item -> item.getVirtualFile().getPath())
        .collect(toList());
  }
}
