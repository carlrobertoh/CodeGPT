package ee.carlrobert.codegpt.actions.editor;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.util.file.FileUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.text.CaseUtils;

public class EditorActionsUtil {

  public static Map<String, String> DEFAULT_ACTIONS = new LinkedHashMap<>(Map.of(
      "Find Bugs", "Find bugs and output code with bugs "
          + "fixed in the following code: {{selectedCode}}",
      "Write Tests", "Write Tests for the selected code {{selectedCode}}",
      "Explain", "Explain the selected code {{selectedCode}}",
      "Refactor", "Refactor the selected code {{selectedCode}}",
      "Optimize", "Optimize the selected code {{selectedCode}}"));

  public static String[][] DEFAULT_ACTIONS_ARRAY = toArray(DEFAULT_ACTIONS);

  public static String[][] toArray(Map<String, String> actionsMap) {
    return actionsMap.entrySet()
        .stream()
        .map((entry) -> new String[] {entry.getKey(), entry.getValue()})
        .collect(toList())
        .toArray(new String[0][0]);
  }

  public static void refreshActions() {
    AnAction actionGroup =
        ActionManager.getInstance().getAction("action.editor.group.EditorActionGroup");
    if (actionGroup instanceof DefaultActionGroup) {
      DefaultActionGroup group = (DefaultActionGroup) actionGroup;
      group.removeAll();
      group.add(new AskAction());
      group.add(new CustomPromptAction());
      group.addSeparator();

      var configuredActions = ConfigurationState.getInstance().getTableData();
      configuredActions.forEach((label, prompt) -> {
        // using label as action description to prevent com.intellij.diagnostic.PluginException
        // https://github.com/carlrobertoh/CodeGPT/issues/95
        var action = new BaseEditorAction(label, label) {
          @Override
          protected void actionPerformed(Project project, Editor editor, String selectedText) {
            var fileExtension = FileUtils.getFileExtension(
                ((EditorImpl) editor).getVirtualFile().getName());
            var message = new Message(prompt.replace(
                "{{selectedCode}}",
                format("\n```%s\n%s\n```", fileExtension, selectedText)));
            message.setUserMessage(prompt.replace("{{selectedCode}}", ""));
            var toolWindowContentManager =
                project.getService(StandardChatToolWindowContentManager.class);
            var toolWindow = toolWindowContentManager.getToolWindow();
            if (toolWindow != null) {
              toolWindow.show();

              if (ConfigurationState.getInstance().isCreateNewChatOnEachAction()) {
                toolWindowContentManager.createNewTabPanel();
              }
            }
            toolWindowContentManager.sendMessage(message);
          }
        };
        group.add(action);
      });
    }
  }

  public static void registerOrReplaceAction(AnAction action) {
    ActionManager actionManager = ActionManager.getInstance();
    var actionId = convertToId(action.getTemplateText());
    if (actionManager.getAction(actionId) != null) {
      actionManager.replaceAction(actionId, action);
    } else {
      actionManager.registerAction(actionId, action, PluginId.getId("ee.carlrobert.chatgpt"));
    }
  }

  public static String convertToId(String label) {
    return "codegpt." + CaseUtils.toCamelCase(label, true);
  }
}
