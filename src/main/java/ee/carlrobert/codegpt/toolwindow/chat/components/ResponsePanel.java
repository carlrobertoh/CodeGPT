package ee.carlrobert.codegpt.toolwindow.chat.components;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.toolwindow.IconActionButton;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.NoSuchElementException;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResponsePanel extends JPanel {

  private final Header header;
  private final Body body;
  private final Footer footer;

  public ResponsePanel() {
    super(new BorderLayout());
    header = new Header();
    body = new Body();
    footer = new Footer();
    add(header, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
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

  public ResponsePanel withModel(String clientCode, String modelCode) {
    if ("YouCode".equals(modelCode)) {
      footer.addContent(new JBLabel(Icons.YouIcon));
      return this;
    }

    var icon = "chat.completion".equals(clientCode) ? Icons.OpenAIIcon : Icons.AzureIcon;
    var modelLabel = new JBLabel(formatModelName(modelCode), icon, SwingConstants.LEADING)
        .withFont(JBFont.small().asBold());
    footer.addContent(modelLabel);
    return this;
  }

  private String formatModelName(String modelCode) {
    try {
      return OpenAIChatCompletionModel.findByCode(modelCode).getDescription();
    } catch (NoSuchElementException e) {
      return modelCode;
    }
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
      setBackground(getPanelBackgroundColor());
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
      addIconActionButton(new IconActionButton("Reload response", Actions.Refresh, new AnAction() {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
          enableActions(false);
          onReload.run();
        }
      }));
    }

    public void addDeleteAction(Runnable onDelete) {
      addIconActionButton(new IconActionButton("Delete response", Actions.GC, new AnAction() {
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
      return new JBLabel("CodeGPT", Icons.DefaultIcon, SwingConstants.LEADING)
          .setAllowAutoWrapping(true)
          .withFont(JBFont.label().asBold());
    }
  }

  static class Body extends JPanel {

    private @Nullable JComponent content;

    Body() {
      super(new BorderLayout());
      setBackground(getPanelBackgroundColor());
      setBorder(JBUI.Borders.empty(4, 8, 0, 8));
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

  static class Footer extends JPanel {

    Footer() {
      super(new BorderLayout());
      setBackground(getPanelBackgroundColor());
      setBorder(JBUI.Borders.compound(
          JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0),
          JBUI.Borders.empty(4, 8, 8, 10)));
    }

    public void addContent(JComponent content) {
      add(content, BorderLayout.EAST);
    }

    public void updateContent(JComponent content) {
      removeAll();
      addContent(content);
      revalidate();
      repaint();
    }
  }
}
