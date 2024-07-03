package ee.carlrobert.codegpt.completions.llama;

import java.util.List;
import java.util.Map;

public record LlamaServerStartupParams(String modelPath, int contextLength, int threads, int port,
                                       List<String> additionalRunParameters,
                                       List<String> additionalBuildParameters,
                                       Map<String, String> additionalEnvironmentVariables) {
}
