package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.progress.ProgressIndicator;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import java.util.function.Consumer;

public interface ModelDownload {

   void download(HuggingFaceModel model, ProgressIndicator indicator,
       Consumer<String> onUpdateProgress,
       Consumer<Exception> onFailed);
}
