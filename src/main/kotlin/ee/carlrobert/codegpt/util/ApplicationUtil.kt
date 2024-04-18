package ee.carlrobert.codegpt.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.IdeFocusManager

object ApplicationUtil {
  @JvmStatic
  fun isUnitTestingMode(): Boolean {
      val app = ApplicationManager.getApplication()
      return app != null && app.isUnitTestMode
    }

  @JvmStatic
  fun findCurrentProject(): Project? {
    val frame = IdeFocusManager.getGlobalInstance().lastFocusedFrame
    val project = frame?.project
    if (isValidProject(project)) {
      return project
    }
    return findProjectFromOpenProjects()
  }

  private fun findProjectFromOpenProjects(): Project? {
    for (project in ProjectManager.getInstance().openProjects) {
      if (isValidProject(project)) {
        return project
      }
    }
    return null
  }

  private fun isValidProject(project: Project?): Boolean {
    return project != null && !project.isDisposed && !project.isDefault
  }
}
