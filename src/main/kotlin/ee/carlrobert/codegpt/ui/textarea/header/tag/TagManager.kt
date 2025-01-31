package ee.carlrobert.codegpt.ui.textarea.header.tag

import com.intellij.openapi.vfs.VirtualFile
import java.util.*

class TagManager {

    private val tags = mutableSetOf<TagDetails>()
    private val listeners = mutableListOf<TagManagerListener>()

    fun addListener(listener: TagManagerListener) {
        listeners.add(listener)
    }

    fun getTags(): Set<TagDetails> = tags.toSet()

    fun addTag(tagDetails: TagDetails) {
        if (tags.add(tagDetails)) {
            listeners.forEach { it.onTagAdded(tagDetails) }
        }
    }

    fun removeFileTag(virtualFile: VirtualFile) {
        getFileTag(virtualFile)?.let {
            removeTag(it.id)
        }
    }

    fun removeTag(id: UUID) {
        tags.find { it.id == id }
            ?.let { tag ->
                if (tags.removeIf { it.id == tag.id }) {
                    listeners.forEach { it.onTagRemoved(tag) }
                }
            }
    }

    fun getTag(id: UUID): TagDetails? = tags.find { it.id == id }

    fun getFileTag(file: VirtualFile): FileTagDetails? =
        tags.filterIsInstance<FileTagDetails>().find { it.virtualFile == file }

    fun isFileTagExists(file: VirtualFile): Boolean = getFileTag(file) != null

    fun clear() {
        val tagsToRemove = tags.toList()
        tags.clear()
        tagsToRemove.forEach { tag ->
            listeners.forEach { it.onTagRemoved(tag) }
        }
    }
}
