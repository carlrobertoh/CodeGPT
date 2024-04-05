package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager

class ChatToolWindowListener : ToolWindowManagerListener {

    override fun stateChanged(toolWindowManager: ToolWindowManager) {
        toolWindowManager.getToolWindow("CodeGPT")?.run {
            if (isVisible) requestFocusForTextArea(project)
        }
    }

    private fun requestFocusForTextArea(project: Project) {
        val contentManager = project.getService(StandardChatToolWindowContentManager::class.java)
        contentManager.tryFindChatTabbedPane().ifPresent { tabbedPane ->
            tabbedPane.tryFindActiveTabPanel().ifPresent { tabPanel ->
                tabPanel.requestFocusForTextArea()
            }
        }
    }
}
