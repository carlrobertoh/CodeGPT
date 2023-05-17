package ee.carlrobert.codegpt.action;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.state.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.util.FileUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.text.CaseUtils;

public class ActionsUtil {

  public static Map<String, String> DEFAULT_ACTIONS = new LinkedHashMap<>(Map.of(
      "Find Bugs", "Find bugs in the following code: {{selectedCode}}",
      "Write Tests", "Write Tests for the following code: {{selectedCode}}",
      "Explain", "Explain the following code: {{selectedCode}}",
      "Refactor", "Refactor the following code: {{selectedCode}}",
      "Optimize", "Optimize the following code: {{selectedCode}}"));

  public static String[][] DEFAULT_ACTIONS_ARRAY = toArray(DEFAULT_ACTIONS);

  public static String[][] toArray(Map<String, String> actionsMap) {
    return actionsMap.entrySet()
        .stream()
        .map((entry) -> new String[]{entry.getKey(), entry.getValue()})
        .collect(toList())
        .toArray(new String[0][0]);
  }

  public static void refreshActions() {
    AnAction actionGroup = ActionManager.getInstance().getAction("codegpt.EditorActionGroup");
    if (actionGroup instanceof DefaultActionGroup) {
      DefaultActionGroup group = (DefaultActionGroup) actionGroup;
      group.removeAll();
      group.add(new AskAction());
      group.addSeparator();
      group.add(new CustomPromptAction());
      group.addSeparator();

      var configuredActions = ConfigurationState.getInstance().tableData;
      configuredActions.forEach((label, prompt) -> {
        // using label as action description to prevent com.intellij.diagnostic.PluginException
        // https://github.com/carlrobertoh/CodeGPT/issues/95
        var action = new BaseAction(label, label) {
          @Override
          protected void actionPerformed(Project project, Editor editor, String selectedText) {
            var fileExtension = FileUtils.getFileExtension(
                ((EditorImpl) editor).getVirtualFile().getName());
            // TODO: Requires more sophisticated language parsing(can't always rely on the file extension)
            sendMessage(project, prompt.replace("{{selectedCode}}",
                format("\n```%s\n%s\n```", fileExtension, selectedText)));
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
