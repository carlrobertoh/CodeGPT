package ee.carlrobert.codegpt.completions.llama;

import java.util.List;

public record LlamaServerStartupParams(String modelPath, int contextLength, int threads, int port,
                                       List<String> additionalRunParameters,
                                       List<String> additionalBuildParameters) {
}
