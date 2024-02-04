package ee.carlrobert.codegpt.settings.service.llama.ollama;

import static java.lang.String.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.settings.service.llama.ModelDownload;
import ee.carlrobert.codegpt.settings.state.OllamaSettingsState;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaPullRequest;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaPullResponse;
import java.util.function.Consumer;

public class OllamaModelDownload implements ModelDownload {

  private static final Logger LOG = Logger.getInstance(OllamaModelDownload.class);

  @Override
  public void download(HuggingFaceModel model, ProgressIndicator indicator,
      Consumer<String> onUpdateProgress,
      Consumer<Exception> onFailed) {
    indicator.setIndeterminate(true);
    indicator.setText(format(
        CodeGPTBundle.get(
            "settingsConfigurable.service.llama.progress.downloadingModelIndicator.text"),
        model.getModelFileName()));
    OllamaPullResponse ollamaPullResponse = CompletionClientProvider.getOllamaClient()
        .pullModel(new OllamaPullRequest(model.getModelTag(), false));
    if (!ollamaPullResponse.getStatus().equals("success")) {
      LOG.error(
          String.format("Ollama /api/pull failed: status:%s", ollamaPullResponse.getStatus()));
      onFailed.accept(new RuntimeException(
          String.format("Ollama /api/pull failed: status:%s", ollamaPullResponse.getStatus())));
    } else {
      onUpdateProgress.accept("Finished model download");
      OllamaModel ollamaModel = new OllamaModel();
      ollamaModel.setModel(model.getModelTag());
      OllamaSettingsState.getInstance().addModelToAvailable(ollamaModel);
    }
  }

}