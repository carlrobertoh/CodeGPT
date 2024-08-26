package ee.carlrobert.codegpt.util;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.commit.CommitWorkflowUi;
import java.util.List;
import java.util.Objects;

public class CommitWorkflowChanges {

  private final List<String> includedVersionedFilePaths;
  private final List<String> includedUnversionedFilePaths;

  public CommitWorkflowChanges(CommitWorkflowUi commitWorkflowUi) {
    includedVersionedFilePaths = commitWorkflowUi.getIncludedChanges().stream()
        .map(Change::getVirtualFile)
        .filter(Objects::nonNull)
        .map(VirtualFile::getPath)
        .toList();
    includedUnversionedFilePaths = commitWorkflowUi.getIncludedUnversionedFiles().stream()
        .map(FilePath::getPath)
        .toList();
  }

  public List<String> getIncludedVersionedFilePaths() {
    return includedVersionedFilePaths;
  }

  public List<String> getIncludedUnversionedFilePaths() {
    return includedUnversionedFilePaths;
  }

  public boolean isFilesSelected() {
    return !includedVersionedFilePaths.isEmpty() || !includedUnversionedFilePaths.isEmpty();
  }
}
