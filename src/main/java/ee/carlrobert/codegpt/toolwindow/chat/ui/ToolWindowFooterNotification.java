package ee.carlrobert.codegpt.toolwindow.chat.ui;

import com.intellij.icons.AllIcons.General;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.CurrentTheme.NotificationInfo;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ToolWindowFooterNotification extends JPanel {

  private final JBLabel label;

  public ToolWindowFooterNotification(Runnable onRemove) {
    this("", onRemove);
  }

  public ToolWindowFooterNotification(String text, Runnable onRemove) {
    super(new BorderLayout());
    this.label = new JBLabel(text, General.BalloonInformation, SwingConstants.LEADING);

    setVisible(false);
    setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0),
        JBUI.Borders.empty(8, 12)));

    setBackground(NotificationInfo.backgroundColor());
    setForeground(NotificationInfo.foregroundColor());
    add(label, BorderLayout.LINE_START);
    add(new ActionLink("Remove", (event) -> {
      hideNotification();
      onRemove.run();
    }), BorderLayout.LINE_END);
  }

  public void show(String text, String toolTipText) {
    label.setText(text);
    label.setToolTipText(toolTipText);
    setVisible(true);
  }

  public void hideNotification() {
    label.setText("");
    label.setToolTipText(null);
    setVisible(false);
  }
}
