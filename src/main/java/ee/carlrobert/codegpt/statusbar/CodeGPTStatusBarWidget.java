package ee.carlrobert.codegpt.statusbar;

import static ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionProgressNotifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeGPTStatusBarWidget extends EditorBasedStatusBarPopup {

  private static final String ID = "ee.carlrobert.codegpt.statusbar.widget";

  public CodeGPTStatusBarWidget(Project project) {
    super(project, false);

    project.getMessageBus()
        .connect(this)
        .subscribe(
            CodeCompletionProgressNotifier.Companion.getCODE_COMPLETION_PROGRESS_TOPIC(),
            (CodeCompletionProgressNotifier) loading -> {
              CodeGPTStatusBarWidget widget = findWidget(project);
              if (widget != null && widget.myStatusBar != null) {
                widget.update(() -> widget.myStatusBar.updateWidget(ID));
              }
            });
  }

  @Override
  protected @NotNull WidgetState getWidgetState(@Nullable VirtualFile file) {
    var state = new WidgetState(CodeGPTBundle.get("statusBar.widget.tooltip"), "", true);
    var fetchingCompletion = IS_FETCHING_COMPLETION.get(getEditor());
    var loading = fetchingCompletion != null && fetchingCompletion;

    state.setIcon(loading ? Icons.StatusBarCompletionInProgress : Icons.DefaultSmall);
    return state;
  }

  @Override
  protected @Nullable ListPopup createPopup(@NotNull DataContext context) {
    return JBPopupFactory.getInstance()
        .createActionGroupPopup(
            CodeGPTBundle.get("project.label"),
            (ActionGroup) ActionManager.getInstance().getAction("codegpt.statusBarPopup"),
            context,
            ActionSelectionAid.SPEEDSEARCH,
            true);
  }

  @Override
  protected @NotNull StatusBarWidget createInstance(@NotNull Project project) {
    return new CodeGPTStatusBarWidget(project);
  }

  @Override
  public @NonNls @NotNull String ID() {
    return ID;
  }

  private static @Nullable CodeGPTStatusBarWidget findWidget(@NotNull Project project) {
    StatusBar bar = WindowManager.getInstance().getStatusBar(project);
    if (bar != null) {
      StatusBarWidget widget = bar.getWidget(ID);
      if (widget instanceof CodeGPTStatusBarWidget) {
        return (CodeGPTStatusBarWidget) widget;
      }
    }

    return null;
  }
}
