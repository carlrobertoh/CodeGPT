package ee.carlrobert.codegpt.util;

import ee.carlrobert.codegpt.util.placeholder.BranchNameReplacer;
import ee.carlrobert.codegpt.util.placeholder.DateReplacer;
import ee.carlrobert.codegpt.util.placeholder.PlaceholderReplacer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public final class PlaceholderUtil {

  private final List<PlaceholderReplacer> replacers;

  public PlaceholderUtil() {
    replacers = new ArrayList<>();
    replacers.add(new BranchNameReplacer());
    replacers.add(new DateReplacer());
  }

  public static DefaultListModel<Object> getPlaceholderList() {
    var placeholderList = new DefaultListModel<>();
    for (PlaceholderReplacer replacer : new PlaceholderUtil().replacers) {
      placeholderList.addElement(
          replacer.getPlaceholderKey() + ": " + replacer.getPlaceholderDescription());
    }

    return placeholderList;
  }

  public String replacePlaceholder(String template) {
    for (PlaceholderReplacer replacer : replacers) {
      template = template.replace(
          '{' + replacer.getPlaceholderKey() + '}',
          replacer.getReplacementValue());
    }

    return template;
  }
}