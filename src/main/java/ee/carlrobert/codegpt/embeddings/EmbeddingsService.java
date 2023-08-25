package ee.carlrobert.codegpt.embeddings;

import static com.github.jelmerk.knn.util.VectorUtils.normalize;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.SearchResult;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.completions.CompletionClientProvider;
import ee.carlrobert.codegpt.embeddings.splitter.SplitterFactory;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.util.FileUtils;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.chat.response.ChatCompletionResponse;
import ee.carlrobert.openai.client.embeddings.EmbeddingsClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class EmbeddingsService {

  private static final Logger LOG = Logger.getInstance(EmbeddingsService.class);
  private static EmbeddingsService instance;

  private final VectorStore vectorStore;
  private final EmbeddingsClient embeddingsClient;

  private EmbeddingsService(Project project) {
    this.vectorStore = VectorStore.getInstance(project);
    this.embeddingsClient = CompletionClientProvider.getEmbeddingsClient();
  }

  public static EmbeddingsService getInstance(Project project) {
    if (instance == null) {
      instance = new EmbeddingsService(project);
    }
    return instance;
  }

  public List<double[]> getEmbeddings(List<String> chunks) {
    return embeddingsClient.getEmbeddings(chunks);
  }

  public GeneratedContextDetails buildRelevantContext(String prompt) {
    try {
      var inputEmbedding = embeddingsClient.getEmbedding(getSearchQuery(prompt));
      var sortedResult = vectorStore.loadIndex()
          .findNearest(normalize(inputEmbedding), 10)
          .stream()
          .map(SearchResult::item)
          .sorted(Comparator.comparing(Word::getMeta))
          .collect(toList());

      var context = sortedResult.stream().map(Word::id).collect(Collectors.joining());
      var fileNames = sortedResult.stream().map(Word::getMeta).collect(Collectors.toSet());

      return new GeneratedContextDetails(context, fileNames);
    } catch (IOException e) {
      LOG.error("Unable to load vector index", e);
      return new GeneratedContextDetails(prompt, Collections.emptySet());
    }
  }

  public List<Item<Object, double[]>> createEmbeddings(List<CheckedFile> listOfCheckedFileDetails, @Nullable ProgressIndicator indicator) {
    var words = new ArrayList<Item<Object, double[]>>();
    for (int i = 0; i < listOfCheckedFileDetails.size(); i++) {
      try {
        var fileDetails = listOfCheckedFileDetails.get(i);
        addEmbeddings(fileDetails.getFileName(), fileDetails.getFileContent(), words);

        if (indicator != null) {
          indicator.setFraction((double) i / listOfCheckedFileDetails.size());
        }
      } catch (Throwable t) {
        // ignore
      }
    }
    return words;
  }

  private String getSearchQuery(String userPrompt) throws JsonProcessingException {
    var message = new ChatCompletionMessage("user", FileUtils.getResourceContent("/prompts/text-generator.txt").replace("{prompt}", userPrompt));
    var request = new ChatCompletionRequest.Builder(List.of(message))
        .setModel(ChatCompletionModel.GPT_4)
        .setMaxTokens(400)
        .setTemperature(0.1)
        .setStream(false)
        .build();

    return new ObjectMapper()
        .readValue(CompletionClientProvider.getChatCompletionClient(SettingsState.getInstance()).call(request), ChatCompletionResponse.class)
        .getChoices()
        .get(0)
        .getMessage()
        .getContent();
  }

  private void addEmbeddings(String fileName, String fileContent, List<Item<Object, double[]>> prevEmbeddings) {
    var fileExtension = FileUtils.getFileExtension(fileName);
    var codeSplitter = SplitterFactory.getCodeSplitter(fileExtension);
    if (codeSplitter != null) {
      var chunks = codeSplitter.split(fileName, fileContent);
      var embeddings = embeddingsClient.getEmbeddings(chunks);
      for (int i = 0; i < chunks.size(); i++) {
        prevEmbeddings.add(new Word(chunks.get(i), fileName, normalize(embeddings.get(i))));
      }
    } else {
      var chunks = splitText(fileContent, 400);
      var embeddings = getEmbeddings(chunks);
      for (int i = 0; i < chunks.size(); i++) {
        prevEmbeddings.add(new Word(chunks.get(i), fileName, normalize(embeddings.get(i))));
      }
    }
  }

  private static List<String> splitText(String str, int chunkSize) {
    int len = str.length();
    var chunks = new ArrayList<String>();
    for (int i = 0; i < len; i += chunkSize) {
      chunks.add(str.substring(i, Math.min(len, i + chunkSize)));
    }
    return chunks;
  }
}
