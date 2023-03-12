package ee.carlrobert.codegpt.ide.toolwindow.components;

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;

public class ScrollPane extends JBScrollPane {

  public ScrollPane(ScrollablePanel scrollablePanel) {
    super(scrollablePanel);
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    setBorder(JBUI.Borders.empty(0, 8));
    setViewportBorder(null);
  }

  public void scrollToBottom() {
    JScrollBar verticalBar = getVerticalScrollBar();
    verticalBar.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent e) {
        Adjustable adjustable = e.getAdjustable();
        adjustable.setValue(adjustable.getMaximum());
        verticalBar.removeAdjustmentListener(this);
      }
    });
  }
}
