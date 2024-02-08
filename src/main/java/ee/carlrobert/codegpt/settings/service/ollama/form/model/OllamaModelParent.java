package ee.carlrobert.codegpt.settings.service.ollama.form.model;

import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel;
import ee.carlrobert.llm.client.ollama.completion.response.OllamaModel.OllamaModelDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class OllamaModelParent {

  private final String modelName;
  private final List<OllamaChildModel> childModels;

  public OllamaModelParent(OllamaModel ollamaModel, List<OllamaModel> childModels) {
    this.modelName = StringUtils.capitalize(ollamaModel.getName().split(":")[0]);
    this.childModels = childModels.stream()
        .filter(subModel -> {
              OllamaModelDetails subModelDetails = subModel.getDetails();
              OllamaModelDetails modelDetails = ollamaModel.getDetails();
              return subModelDetails.getFamily().equals(modelDetails.getFamily());
            }
        )
        .map(subModel -> new OllamaChildModel(subModel.getName(), subModel.getDetails()))
        .collect(Collectors.toList());
  }


  public String getTag() {
    return modelName;
  }

  public List<OllamaChildModel> getChildrenModels() {
    return childModels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return this.modelName.equals(((OllamaModelParent) o).modelName);
  }

  public List<Integer> getSortedUniqueModelSizes() {
    return childModels.stream().map(OllamaChildModel::getParameterSize).distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return modelName;
  }
}
