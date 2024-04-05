package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager

class ChatToolWindowListener : ToolWindowManagerListener {

    override fun toolWindowShown(toolWindow: ToolWindow) {
        if ("CodeGPT" == toolWindow.id) {
            requestFocusForTextArea(toolWindow.project)
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
