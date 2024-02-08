package ee.carlrobert.codegpt.settings.service.ollama.form;

import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class LlamaModelEnumComboBoxModel extends AbstractListModel<LlamaModel> implements
    ComboBoxModel<LlamaModel> {

  private final List<LlamaModel> myList;
  private LlamaModel mySelected;


  public LlamaModelEnumComboBoxModel() {
    myList = new ArrayList<>();
  }

  public void updateList(List<LlamaModel> models) {
    myList.clear();
    myList.addAll(models);
    // Select previous model if still present in refreshed models list
    var newSelection = myList.isEmpty() ? null : myList.get(0);
    for (LlamaModel model : myList) {
      if (model.equals(mySelected)) {
        newSelection = mySelected;
        break;
      }
    }
    setSelectedItem(newSelection);
  }

  @Override
  public int getSize() {
    return myList.size();
  }

  @Override
  public LlamaModel getElementAt(int index) {
    return myList.get(index);
  }

  @Override
  public void setSelectedItem(Object item) {
    @SuppressWarnings("unchecked") LlamaModel e = (LlamaModel) item;
    setSelectedItem(e);
  }

  public void setSelectedItem(LlamaModel item) {
    mySelected = item;
    fireContentsChanged(this, 0, getSize());
  }

  @Override
  public LlamaModel getSelectedItem() {
    return mySelected;
  }
}
