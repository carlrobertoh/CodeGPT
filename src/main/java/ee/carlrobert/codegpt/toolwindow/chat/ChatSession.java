package ee.carlrobert.codegpt.toolwindow.chat;

import ee.carlrobert.codegpt.ReferencedFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChatSession {

  private final UUID id;
  private final Set<ReferencedFile> referencedFiles;

  public ChatSession() {
    this.id = UUID.randomUUID();
    this.referencedFiles = new HashSet<>();
  }

  public UUID getId() {
    return id;
  }

  public List<ReferencedFile> getReferencedFiles() {
    return new ArrayList<>(referencedFiles);
  }

  public void addReferencedFiles(List<ReferencedFile> files) {
    if (files == null) {
      throw new IllegalArgumentException("Referenced files cannot be null");
    }
    referencedFiles.addAll(files);
  }
}
