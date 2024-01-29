package ee.carlrobert.codegpt.completions;

public interface LlmModel {

  int getParameterSize();
  int getQuantization();

  /**
   * Identifier of the model.
   * Can be a plain String, URL or similar
   */
  String getId();

}
