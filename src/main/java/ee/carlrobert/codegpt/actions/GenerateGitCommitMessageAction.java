package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.ui.Messages.OK;
import static com.intellij.util.ObjectUtils.tryCast;
import static ee.carlrobert.codegpt.util.file.FileUtils.getResourceContent;
import static java.util.stream.Collectors.joining;

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
import com.intellij.openapi.vcs.ui.CommitMessage;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.util.OverlayUtils;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GenerateGitCommitMessageAction extends AnAction {

  private final EncodingManager encodingManager;

  public GenerateGitCommitMessageAction() {
    super(
        CodeGPTBundle.get("action.generateCommitMessage.title"),
        CodeGPTBundle.get("action.generateCommitMessage.description"),
        Icons.SparkleIcon);
    encodingManager = EncodingManager.getInstance();
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    var apiKeyExists = OpenAICredentialsManager.getInstance().isApiKeySet();
    event.getPresentation().setEnabled(apiKeyExists);
    event.getPresentation().setText(CodeGPTBundle.get(apiKeyExists
        ? "action.generateCommitMessage.title"
        : "action.generateCommitMessage.serviceWarning"));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project == null || project.getBasePath() == null) {
      return;
    }

    var gitDiff = getGitDiff(project);
    var tokenCount = encodingManager.countTokens(gitDiff);
    if (tokenCount > 4096 && OverlayUtils.showTokenSoftLimitWarningDialog(tokenCount) != OK) {
      return;
    }

    var editor = getCommitMessageEditor(event);
    if (editor != null) {
      ((EditorEx) editor).setCaretVisible(false);
      generateMessage(project, editor, gitDiff);
    }
  }

  private void generateMessage(Project project, Editor editor, String gitDiff) {
    CompletionClientProvider.getOpenAIClient().getChatCompletion(
        new OpenAIChatCompletionRequest.Builder(List.of(
            new OpenAIChatCompletionMessage("system",
                getResourceContent("/prompts/git-message.txt")),
            new OpenAIChatCompletionMessage("user", gitDiff)))
            .setModel(OpenAISettingsState.getInstance().getModel())
            .build(),
        getEventListener(project, editor.getDocument()));
  }

  private CompletionEventListener getEventListener(Project project, Document document) {
    return new CompletionEventListener() {
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

  // TODO: Get diff based on the user selection
  private String getGitDiff(Project project) {
    var process = createGitDiffProcess(project.getBasePath());
    var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return reader.lines().collect(joining("\n"));
  }

  private Process createGitDiffProcess(String projectPath) {
    var processBuilder = new ProcessBuilder("git", "diff", "--cached");
    processBuilder.directory(new File(projectPath));
    try {
      return processBuilder.start();
    } catch (IOException ex) {
      throw new RuntimeException("Unable to start git diff process", ex);
    }
  }
}
