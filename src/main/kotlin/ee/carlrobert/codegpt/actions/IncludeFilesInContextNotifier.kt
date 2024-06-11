package ee.carlrobert.codegpt.actions

import com.intellij.util.messages.Topic
import ee.carlrobert.codegpt.ReferencedFile

fun interface IncludeFilesInContextNotifier {
  fun filesIncluded(includedFiles: List<ReferencedFile>)

  companion object {
    @JvmField
    val FILES_INCLUDED_IN_CONTEXT_TOPIC: Topic<IncludeFilesInContextNotifier> =
      Topic.create("filesIncludedInContext", IncludeFilesInContextNotifier::class.java)
  }
}
