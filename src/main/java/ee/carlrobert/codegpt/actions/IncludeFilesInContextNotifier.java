package ee.carlrobert.codegpt.actions;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;
import java.util.List;

public interface IncludeFilesInContextNotifier {

  Topic<IncludeFilesInContextNotifier> FILES_INCLUDED_IN_CONTEXT_TOPIC =
      Topic.create("filesIncludedInContext", IncludeFilesInContextNotifier.class);

  void filesIncluded(List<VirtualFile> includedFiles);
}
