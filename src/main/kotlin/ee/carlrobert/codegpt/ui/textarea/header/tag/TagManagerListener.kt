package ee.carlrobert.codegpt.ui.textarea.header.tag

interface TagManagerListener {
    fun onTagAdded(tag: TagDetails)
    fun onTagRemoved(tag: TagDetails)
    fun onTagSelectionChanged(tag: TagDetails)
}