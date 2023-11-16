package ee.carlrobert.codegpt.toolwindow.chat.components;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class SmartScroller implements AdjustmentListener {

  private static final int HORIZONTAL = 0;
  private static final int VERTICAL = 1;
  private static final int START = 0;
  private static final int END = 1;

  private final int viewportPosition;

  private boolean adjustScrollBar = true;
  private int previousValue = -1;
  private int previousMaximum = -1;

  /**
   * Convenience constructor. Scroll direction is VERTICAL and viewport position is at the END.
   *
   * @param scrollPane the scroll pane to monitor
   */
  public SmartScroller(JScrollPane scrollPane) {
    this(scrollPane, VERTICAL, END);
  }


  /**
   * Specify how the SmartScroller will function.
   *
   * @param scrollPane       the scroll pane to monitor
   * @param scrollDirection  indicates which JScrollBar to monitor. Valid values are HORIZONTAL and
   *                         VERTICAL.
   * @param viewportPosition indicates where the viewport will normally be positioned as data is
   *                         added. Valid values are START and END
   */
  public SmartScroller(JScrollPane scrollPane, int scrollDirection, int viewportPosition) {
    if (scrollDirection != HORIZONTAL
        && scrollDirection != VERTICAL) {
      throw new IllegalArgumentException("invalid scroll direction specified");
    }

    if (viewportPosition != START
        && viewportPosition != END) {
      throw new IllegalArgumentException("invalid viewport position specified");
    }

    this.viewportPosition = viewportPosition;

    JScrollBar scrollBar;
    if (scrollDirection == HORIZONTAL) {
      scrollBar = scrollPane.getHorizontalScrollBar();
    } else {
      scrollBar = scrollPane.getVerticalScrollBar();
    }

    scrollBar.addAdjustmentListener(this);

    //  Turn off automatic scrolling for text components

    Component view = scrollPane.getViewport().getView();

    if (view instanceof JTextComponent) {
      JTextComponent textComponent = (JTextComponent) view;
      DefaultCaret caret = (DefaultCaret) textComponent.getCaret();
      caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }
  }

  @Override
  public void adjustmentValueChanged(final AdjustmentEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        checkScrollBar(e);
      }
    });
  }

  /*
   *  Analyze every adjustment event to determine when the viewport
   *  needs to be repositioned.
   */
  private void checkScrollBar(AdjustmentEvent e) {
    //  The scroll bar listModel contains information needed to determine
    //  whether the viewport should be repositioned or not.

    JScrollBar scrollBar = (JScrollBar) e.getSource();
    BoundedRangeModel listModel = scrollBar.getModel();
    int value = listModel.getValue();
    int extent = listModel.getExtent();
    int maximum = listModel.getMaximum();

    boolean valueChanged = previousValue != value;
    boolean maximumChanged = previousMaximum != maximum;

    //  Check if the user has manually repositioned the scrollbar

    if (valueChanged && !maximumChanged) {
      if (viewportPosition == START) {
        adjustScrollBar = value != 0;
      } else {
        adjustScrollBar = value + extent >= maximum;
      }
    }

    //  Reset the "value" so we can reposition the viewport and
    //  distinguish between a user scroll and a program scroll.
    //  (ie. valueChanged will be false on a program scroll)

    if (adjustScrollBar && viewportPosition == END) {
      //  Scroll the viewport to the end.
      scrollBar.removeAdjustmentListener(this);
      value = maximum - extent;
      scrollBar.setValue(value);
      scrollBar.addAdjustmentListener(this);
    }

    if (adjustScrollBar && viewportPosition == START) {
      //  Keep the viewport at the same relative viewportPosition
      scrollBar.removeAdjustmentListener(this);
      value = value + maximum - previousMaximum;
      scrollBar.setValue(value);
      scrollBar.addAdjustmentListener(this);
    }

    previousValue = value;
    previousMaximum = maximum;
  }
}