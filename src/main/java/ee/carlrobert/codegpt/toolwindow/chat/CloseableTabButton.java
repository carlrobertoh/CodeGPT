package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.icons.AllIcons;
import com.intellij.util.ui.JBUI;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CloseableTabButton extends JButton {

  private static final Icon closeIcon = AllIcons.Actions.Close;
  private final String title;

  public CloseableTabButton(String title) {
    super(closeIcon);
    this.title = title;
    setBorder(BorderFactory.createEmptyBorder());
    setContentAreaFilled(false);
    setPreferredSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));
    setToolTipText("Close");
    setRolloverIcon(AllIcons.Actions.CloseHovered);
  }

  public JPanel getComponent() {
    var panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);

    var constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 1;
    panel.add(new JLabel(title), constraints);

    constraints.gridx++;
    constraints.weightx = 0;
    constraints.insets = JBUI.insetsLeft(8);
    panel.add(this, constraints);
    return panel;
  }
}
