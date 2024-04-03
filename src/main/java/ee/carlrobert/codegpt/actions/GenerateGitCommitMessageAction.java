package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.ui.Messages.OK;
import static com.intellij.util.ObjectUtils.tryCast;
import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserBase;
import com.intellij.openapi.vcs.changes.ui.CommitDialogChangesBrowser;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.openapi.vfs.VirtualFile;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

public class GenerateGitCommitMessageAction extends AnAction {

  public static final int MAX_TOKEN_COUNT_WARNING = 4096;
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
    var selectedService = GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider();
    if (selectedService == YOU) {
      event.getPresentation().setVisible(false);
      return;
    }

    var includedChangesFilePaths = getIncludedChangesFilePaths(event);
    var includedUnversionedChangesFilePaths = getIncludedUnversionedFilePaths(event);
    var filesSelected = !includedChangesFilePaths.isEmpty() || !includedUnversionedChangesFilePaths.isEmpty();
    var callAllowed = isCallAllowed(selectedService);
    event.getPresentation().setEnabled(callAllowed && filesSelected);
    event.getPresentation().setText(CodeGPTBundle.get(callAllowed
        ? "action.generateCommitMessage.title"
        : "action.generateCommitMessage.missingCredentials"));
  }

  private boolean isCallAllowed(ServiceType serviceType) {
    return (serviceType == OPENAI && OpenAICredentialManager.getInstance().isCredentialSet())
        || (serviceType == AZURE && AzureCredentialsManager.getInstance().isCredentialSet())
        || List.of(LLAMA_CPP, ANTHROPIC, CUSTOM_OPENAI).contains(serviceType);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project == null || project.getBasePath() == null) {
      return;
    }

    var gitDiff = getGitDiff(
        project,
        getIncludedChangesFilePaths(event),
        getIncludedUnversionedFilePaths(event));

    var tokenCount = encodingManager.countTokens(gitDiff);
    if (tokenCount > MAX_TOKEN_COUNT_WARNING
        && OverlayUtil.showTokenSoftLimitWarningDialog(tokenCount) != OK) {
      return;
    }

    var editor = getCommitMessageEditor(event);
    if (editor != null) {
      ((EditorEx) editor).setCaretVisible(false);
      CompletionRequestService.getInstance()
          .generateCommitMessageAsync(gitDiff, getEventListener(project, editor.getDocument()));
    }
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }

  private CompletionEventListener<String> getEventListener(Project project, Document document) {
    return new CompletionEventListener<>() {
      private final StringBuilder messageBuilder = new StringBuilder();

      @Override
      public void onMessage(String message, EventSource eventSource) {
        messageBuilder.append(message);
        var application = ApplicationManager.getApplication();
        application.invokeLater(() -> application.runWriteAction(
            () -> WriteCommandAction.runWriteCommandAction(project, () -> document.setText(messageBuilder))));
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

  private String getGitDiff(
      Project project,
      List<String> includedChangesFilePaths,
      List<String> includedUnversionedFilePaths) {
    return Stream.of(
        new AbstractMap.SimpleEntry<>(includedChangesFilePaths, true),
        new AbstractMap.SimpleEntry<>(includedUnversionedFilePaths, false))
        .filter(entry -> !entry.getKey().isEmpty())
        .map(entry -> {
          var process = createGitDiffProcess(project.getBasePath(), entry.getKey(), entry.getValue());
          return new BufferedReader(new InputStreamReader(process.getInputStream()))
              .lines()
              .collect(joining("\n"));
        })
        .collect(joining("\n"));
  }

  private Process createGitDiffProcess(String projectPath, List<String> filePaths, boolean cached) {
    var command = new ArrayList<String>();
    command.add("git");
    command.add("diff");
    if (cached) {
      command.add("--cached");
    }
    command.addAll(filePaths);

    var processBuilder = new ProcessBuilder(command);
    processBuilder.directory(new File(projectPath));
    try {
      return processBuilder.start();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to start git diff process", ex);
    }
  }

  private @NotNull List<String> getFilePaths(
      AnActionEvent event,
      Function<CommitDialogChangesBrowser, Stream<?>> extractor) {
    var changesBrowserBase = event.getData(ChangesBrowserBase.DATA_KEY);
    if (changesBrowserBase == null) {
      return List.of();
    }

    return extractor.apply((CommitDialogChangesBrowser) changesBrowserBase)
        .map(obj -> obj instanceof Change
            ? ((Change) obj).getVirtualFile()
            : ((FilePath) obj).getVirtualFile())
        .filter(Objects::nonNull)
        .map(VirtualFile::getPath)
        .distinct()
        .collect(toList());
  }

  private @NotNull List<String> getIncludedChangesFilePaths(AnActionEvent event) {
    return getFilePaths(event, browser -> browser.getIncludedChanges().stream());
  }

  private @NotNull List<String> getIncludedUnversionedFilePaths(AnActionEvent event) {
    return getFilePaths(event, browser -> browser.getIncludedUnversionedFiles().stream());
  }
}
