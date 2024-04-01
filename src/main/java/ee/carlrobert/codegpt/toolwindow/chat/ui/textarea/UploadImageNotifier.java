package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.util.messages.Topic;

public interface UploadImageNotifier {

  Topic<UploadImageNotifier> UPLOADED_FILE_PATH_TOPIC =
      Topic.create("uploadedFilePath", UploadImageNotifier.class);

  void fileUploaded(String filePath);
}
