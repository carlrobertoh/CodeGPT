package ee.carlrobert.codegpt.completions;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.net.URL;
import java.util.Collection;
import java.util.List;

public interface LlmModel {

  int getParameterSize();
  int getQuantization();

  static List<Integer> getSortedUniqueModelSizes(Collection<LlmModel> models) {
    return models.stream()
        .map(LlmModel::getParameterSize)
        .collect(toSet())
        .stream()
        .sorted()
        .collect(toList());
  }

  URL getModelUrl();
}
