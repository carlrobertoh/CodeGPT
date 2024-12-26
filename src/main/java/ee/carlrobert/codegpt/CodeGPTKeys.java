package ee.carlrobert.codegpt;

import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.predictions.CodeSuggestionDiffViewer;
import ee.carlrobert.codegpt.settings.prompts.PersonaDetails;
import ee.carlrobert.codegpt.ui.DocumentationDetails;
import ee.carlrobert.llm.client.codegpt.CodeGPTUserDetails;
import java.util.List;
import okhttp3.Call;

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
  public static final Key<String> REMAINING_EDITOR_COMPLETION =
      Key.create("codegpt.editorCompletionLines");
  public static final Key<Boolean> IS_FETCHING_COMPLETION =
      Key.create("codegpt.isFetchingCompletion");
  public static final Key<Boolean> IS_PROMPT_TEXT_FIELD_DOCUMENT =
      Key.create("codegpt.isPromptTextFieldDocument");
  public static final Key<CodeSuggestionDiffViewer> EDITOR_PREDICTION_DIFF_VIEWER =
      Key.create("codegpt.editorPredictionDiffViewer");
  public static final Key<Call> PENDING_PREDICTION_CALL =
      Key.create("codegpt.editorPendingPredictionCall");
}
