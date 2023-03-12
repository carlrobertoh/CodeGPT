package ee.carlrobert.codegpt.ide.toolwindow.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

// https://tips4java.wordpress.com/2009/09/27/component-border/
/**
 *  The ComponentBorder class allows you to place a real component in
 *  the space reserved for painting the Border of a component.
 *
 *  This class takes advantage of the knowledge that all Swing components are
 *  also Containers. By default the layout manager is null, so we should be
 *  able to place a child component anywhere in the parent component. In order
 *  to prevent the child component from painting over top of the parent
 *  component a Border is added to the parent componet such that the insets of
 *  the Border will reserve space for the child component to be painted without
 *  affecting the parent component.
 */
public class ComponentBorder implements Border
{
  public enum Edge
  {
    TOP,
    LEFT,
    BOTTOM,
    RIGHT;
  }

  public static final float LEADING  = 0.0f;
  public static final float CENTER   = 0.5f;
  public static final float TRAILING = 1.0f;

  private JComponent parent;
  private JComponent component;
  private Edge edge;
  private float alignment;
  private int gap = 5;
  private boolean adjustInsets = true;
  private Insets borderInsets = new Insets(0, 0, 0, 0);

  /**
   *  Convenience constructor that uses the default edge (Edge.RIGHT) and
   *  alignment (CENTER).
   *
   *  @param component the component to be added in the Border area
   */
  public ComponentBorder(JComponent component)
  {
    this(component,	Edge.RIGHT);
  }

  /**
   *  Convenience constructor that uses the default alignment (CENTER).
   *
   *  @param component the component to be added in the Border area
   *  @param edge a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
   */
  public ComponentBorder(JComponent component, Edge edge)
  {
    this(component, edge, CENTER);
  }

  /**
   *  Main constructor to create a ComponentBorder.
   *
   *  @param component the component to be added in the Border area
   *  @param edge  a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
   *  @param alignment the alignment of the component along the
   *                   specified Edge. Must be in the range 0 - 1.0.
   */
  public ComponentBorder(JComponent component, Edge edge, float alignment )
  {
    this.component = component;
    component.setSize( component.getPreferredSize() );
    component.setCursor(Cursor.getDefaultCursor());
    setEdge( edge );
    setAlignment( alignment );
  }

  public boolean isAdjustInsets()
  {
    return adjustInsets;
  }

  public void setAdjustInsets(boolean adjustInsets)
  {
    this.adjustInsets = adjustInsets;
  }

  /**
   *  Get the component alignment along the Border Edge
   *
   *  @return the alignment
   */
  public float getAlignment()
  {
    return alignment;
  }

  /**
   *  Set the component alignment along the Border Edge
   *
   *  @param alignment a value in the range 0 - 1.0. Standard values would be
   *	                 CENTER (default), LEFT and RIGHT.
   */
  public void setAlignment(float alignment)
  {
    this.alignment = alignment > 1.0f ? 1.0f : alignment < 0.0f ? 0.0f : alignment;
  }

  /**
   *  Get the Edge the component is positioned along
   *
   *  @return the Edge
   */
  public Edge getEdge()
  {
    return edge;
  }

  /**
   *	Set the Edge the component is positioned along
   *
   *  @param edge the Edge the component is position on.
   */
  public void setEdge(Edge edge)
  {
    this.edge = edge;
  }

  /**
   *  Get the gap between the border component and the parent component
   *
   *  @return the gap in pixels.
   */
  public int getGap()
  {
    return gap;
  }

  /**
   *  Set the gap between the border component and the parent component
   *
   *  @param gap the gap in pixels (default is 5)
   */
  public void setGap(int gap)
  {
    this.gap = gap;
  }

//
//  Implement the Border interface
//

  public Insets getBorderInsets(Component c)
  {
    return borderInsets;
  }

  public boolean isBorderOpaque()
  {
    return false;
  }

  /**
   *  In this case a real component is to be painted. Setting the location
   *  of the component will cause it to be painted at that location.
   */
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
  {
    float x2 = (width  - component.getWidth())  * component.getAlignmentX() + x;
    float y2 = (height - component.getHeight()) * component.getAlignmentY() + y;
    component.setLocation((int)x2, (int)y2);
  }

  /*
   *  Install this Border on the specified component by replacing the
   *  existing Border with a CompoundBorder containing the original Border
   *  and our ComponentBorder
   *
   *  This method should only be invoked once all the properties of this
   *  class have been set. Installing the Border more than once will cause
   *  unpredictable results.
   */
  public void install(JComponent parent)
  {
    this.parent = parent;

    determineInsetsAndAlignment();

    //  Add this Border to the parent

    Border current = parent.getBorder();

    if (current == null)
    {
      parent.setBorder(this);
    }
    else
    {
      CompoundBorder compound = new CompoundBorder(current, this);
      parent.setBorder(compound);
    }

    //  Add component to the parent

    parent.add(component);
  }

  /**
   *	The insets need to be determined so they are included in the preferred
   *  size of the component the Border is attached to.
   *
   *  The alignment of the component is determined here so it doesn't need
   *  to be recalculated every time the Border is painted.
   */
  private void determineInsetsAndAlignment()
  {
    borderInsets = new Insets(0, 0, 0, 0);

    //  The insets will only be updated for the edge the component will be
    //  diplayed on.
    //
    //  The X, Y alignment of the component is controlled by both the edge
    //  and alignment parameters

    if (edge == Edge.TOP)
    {
      borderInsets.top = component.getPreferredSize().height + gap;
      component.setAlignmentX(alignment);
      component.setAlignmentY(0.0f);
    }
    else if (edge == Edge.BOTTOM)
    {
      borderInsets.bottom = component.getPreferredSize().height + gap;
      component.setAlignmentX(alignment);
      component.setAlignmentY(1.0f);
    }
    else if (edge == Edge.LEFT)
    {
      borderInsets.left = component.getPreferredSize().width + gap;
      component.setAlignmentX(0.0f);
      component.setAlignmentY(alignment);
    }
    else if (edge == Edge.RIGHT)
    {
      borderInsets.right = component.getPreferredSize().width + gap;
      component.setAlignmentX(1.0f);
      component.setAlignmentY(alignment);
    }

    if (adjustInsets)
      adjustBorderInsets();
  }

  /*
   *  The complimentary edges of the Border may need to be adjusted to allow
   *  the component to fit completely in the bounds of the parent component.
   */
  private void adjustBorderInsets()
  {
    Insets parentInsets = parent.getInsets();

    //  May need to adust the height of the parent component to fit
    //  the component in the Border

    if (edge == Edge.RIGHT || edge == Edge.LEFT)
    {
      int parentHeight = parent.getPreferredSize().height - parentInsets.top - parentInsets.bottom;
      int diff = component.getHeight() - parentHeight;

      if (diff > 0)
      {
        int topDiff = (int)(diff * alignment);
        int bottomDiff = diff - topDiff;
        borderInsets.top += topDiff;
        borderInsets.bottom += bottomDiff;
      }
    }

    //  May need to adust the width of the parent component to fit
    //  the component in the Border

    if (edge == Edge.TOP || edge == Edge.BOTTOM)
    {
      int parentWidth = parent.getPreferredSize().width - parentInsets.left - parentInsets.right;
      int diff = component.getWidth() - parentWidth;

      if (diff > 0)
      {
        int leftDiff = (int)(diff * alignment);
        int rightDiff = diff - leftDiff;
        borderInsets.left += leftDiff;
        borderInsets.right += rightDiff;
      }
    }
  }
}
