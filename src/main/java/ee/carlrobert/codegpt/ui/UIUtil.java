package ee.carlrobert.codegpt.ui;

import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.toolwindow.chat.ui.SmartScroller;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class UIUtil {

  public static JTextPane createTextPane(String text) {
    return createTextPane(text, true);
  }

  public static JTextPane createTextPane(String text, boolean opaque) {
    return createTextPane(text, opaque, UIUtil::handleHyperlinkClicked);
  }

  public static JTextPane createTextPane(String text, boolean opaque, HyperlinkListener listener) {
    var textPane = new JTextPane();
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.addHyperlinkListener(listener);
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    textPane.setText(text);
    textPane.setOpaque(opaque);
    return textPane;
  }

  public static JBTextArea createTextArea(String initialValue) {
    var textArea = new JBTextArea(initialValue);
    textArea.setRows(3);
    textArea.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border()),
        JBUI.Borders.empty(4)));
    textArea.setLineWrap(true);
    return textArea;
  }

  public static JButton createIconButton(Icon icon) {
    var button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    return button;
  }

  public static JScrollPane createScrollPaneWithSmartScroller(ScrollablePanel scrollablePanel) {
    var scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    new SmartScroller(scrollPane);
    return scrollPane;
  }

  public static void setEqualLabelWidths(JPanel firstPanel, JPanel secondPanel) {
    var firstLabel = firstPanel.getComponents()[0];
    var secondLabel = secondPanel.getComponents()[0];
    if (firstLabel instanceof JLabel && secondLabel instanceof JLabel) {
      firstLabel.setPreferredSize(secondLabel.getPreferredSize());
    }
  }

  public static JPanel createPanel(JComponent component, String label, boolean resizeX) {
    return UI.PanelFactory.panel(component)
        .withLabel(label)
        .resizeX(resizeX)
        .createPanel();
  }

  public static void handleHyperlinkClicked(HyperlinkEvent event) {
    var url = event.getURL();
    if (ACTIVATED.equals(event.getEventType()) && url != null) {
      try {
        BrowserUtil.browse(url.toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void addShiftEnterInputMap(JTextArea textArea, AbstractAction onSubmit) {
    textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
    textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "text-submit");
    textArea.getActionMap().put("text-submit", onSubmit);
  }

  public static JPanel createRadioButtonsPanel(List<JBRadioButton> radioButtons) {
    var buttonGroup = new ButtonGroup();
    var radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    for (int i = 0; i < radioButtons.size(); i++) {
      JBRadioButton radioButton = radioButtons.get(i);
      buttonGroup.add(radioButton);
      radioPanel.add(radioButton);
      radioPanel.add(Box.createVerticalStrut(i == radioButtons.size() - 1 ? 8 : 4));
    }
    return radioPanel;
  }

  public static JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  public static JPanel withEmptyLeftBorder(JPanel panel) {
    return (JPanel) withEmptyLeftBorder((JComponent) panel);
  }

  public static JLabel createComment(String messageKey) {
    var comment = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get(messageKey), true);
    comment.setBorder(JBUI.Borders.empty(0, 4));
    return comment;
  }

  public static JPanel createForm(JBRadioButton radio1, JComponent layout1, JBRadioButton radio2,
      JComponent layout2,
      boolean isFirstLayoutInitial) {
    String firstComponentName = "1st";
    String secondComponentName = "2nd";
    return createForm(Map.of(
        firstComponentName, new RadioButtonWithLayout(radio1,
            layout1),
        secondComponentName, new RadioButtonWithLayout(radio2,
            layout2)), isFirstLayoutInitial ? firstComponentName : secondComponentName);
  }

  public static JPanel createForm(Map<String, RadioButtonWithLayout> layouts,
      String initialLayout) {
    JPanel finalPanel = new JPanel(new BorderLayout());
    finalPanel.add(createRadioButtonsPanel(
            layouts.entrySet().stream().sorted(Entry.comparingByKey())
                .map(entry -> entry.getValue().getRadioButton()).collect(Collectors.toList())),
        BorderLayout.NORTH);
    finalPanel.add(createRadioButtonGroupLayouts(layouts, initialLayout), BorderLayout.CENTER);
    return finalPanel;
  }

  public static PanelBuilder createSelectLayoutPanelBuilder(JBRadioButton radio1,
      JComponent layout1, JBRadioButton radio2, JComponent layout2,
      boolean isFirstInitiallySelected) {
    UIUtil.createSelectLayoutPanelBuilder(List.of(
        new RadioButtonWithLayout(radio1, layout1),
        new RadioButtonWithLayout(radio2, layout2)
    ), isFirstInitiallySelected ? radio1 : radio2);
    return UI.PanelFactory
        .panel(createForm(radio1, layout1, radio2, layout2, isFirstInitiallySelected));
  }

  private static void createSelectLayoutPanelBuilder(List<RadioButtonWithLayout> entries,
      JBRadioButton initiallySelected) {
    var buttonGroup = new ButtonGroup();
    entries.forEach(entry -> buttonGroup.add(entry.getRadioButton()));
    entries.forEach(entry -> {
          JBRadioButton radioButton = entry.getRadioButton();
          entry.getComponent().setVisible(radioButton.equals(initiallySelected));
          radioButton.addActionListener((e) -> {
            for (RadioButtonWithLayout innerEntry : entries) {
              innerEntry.getComponent().setVisible(innerEntry.equals(entry));
            }
          });
        }
    );
  }

  public static ComponentPanelBuilder createApiKeyPanel(String initialApiKey,
      JBPasswordField apiKeyField, String commentBundleKey) {
    apiKeyField.setColumns(30);
    apiKeyField.setText(initialApiKey);
    return UI.PanelFactory.panel(apiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get(commentBundleKey))
        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked);
  }

  public static ComponentPanelBuilder createApiKeyPanel(String initialApiKey,
      JBPasswordField apiKeyField) {
    return createApiKeyPanel(initialApiKey, apiKeyField,
        "settingsConfigurable.shared.apiKey.comment");
  }

  public static TextFieldWithBrowseButton createTextFieldWithBrowseButton(
      FileChooserDescriptor fileChooserDescriptor) {
    var browseButton = new TextFieldWithBrowseButton();
    browseButton.setEnabled(true);
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    fileChooserDescriptor.setHideIgnored(false);
    browseButton.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor));
    return browseButton;
  }


  /**
   * Creates RadioButton group to toggle between different layouts.
   *
   * @param layouts       Map from layout name to RadioButton + Layout to be shown
   * @param initialLayout Key of {@code layouts} entry to be initially shown
   * @return Panel with the RadioButton group
   */
  private static JPanel createRadioButtonGroupLayouts(Map<String, RadioButtonWithLayout> layouts,
      String initialLayout) {
    AutoSizeCardLayout cardlayout = new AutoSizeCardLayout();

    var formPanelCards = new JPanel(cardlayout);
    formPanelCards.setBorder(JBUI.Borders.emptyLeft(16));

    for (Entry<String, RadioButtonWithLayout> layout : layouts.entrySet()) {
      RadioButtonWithLayout value = layout.getValue();
      Component component = value.getComponent();
      String key = layout.getKey();
      component.setName(key);
      formPanelCards.add(component, key);
      value.getRadioButton().addActionListener(e -> cardlayout.show(formPanelCards, key));
    }

    cardlayout.show(formPanelCards, initialLayout);
    return formPanelCards;
  }

  public static class RadioButtonWithLayout {
    private final JBRadioButton radioButton;
    private final JComponent layout;

    public RadioButtonWithLayout(JBRadioButton radioButton, JComponent layout) {
      this.radioButton = radioButton;
      this.layout = layout;
    }

    public JBRadioButton getRadioButton() {
      return radioButton;
    }

    public JComponent getComponent() {
      return layout;
    }
  }


  /**
   * {@link CardLayout} that automatically resizes its size according to the shown component.<br/>
   * Source: <a href="https://stackoverflow.com/a/8286694/9748566">StackOverflow</a>
   */
  public static class AutoSizeCardLayout extends CardLayout {

    @Override
    public Dimension preferredLayoutSize(Container parent) {

      Component current = findCurrentComponent(parent);
      if (current != null) {
        Insets insets = parent.getInsets();
        Dimension pref = current.getPreferredSize();
        pref.width += insets.left + insets.right;
        pref.height += insets.top + insets.bottom;
        return pref;
      }
      return super.preferredLayoutSize(parent);
    }

    public Component findCurrentComponent(Container parent) {
      for (Component comp : parent.getComponents()) {
        if (comp.isVisible()) {
          return comp;
        }
      }
      return null;
    }

  }


}

