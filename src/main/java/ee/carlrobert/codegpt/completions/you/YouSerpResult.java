package ee.carlrobert.codegpt.completions.you;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YouSerpResult(String url, String name, String snippet, String snippetSource) {
}
