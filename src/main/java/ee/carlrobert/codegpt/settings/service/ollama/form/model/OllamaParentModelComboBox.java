package ee.carlrobert.codegpt.settings.service.ollama.form.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class OllamaParentModelComboBox extends AbstractListModel<OllamaModelParent> implements
    ComboBoxModel<OllamaModelParent> {

  private final List<OllamaModelParent> myList;
  private OllamaModelParent mySelected;


  public OllamaParentModelComboBox() {
    myList = new ArrayList<>();
  }

  public void updateList(List<OllamaModelParent> models) {
    myList.clear();
    myList.addAll(models);
    // Select previous model if still present in refreshed models list
    var newSelection = myList.isEmpty() ? null : myList.get(0);
    for (OllamaModelParent model : myList) {
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
  public OllamaModelParent getElementAt(int index) {
    return myList.get(index);
  }

  @Override
  public void setSelectedItem(Object item) {
    @SuppressWarnings("unchecked") OllamaModelParent e = (OllamaModelParent) item;
    setSelectedItem(e);
  }

  public void setSelectedItem(OllamaModelParent item) {
    mySelected = item;
    fireContentsChanged(this, 0, getSize());
  }

  @Override
  public OllamaModelParent getSelectedItem() {
    return mySelected;
  }

  public List<OllamaModelParent> getList() {
    return myList;
  }
}
