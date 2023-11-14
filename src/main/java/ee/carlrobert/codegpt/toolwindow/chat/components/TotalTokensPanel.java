package ee.carlrobert.codegpt.toolwindow.chat.components;

import static java.lang.String.format;

import com.intellij.icons.AllIcons.General;
import com.intellij.ui.components.JBLabel;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.toolwindow.chat.TokenDetails;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

public class TotalTokensPanel extends JPanel {

  private final EncodingManager encodingManager;
  private final TokenDetails tokenDetails;
  private final JBLabel label;

  public TotalTokensPanel(
      Conversation conversation,
      String userPrompt,
      @Nullable String highlightedText) {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
    this.encodingManager = EncodingManager.getInstance();
    this.tokenDetails = createTokenDetails(conversation, userPrompt, highlightedText);
    this.label = getLabel(tokenDetails);

    setOpaque(false);
    add(getContextHelpIcon(tokenDetails));
    add(Box.createHorizontalStrut(4));
    add(label);
  }

  public TokenDetails getTokenDetails() {
    return tokenDetails;
  }

  public void update() {
    update(tokenDetails.getTotal());
  }

  public void update(int total) {
    label.setText(getLabelHtml(total));
  }

  public void updateConversationTokens(Conversation conversation) {
    tokenDetails.setConversationTokens(encodingManager.countConversationTokens(conversation));
    update();
  }

  public void updateUserPromptTokens(String userPrompt) {
    tokenDetails.setUserPromptTokens(encodingManager.countTokens(userPrompt));
    update();
  }

  public void updateHighlightedTokens(String highlightedText) {
    tokenDetails.setHighlightedTokens(encodingManager.countTokens(highlightedText));
    update();
  }

  private TokenDetails createTokenDetails(
      Conversation conversation,
      String userPrompt,
      @Nullable String highlightedText) {
    var tokenDetails = new TokenDetails(encodingManager);
    tokenDetails.setConversationTokens(encodingManager.countConversationTokens(conversation));
    tokenDetails.setUserPromptTokens(encodingManager.countTokens(userPrompt));
    if (highlightedText != null) {
      tokenDetails.setHighlightedTokens(encodingManager.countTokens(highlightedText));
    }
    return tokenDetails;
  }

  private JBLabel getContextHelpIcon(TokenDetails tokenDetails) {
    var iconLabel = new JBLabel(General.ContextHelp);
    iconLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        var html = new LinkedHashMap<>(Map.of(
            "System Prompt", tokenDetails.getSystemPromptTokens(),
            "Conversation Tokens", tokenDetails.getConversationTokens(),
            "Input Tokens", tokenDetails.getUserPromptTokens(),
            "Highlighted Tokens", tokenDetails.getHighlightedTokens()))
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

  private JBLabel getLabel(TokenDetails tokenDetails) {
    return new JBLabel(getLabelHtml(tokenDetails.getTotal()));
  }
}
