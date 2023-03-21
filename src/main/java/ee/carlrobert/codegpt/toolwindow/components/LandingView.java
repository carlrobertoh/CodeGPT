package ee.carlrobert.codegpt.toolwindow.components;

import static java.util.stream.Collectors.toList;

import icons.Icons;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LandingView {

  private static final List<String> questions = List.of("How do I make an HTTP request in Javascript?",
      "What is the difference between px, dip, dp, and sp?",
      "How do I undo the most recent local commits in Git?",
      "What is the difference between stack and heap?");

  public List<JPanel> getQuestionPanels() {
    return questions.stream()
        .map(this::createQuestionPanel)
        .collect(toList());
  }

  public JPanel createImageIconPanel() {
    var imageIconPanel = new JPanel();
    imageIconPanel.setLayout(new GridBagLayout());
    var imageIconLabel = new JLabel(Icons.SunImageIcon);
    imageIconLabel.setHorizontalAlignment(JLabel.CENTER);
    imageIconPanel.add(imageIconLabel);
    return imageIconPanel;
  }

  private JPanel createQuestionPanel(String question) {
    var panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    var label = new JLabel(question, SwingConstants.CENTER);
    label.setHorizontalAlignment(JLabel.CENTER);
    panel.add(label);
    return panel;
  }
}
