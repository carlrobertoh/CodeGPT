package ee.carlrobert.codegpt.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsException
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository

object GitUtil {

    @Throws(VcsException::class)
    @JvmStatic
    fun getStagedDiff(project: Project, gitRepository: GitRepository): List<String> {
        return getGitDiff(project, gitRepository, true)
    }

    @Throws(VcsException::class)
    @JvmStatic
    fun getUnstagedDiff(project: Project, gitRepository: GitRepository): List<String> {
        return getGitDiff(project, gitRepository, false)
    }

    private fun getGitDiff(
        project: Project,
        gitRepository: GitRepository,
        staged: Boolean
    ): List<String> {
        val handler = GitLineHandler(project, gitRepository.root, GitCommand.DIFF)
        if (staged) {
            handler.addParameters("--cached")
        }
        handler.addParameters(
            "--unified=2",
            "--diff-filter=AM",
            "--no-prefix",
            "--no-color",
        )

        val commandResult = Git.getInstance().runCommand(handler)
        return commandResult.output.filter {
            listOf("diff --git", "index ", "---", "- ", "+++").none { prefix -> it.startsWith(prefix) }
        }
    }
}
