package ee.carlrobert.codegpt.ui.textarea

import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.ui.textarea.header.tag.TagDetails

interface TagProcessor {
    fun process(message: Message, tagDetails: TagDetails, promptBuilder: StringBuilder)
}
