package ee.carlrobert.codegpt;

import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.settings.persona.PersonaDetails;
import ee.carlrobert.codegpt.ui.DocumentationDetails;
import ee.carlrobert.llm.client.codegpt.CodeGPTUserDetails;
import java.util.List;

public class CodeGPTKeys {

  public static final Key<String> PREVIOUS_INLAY_TEXT =
      Key.create("codegpt.editor.inlay.prev-value");
  public static final Key<List<ReferencedFile>> SELECTED_FILES =
      Key.create("codegpt.selectedFiles");
  public static final Key<String> IMAGE_ATTACHMENT_FILE_PATH =
      Key.create("codegpt.imageAttachmentFilePath");
  public static final Key<CodeGPTUserDetails> CODEGPT_USER_DETAILS =
      Key.create("codegpt.userDetails");
  public static final Key<DocumentationDetails> ADDED_DOCUMENTATION =
      Key.create("codegpt.addedDocumentation");
  public static final Key<PersonaDetails> ADDED_PERSONA =
      Key.create("codegpt.addedPersona");
}
