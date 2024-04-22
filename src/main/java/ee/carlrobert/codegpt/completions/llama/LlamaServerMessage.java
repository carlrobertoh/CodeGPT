package ee.carlrobert.codegpt.completions.llama;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LlamaServerMessage(String level, String msg) {
}
