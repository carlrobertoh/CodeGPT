package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import static java.lang.String.format;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.Conversation;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TotalTokensPanel extends JPanel {

  private final EncodingManager encodingManager;
  private final TotalTokensDetails totalTokensDetails;
  private final JBLabel label;

  public TotalTokensPanel(
      Conversation conversation,
      @Nullable String highlightedText,
      Disposable parentDisposable) {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
    setBorder(JBUI.Borders.empty(4));
    this.encodingManager = EncodingManager.getInstance();
    this.totalTokensDetails = createTokenDetails(conversation, highlightedText);
    this.label = getLabel(totalTokensDetails);

    setOpaque(false);
    add(getContextHelpIcon(totalTokensDetails));
    add(Box.createHorizontalStrut(4));
    add(label);
    addSelectionListeners(parentDisposable);
  }

  private void addSelectionListeners(Disposable parentDisposable) {
    var editorFactory = EditorFactory.getInstance();
    for (var editor : editorFactory.getAllEditors()) {
      editor.getSelectionModel().addSelectionListener(getSelectionListener());
    }
    editorFactory.addEditorFactoryListener(new EditorFactoryListener() {
      @Override
      public void editorCreated(@NotNull EditorFactoryEvent event) {
        event.getEditor().getSelectionModel().addSelectionListener(getSelectionListener());
      }
    }, parentDisposable);
  }

  private SelectionListener getSelectionListener() {
    return new SelectionListener() {
      @Override
      public void selectionChanged(@NotNull SelectionEvent e) {
        updateHighlightedTokens(e.getEditor().getDocument().getText(e.getNewRange()));
      }
    };
  }

  public TotalTokensDetails getTokenDetails() {
    return totalTokensDetails;
  }

  public void update() {
    update(totalTokensDetails.getTotal());
  }

  public void update(int total) {
    label.setText(getLabelHtml(total));
  }

  public void updateConversationTokens(int total) {
    totalTokensDetails.setConversationTokens(total);
    update();
  }

  public void updateConversationTokens(Conversation conversation) {
    updateConversationTokens(encodingManager.countConversationTokens(conversation));
  }

  public void updateUserPromptTokens(String userPrompt) {
    totalTokensDetails.setUserPromptTokens(encodingManager.countTokens(userPrompt));
    update();
  }

  public void updateHighlightedTokens(String highlightedText) {
    totalTokensDetails.setHighlightedTokens(encodingManager.countTokens(highlightedText));
    update();
  }

  private TotalTokensDetails createTokenDetails(
      Conversation conversation,
      @Nullable String highlightedText) {
    var tokenDetails = new TotalTokensDetails(encodingManager);
    tokenDetails.setConversationTokens(encodingManager.countConversationTokens(conversation));
    if (highlightedText != null) {
      tokenDetails.setHighlightedTokens(encodingManager.countTokens(highlightedText));
    }
    return tokenDetails;
  }

  private JBLabel getContextHelpIcon(TotalTokensDetails totalTokensDetails) {
    var iconLabel = new JBLabel(General.ContextHelp);
    iconLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        var html = new LinkedHashMap<>(Map.of(
            "System Prompt", totalTokensDetails.getSystemPromptTokens(),
            "Conversation Tokens", totalTokensDetails.getConversationTokens(),
            "Input Tokens", totalTokensDetails.getUserPromptTokens(),
            "Highlighted Tokens", totalTokensDetails.getHighlightedTokens()))
            .entrySet().stream()
            .map(entry -> format(
                "<p style=\"margin: 0;\"><small>%s: <strong>%d</strong></small></p>",
                entry.getKey(),
                entry.getValue()))
            .collect(Collectors.joining());
        iconLabel.setToolTipText("<html>" + html + "</html>");
      }
    });
    return iconLabel;
  }

  private String getLabelHtml(int total) {
    return format("<html><small>Total Tokens: <strong>%d</strong></small></html>", total);
  }

  private JBLabel getLabel(TotalTokensDetails totalTokensDetails) {
    return new JBLabel(getLabelHtml(totalTokensDetails.getTotal()));
  }
}
