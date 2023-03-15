package ee.carlrobert.codegpt.ide.action;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import java.util.LinkedHashMap;
import java.util.Map;

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
        .map((entry) -> new String[] {entry.getKey(), entry.getValue()})
        .collect(toList())
        .toArray(new String[0][0]);
  }

  public static void refreshActions(Map<String, String> tableData) {
    ActionManager actionManager = ActionManager.getInstance();
    AnAction existingActionGroup = actionManager.getAction("ActionGroup");
    if (existingActionGroup instanceof DefaultActionGroup) {
      DefaultActionGroup group = (DefaultActionGroup) existingActionGroup;
      group.removeAll();
      group.add(new AskAction());
      group.addSeparator();
      group.add(new CustomPromptAction());
      group.addSeparator();
      tableData.forEach((action, prompt) -> group.add(new BaseAction(action) {
        @Override
        protected void actionPerformed(Project project, Editor editor, String selectedText) {
          sendMessage(project, prompt.replace("{{selectedCode}}", format("\n\n%s", selectedText)));
        }
      }));
    }
  }
}
