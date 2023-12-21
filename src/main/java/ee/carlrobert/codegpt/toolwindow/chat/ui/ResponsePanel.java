package ee.carlrobert.codegpt.toolwindow.chat.ui;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.ui.IconActionButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResponsePanel extends JPanel {

  private final Header header;
  private final Body body;

  public ResponsePanel() {
    super(new BorderLayout());
    header = new Header();
    body = new Body();
    add(header, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
  }

  public void enableActions() {
    header.enableActions(true);
  }

  public ResponsePanel withReloadAction(Runnable onReload) {
    header.addReloadAction(onReload);
    return this;
  }

  public ResponsePanel withDeleteAction(Runnable onDelete) {
    header.addDeleteAction(onDelete);
    return this;
  }

  public ResponsePanel addContent(JComponent content) {
    body.addContent(content);
    return this;
  }

  public void updateContent(JComponent content) {
    body.updateContent(content);
  }

  public JComponent getContent() {
    return body.getContent();
  }

  static class Header extends JPanel {

    private final JPanel iconsWrapper;

    Header() {
      super(new BorderLayout());
      setBorder(JBUI.Borders.empty(12, 8, 4, 8));
      add(getIconLabel(), BorderLayout.LINE_START);

      iconsWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
      iconsWrapper.setBackground(getBackground());
      add(iconsWrapper, BorderLayout.LINE_END);
    }

    public void enableActions(boolean enabled) {
      for (var iconButton : iconsWrapper.getComponents()) {
        iconButton.setEnabled(enabled);
      }
    }

    public void addReloadAction(Runnable onReload) {
      addIconActionButton(new IconActionButton(
          new AnAction(
              CodeGPTBundle.get("toolwindow.chat.response.action.reloadResponse.text"),
              CodeGPTBundle.get("toolwindow.chat.response.action.reloadResponse.description"),
              Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
              enableActions(false);
              onReload.run();
            }
          }));
    }

    public void addDeleteAction(Runnable onDelete) {
      addIconActionButton(new IconActionButton(
          new AnAction(
              CodeGPTBundle.get("toolwindow.chat.response.action.deleteResponse.text"),
              CodeGPTBundle.get("toolwindow.chat.response.action.deleteResponse.description"),
              Actions.GC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
              onDelete.run();
            }
          }));
    }

    private void addIconActionButton(IconActionButton iconActionButton) {
      if (iconsWrapper.getComponents() != null && iconsWrapper.getComponents().length > 0) {
        iconsWrapper.add(Box.createHorizontalStrut(8));
      }
      iconsWrapper.add(iconActionButton);
    }

    private JBLabel getIconLabel() {
      return new JBLabel(
          CodeGPTBundle.get("project.label"),
          Icons.Default,
          SwingConstants.LEADING)
          .setAllowAutoWrapping(true)
          .withFont(JBFont.label().asBold());
    }
  }

  static class Body extends JPanel {

    private @Nullable JComponent content;

    Body() {
      super(new BorderLayout());
      setBorder(JBUI.Borders.empty(4, 8, 8, 8));
    }

    public void addContent(JComponent content) {
      this.content = content;
      add(content);
    }

    public void updateContent(JComponent content) {
      removeAll();
      revalidate();
      repaint();
      addContent(content);
    }

    public @Nullable JComponent getContent() {
      return content;
    }
  }
}
