package ee.carlrobert.codegpt.util

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsException
import git4idea.GitCommit
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.history.GitHistoryUtils
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

object GitUtil {

    private val logger = thisLogger()

    @Throws(VcsException::class)
    @JvmStatic
    fun getStagedDiff(
        project: Project,
        gitRepository: GitRepository,
        includedVersionedFilePaths: List<String> = emptyList()
    ): List<String> {
        return getGitDiff(project, gitRepository, includedVersionedFilePaths, true)
    }

    @Throws(VcsException::class)
    @JvmStatic
    fun getUnstagedDiff(
        project: Project,
        gitRepository: GitRepository,
        includedUnversionedFilePaths: List<String> = emptyList()
    ): List<String> {
        return getGitDiff(project, gitRepository, includedUnversionedFilePaths, false)
    }

    private fun getGitDiff(
        project: Project,
        gitRepository: GitRepository,
        filePaths: List<String>,
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

        filePaths.forEach { path ->
            handler.addParameters(path)
        }

        val commandResult = Git.getInstance().runCommand(handler)
        return filterDiffOutput(commandResult.output)
    }

    @Throws(VcsException::class)
    fun getProjectRepository(project: Project): GitRepository? {
        val repositoryManager = project.service<GitRepositoryManager>()
        return repositoryManager.getRepositoryForFile(project.workspaceFile)
            ?: repositoryManager.repositories.firstOrNull()
    }

    @Throws(VcsException::class)
    fun getCommitDiff(
        project: Project,
        gitRepository: GitRepository,
        commitHash: String
    ): List<String> {
        val handler = GitLineHandler(project, gitRepository.root, GitCommand.SHOW)
        handler.addParameters(
            commitHash,
            "--unified=2",
            "--no-prefix",
            "--no-color"
        )

        val commandResult = Git.getInstance().runCommand(handler)
        return filterDiffOutput(commandResult.output)
    }

    @Throws(VcsException::class)
    fun getAllRecentCommits(
        project: Project,
        repository: GitRepository,
        searchText: String? = "",
        limit: Int = 250
    ): List<GitCommit> {
        val result = mutableListOf<GitCommit>()

        try {
            GitHistoryUtils
                .loadDetails(project, repository.root, { commit ->
                    if (searchText.isNullOrEmpty()) {
                        result.add(commit)
                    } else {
                        if (commit.id.asString().contains(searchText, true)
                            || commit.fullMessage.contains(searchText, true)
                        ) {
                            result.add(commit)
                        }
                    }
                }, "-n", "$limit")
        } catch (e: VcsException) {
            logger.error("Error fetching commit history: {}", e.message)
        }

        return result
    }

    private fun filterDiffOutput(output: List<String>): List<String> {
        return output.filter {
            !it.startsWith("diff --git") &&
                    !it.startsWith("index ") &&
                    !it.startsWith("---") &&
                    !it.startsWith("+++") &&
                    !it.startsWith("- ") &&
                    !it.startsWith("commit ")
        }
    }
}
