package ee.carlrobert.codegpt.settings.configuration

import com.intellij.openapi.project.Project
import git4idea.GitUtil
import git4idea.branch.GitBranchUtil
import java.time.LocalDate

enum class Placeholder(val description: String, val code: String) {
    DATE_ISO_8601("Current date in ISO 8601 format, e.g. 2021-01-01.", "$" + "DATE_ISO_8601"),
    BRANCH_NAME("The name of the current branch.", "$" + "BRANCH_NAME"),
    PREFIX("Code before the cursor.", "$" + "PREFIX"),
    SUFFIX("Code after the cursor.", "$" + "SUFFIX"),
    FIM_PROMPT(
        "Prebuilt Fill-In-The-Middle (FIM) prompt using the specified template.",
        "$" + "FIM_PROMPT"
    ),
}

interface PlaceholderStrategy {
    fun getReplacementValue(): String
}

class DatePlaceholderStrategy : PlaceholderStrategy {
    override fun getReplacementValue(): String {
        return LocalDate.now().toString()
    }
}

class BranchNamePlaceholderStrategy(val project: Project) : PlaceholderStrategy {
    override fun getReplacementValue(): String {
        return try {
            val repositories = GitUtil.getRepositoryManager(project).repositories
            if (repositories.isEmpty() || repositories.size != 1) {
                return "BRANCH-UNKNOWN"
            }

            GitBranchUtil.getBranchNameOrRev(repositories[0])
        } catch (ignore: Exception) {
            "BRANCH-UNKNOWN"
        }
    }
}