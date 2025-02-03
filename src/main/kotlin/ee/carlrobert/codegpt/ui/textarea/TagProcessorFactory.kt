package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.ui.textarea.header.tag.*
import ee.carlrobert.codegpt.util.GitUtil
import git4idea.GitCommit

object TagProcessorFactory {

    fun getProcessor(project: Project, tagDetails: TagDetails): TagProcessor {
        return when (tagDetails) {
            is FileTagDetails -> FileTagProcessor()
            is SelectionTagDetails -> SelectionTagProcessor()
            is DocumentationTagDetails -> DocumentationTagProcessor()
            is PersonaTagDetails -> PersonaTagProcessor()
            is FolderTagDetails -> FolderTagProcessor()
            is WebTagDetails -> WebTagProcessor()
            is GitCommitTagDetails -> GitCommitTagProcessor(project)
            is CurrentGitChangesTagDetails -> CurrentGitChangesTagProcessor(project)
            else -> throw IllegalArgumentException("Unknown tag type: ${tagDetails::class.simpleName}")
        }
    }
}

class FileTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is FileTagDetails) {
            return
        }
        if (message.referencedFilePaths == null) {
            message.referencedFilePaths = mutableListOf()
        }
        message.referencedFilePaths?.add(tagDetails.virtualFile.path)
    }
}

class SelectionTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        val selectionTagDetails = tagDetails as? SelectionTagDetails ?: return
        if (selectionTagDetails.selectedText.isNullOrEmpty()) {
            return
        }

        promptBuilder
            .append("\n```${tagDetails.virtualFile.extension}\n")
            .append(selectionTagDetails.selectedText)
            .append("\n```\n")

        selectionTagDetails.selectionModel.let {
            if (it.hasSelection()) {
                it.removeSelection()
            }
        }
    }
}

class DocumentationTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is DocumentationTagDetails) {
            return
        }
        message.documentationDetails = tagDetails.documentationDetails
    }
}

class PersonaTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is PersonaTagDetails) {
            return
        }
        message.personaName = tagDetails.personaDetails.name
    }
}

class FolderTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is FolderTagDetails) {
            return
        }

        if (message.referencedFilePaths == null) {
            message.referencedFilePaths = mutableListOf()
        }

        processFolder(tagDetails.folder, message.referencedFilePaths ?: mutableListOf())
    }

    private fun processFolder(folder: VirtualFile, referencedFilePaths: MutableList<String>) {
        folder.children.forEach { child ->
            when {
                child.isDirectory -> processFolder(child, referencedFilePaths)
                else -> referencedFilePaths.add(child.path)
            }
        }
    }
}

class WebTagProcessor : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is WebTagDetails) {
            return
        }
        message.isWebSearchIncluded = true
    }
}

class GitCommitTagProcessor(private val project: Project) : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is GitCommitTagDetails) {
            return
        }
        promptBuilder
            .append("\n```shell\n")
            .append(getDiffString(project, tagDetails.gitCommit))
            .append("\n```\n")
    }

    private fun getDiffString(project: Project, gitCommit: GitCommit): String {
        return ProgressManager.getInstance().runProcessWithProgressSynchronously<String, Exception>(
            {
                val repository = GitUtil.getProjectRepository(project)
                    ?: return@runProcessWithProgressSynchronously ""

                val commitId = gitCommit.id.asString()
                val diff = GitUtil.getCommitDiffs(project, repository, commitId)
                    .joinToString("\n")

                service<EncodingManager>().truncateText(diff, 8192, true)
            },
            "Getting Commit Diff",
            true,
            project
        )
    }
}

class CurrentGitChangesTagProcessor(private val project: Project) : TagProcessor {
    override fun process(
        message: Message,
        tagDetails: TagDetails,
        promptBuilder: StringBuilder
    ) {
        if (tagDetails !is CurrentGitChangesTagDetails) {
            return
        }

        ProgressManager.getInstance().runProcessWithProgressSynchronously<Unit, Exception>(
            {
                GitUtil.getCurrentChanges(project)?.let {
                    promptBuilder
                        .append("\n```shell\n")
                        .append(it)
                        .append("\n```\n")
                }
            },
            "Getting Current Changes",
            true,
            project
        )
    }
}