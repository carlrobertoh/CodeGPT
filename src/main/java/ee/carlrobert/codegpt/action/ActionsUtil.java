package ee.carlrobert.codegpt.action;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.util.FileUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

      AtomicInteger counter = new AtomicInteger(1);
      tableData.forEach((action, prompt) -> {
        action = "Codegpt -"+action;
        String actionId = "ee.carlrobert.codegpt.action" + action;
        AnAction anAction = new BaseAction(action) {
          @Override
          protected void actionPerformed(Project project, Editor editor, String selectedText) {
            var fileExtension = FileUtils.getFileExtension(((EditorImpl) editor).getVirtualFile().getName());
            // TODO: Requires more sophisticated language parsing(can't always rely on the file extension)
            sendMessage(project, editor, prompt.replace("{{selectedCode}}", format("\n```%s\n%s\n```", fileExtension, selectedText)));
          }
        };


        group.add(anAction);
        // Unregister the previous action instance if already exists with the same ID
        AnAction oldAction = actionManager.getAction(actionId);
        if (oldAction != null) {
          actionManager.unregisterAction(actionId);
        }
        actionManager.registerAction(actionId, anAction);

      });
    }



    }
  }




