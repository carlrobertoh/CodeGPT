package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.util.messages.Topic;

public interface AttachImageNotifier {

  Topic<AttachImageNotifier> IMAGE_ATTACHMENT_FILE_PATH_TOPIC =
      Topic.create("imageAttachmentFilePath", AttachImageNotifier.class);

  void imageAttached(String filePath);
}
