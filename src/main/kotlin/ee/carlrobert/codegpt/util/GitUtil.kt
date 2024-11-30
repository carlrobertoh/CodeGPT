package ee.carlrobert.codegpt.util

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
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
    fun getUnstagedDiff(
        project: Project,
        gitRepository: GitRepository,
        filePaths: List<String> = emptyList(),
    ): List<GitDiffDetails> {
        val handler = GitLineHandler(project, gitRepository.root, GitCommand.DIFF)
        handler.addParameters(
            "--cached",
            "--unified=1",
            "--diff-filter=AM",
            "--no-prefix",
            "--no-color",
        )

        filePaths.forEach { path ->
            handler.addParameters(path)
        }

        return Git.getInstance().runCommand(handler).outputAsJoinedString
            .split("(?=(diff --git [^\n]+))".toRegex())
            .filter { it.isNotEmpty() }
            .map { diffLine ->
                val lines = diffLine.lines()
                val fileName = lines.first().split(" ").last().substringAfterLast("/")
                val content = lines
                    .filter { line -> line.isNotEmpty() && !line.startsWith("+++") && !line.startsWith("---") }
                    .joinToString("\n")
                GitDiffDetails(fileName, content)
            }
            .filter { it.content.isNotEmpty() }
    }

    data class GitDiffDetails(val fileName: String, val content: String)

    @Throws(VcsException::class)
    @JvmStatic
    fun getProjectRepository(project: Project): GitRepository? {
        val repositoryManager = project.service<GitRepositoryManager>()
        return repositoryManager.getRepositoryForFile(project.guessProjectDir())
            ?: repositoryManager.repositories.firstOrNull()
    }

    @Throws(VcsException::class)
    fun getCommitsForHashes(
        project: Project,
        repository: GitRepository,
        commitHashes: List<String>
    ): List<GitCommit> {
        val result = mutableListOf<GitCommit>()

        GitHistoryUtils
            .loadDetails(project, repository.root, { commit ->
                if (commitHashes.contains(commit.id.asString())) {
                    result.add(commit)
                }
            })

        return result
    }

    @Throws(VcsException::class)
    fun getCommitDiffs(
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
