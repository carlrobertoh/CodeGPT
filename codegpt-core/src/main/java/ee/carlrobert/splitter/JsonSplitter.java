package ee.carlrobert.splitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class JsonSplitter implements Splitter {

  private static final Logger LOG = Logger.getInstance(JsonSplitter.class);

  @Override
  public List<String> split(String fileName, String content) {
    var chunks = new ArrayList<String>();

    try {
      // TODO: Switch to ObjectMapper
      for (var entry : new JSONObject(content).toMap().entrySet()) {
        chunks.add(new ObjectMapper().writeValueAsString(entry));
      }
    } catch (JsonProcessingException e) {
      LOG.error("Something went wrong while chunking the json", e);
    }
    return chunks;
  }
}
