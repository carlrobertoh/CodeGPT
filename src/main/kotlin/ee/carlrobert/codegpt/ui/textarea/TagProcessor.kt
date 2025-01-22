package ee.carlrobert.codegpt.ui.textarea

import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.ui.textarea.header.HeaderTagDetails

interface TagProcessor {
    fun process(message: Message, tagDetails: HeaderTagDetails, promptBuilder: StringBuilder)
}
