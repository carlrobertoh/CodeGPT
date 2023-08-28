package ee.carlrobert.codegpt.embeddings;

import static com.github.jelmerk.knn.util.VectorUtils.normalize;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jelmerk.knn.Item;
import com.github.jelmerk.knn.SearchResult;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import ee.carlrobert.codegpt.embeddings.splitter.SplitterFactory;
import ee.carlrobert.openai.client.completion.CompletionClient;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.chat.response.ChatCompletionResponse;
import ee.carlrobert.openai.client.embeddings.EmbeddingsClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class EmbeddingsService {

  private static final Logger LOG = Logger.getInstance(EmbeddingsService.class);

  private final VectorStore vectorStore;
  private final EmbeddingsClient embeddingsClient;
  private final CompletionClient completionClient;

  public EmbeddingsService(EmbeddingsClient embeddingsClient, CompletionClient completionClient, Path pluginBasePath) {
    this.embeddingsClient = embeddingsClient;
    this.completionClient = completionClient;
    this.vectorStore = VectorStore.getInstance(pluginBasePath);
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

  public String buildPromptWithContext(String prompt) {
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

      return getResourceContent("/prompts/prompt-with-context.txt")
          .replace("{prompt}", prompt)
          .replace("{context}", new GeneratedContextDetails(context, fileNames).getContext());
    } catch (IOException e) {
      LOG.error("Unable to load vector index", e);
      return prompt;
    }
  }

  public List<Item<Object, double[]>> createEmbeddings(List<CheckedFile> checkedFiles, @Nullable ProgressIndicator indicator) {
    var words = new ArrayList<Item<Object, double[]>>();
    for (int i = 0; i < checkedFiles.size(); i++) {
      try {
        var checkedFile = checkedFiles.get(i);
        addEmbeddings(checkedFile, words);

        if (indicator != null) {
          indicator.setFraction((double) i / checkedFiles.size());
        }
      } catch (Throwable t) {
        // ignore
      }
    }
    return words;
  }

  private String getSearchQuery(String userPrompt) throws JsonProcessingException {
    var message = new ChatCompletionMessage("user", getResourceContent("/prompts/text-generator.txt").replace("{prompt}", userPrompt));
    var request = new ChatCompletionRequest.Builder(List.of(message))
        .setModel(ChatCompletionModel.GPT_4)
        .setMaxTokens(400)
        .setTemperature(0.1)
        .setStream(false)
        .build();

    return new ObjectMapper()
        .readValue(completionClient.call(request), ChatCompletionResponse.class)
        .getChoices()
        .get(0)
        .getMessage()
        .getContent();
  }

  private void addEmbeddings(CheckedFile checkedFile, List<Item<Object, double[]>> prevEmbeddings) {
    var fileExtension = checkedFile.getFileExtension();
    var codeSplitter = SplitterFactory.getCodeSplitter(fileExtension);
    if (codeSplitter != null) {
      var chunks = codeSplitter.split(checkedFile.getFileName(), checkedFile.getFileContent());
      var embeddings = embeddingsClient.getEmbeddings(chunks);
      for (int i = 0; i < chunks.size(); i++) {
        prevEmbeddings.add(new Word(chunks.get(i), checkedFile.getFileName(), normalize(embeddings.get(i))));
      }
    } else {
      var chunks = splitText(checkedFile.getFileContent(), 400);
      var embeddings = getEmbeddings(chunks);
      for (int i = 0; i < chunks.size(); i++) {
        prevEmbeddings.add(new Word(chunks.get(i), checkedFile.getFileName(), normalize(embeddings.get(i))));
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

  // TODO: Move to shared module
  private static String getResourceContent(String name) {
    try (var stream = Objects.requireNonNull(EmbeddingsService.class.getResourceAsStream(name))) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read resource", e);
    }
  }
}
