package ee.carlrobert.codegpt.actions;

import com.intellij.util.messages.Topic;
import ee.carlrobert.codegpt.ReferencedFile;
import java.util.List;

public interface IncludeFilesInContextNotifier {

  Topic<IncludeFilesInContextNotifier> FILES_INCLUDED_IN_CONTEXT_TOPIC =
      Topic.create("filesIncludedInContext", IncludeFilesInContextNotifier.class);

  void filesIncluded(List<ReferencedFile> includedFiles);
}
