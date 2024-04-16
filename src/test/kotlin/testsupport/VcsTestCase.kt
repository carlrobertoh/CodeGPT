package testsupport

import com.intellij.openapi.components.service
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsDirectoryMapping
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.waitUntil
import git4idea.GitUtil
import git4idea.GitVcs
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import kotlinx.coroutines.runBlocking
import java.nio.file.Path

open class VcsTestCase : BasePlatformTestCase() {

    private lateinit var projectDir: VirtualFile

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        projectDir =
            LocalFileSystem.getInstance().findFileByNioFile(Path.of(project.basePath!!))
                ?: error("Project directory not found")
    }

    @Throws(Exception::class)
    override fun tearDown() {
        project.service<ProjectLevelVcsManager>().directoryMappings = emptyList()
        super.tearDown()
    }

    fun git(command: GitCommand, parameters: List<String> = emptyList()) {
        val checkoutHandler = GitLineHandler(project, projectDir, command)
        checkoutHandler.addParameters(parameters)
        service<Git>().runCommand(checkoutHandler).throwOnError()
    }

    fun waitUntilChangesApplied() {
        project.service<ProjectLevelVcsManager>().directoryMappings =
            listOf(VcsDirectoryMapping("", GitVcs.NAME))
        runBlocking {
            waitUntil {
                GitUtil.getRepositories(project).isNotEmpty()
            }
        }
    }
}