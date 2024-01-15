package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import ee.carlrobert.codegpt.actions.editor.InsertInlineTextAction;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.completions.ConversationType;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.service.FillInTheMiddle;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;

public class InlineCodeCompletionRenderer implements EditorCustomElementRenderer, DocumentListener,
    KeyListener, CaretListener, EditorMouseListener, SelectionListener {

  public static final Key<Inlay<InlineCodeCompletionRenderer>> INLAY_KEY = Key.create(
      "codegpt.inlay");
  public static final String ACTION_ID = "InsertInlineTextAction";
  public static final int MAX_OFFSET = 4000; // TODO: Add in settings? Always entire file?

  private Timer typingTimer;
  private String inlayText = "";
  private final Editor editor;
  private final ConversationService conversationService;
  private final Conversation conversation;


  public InlineCodeCompletionRenderer(Editor editor) {
    this.editor = editor;
    ((EditorEx) editor).getDocument().addDocumentListener(this);
    editor.getContentComponent().addKeyListener(this);
    editor.addEditorMouseListener(this);
    editor.getCaretModel().addCaretListener(this);
    editor.getSelectionModel().addSelectionListener(this);
    initTypingTimer();
    this.conversationService = ConversationService.getInstance();
    var completionCode =
        SettingsState.getInstance().getSelectedService().getCompletionCode() + ".inline";
    this.conversation = conversationService.createConversation(completionCode);
    conversationService.addConversation(this.conversation);
  }

  private void initTypingTimer() {
    typingTimer = new Timer(ConfigurationState.getInstance().getInlineDelay(),
        e -> ApplicationManager.getApplication().invokeLater(() -> {
          if (editor != null && !editor.isDisposed()) {
            Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(INLAY_KEY);
            if (inlay == null) {
              Document document = editor.getDocument();
              if (document.getText().isEmpty() || document.getText().isBlank()) {
                return;
              }
              int offset = editor.getCaretModel().getOffset();
              var message = createFimMessage(offset, document);
              typingTimer.stop();
              ApplicationManager.getApplication().executeOnPooledThread(() -> {
                var response = CompletionRequestService.getInstance()
                    .getCodeCompletion(new CallParameters(conversation,
                        ConversationType.INLINE_COMPLETION, message, false));
                if (response != null) {
                  var inlineCode = response.replace(
                      SettingsState.getInstance().getSelectedService().getFillInTheMiddle()
                          .getEot(), "");
                  var insertAction = new InsertInlineTextAction(INLAY_KEY, inlineCode,
                      InlineCodeCompletionRenderer.this::disableSuggestions);
                  ActionManager.getInstance().registerAction(ACTION_ID, insertAction);
                  var keymap = KeymapManager.getInstance().getActiveKeymap();
                  keymap.addShortcut(ACTION_ID,
                      new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
                  ApplicationManager.getApplication()
                      .invokeLater(() -> createInlay(inlineCode));
                }
                restartTimer();
              });
            }
          }
        }));
    typingTimer.setRepeats(true);
    typingTimer.start();
  }

  private void createInlay(String inlineCode) {
    inlayText = inlineCode;
    WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
      int offset = editor.getCaretModel().getOffset();
      Inlay<InlineCodeCompletionRenderer> inlay = editor.getInlayModel()
          .addInlineElement(offset, true, this);
      editor.putUserData(INLAY_KEY, inlay);
    });
  }

  private void removeInlay() {
    Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(INLAY_KEY);
    if (inlay != null) {
      WriteCommandAction.runWriteCommandAction(editor.getProject(), inlay::dispose);
      editor.putUserData(INLAY_KEY, null);
    }
  }

  @Override
  public void beforeDocumentChange(@NotNull DocumentEvent event) {
    resetSuggestion();
  }

  void restartTimer() {
    typingTimer.restart();
  }

  private void resetSuggestion() {
    restartTimer();
    ActionManager actionManager = ActionManager.getInstance();
    actionManager.unregisterAction(ACTION_ID);
    removeInlay();
  }

  private void enableSuggestions() {
    if (!typingTimer.isRunning()) {
      typingTimer.start();
    }
  }

  void disableSuggestions() {
    resetSuggestion();
    typingTimer.stop();
  }

  @Override
  public void keyTyped(KeyEvent e) {
    resetSuggestion();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    resetSuggestion();
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void caretPositionChanged(@NotNull CaretEvent event) {
    resetSuggestion();
  }

  @Override
  public void selectionChanged(@NotNull SelectionEvent e) {
    TextRange range = e.getNewRange();
    int rangeLength = range.getLength();
    if (rangeLength != 0) {
      disableSuggestions();
    } else {
      enableSuggestions();
    }
  }

  @Override
  public int calcWidthInPixels(@NotNull Inlay inlay) {
    FontMetrics fontMetrics = editor.getContentComponent()
        .getFontMetrics(editor.getColorsScheme().getFont(EditorFontType.PLAIN));
    return fontMetrics.stringWidth(inlayText);
  }


  @Override
  public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion,
      @NotNull TextAttributes textAttributes) {
    // Create custom TextAttributes for different font type or color
    TextAttributes customAttributes = new TextAttributes();
    customAttributes.setFontType(Font.ITALIC);
    customAttributes.setForegroundColor(JBColor.GRAY);

    // Apply the custom attributes to the Graphics object
    Font font = editor.getColorsScheme().getFont(EditorFontType.ITALIC)
        .deriveFont(customAttributes.getFontType());
    g.setFont(font);
    g.setColor(customAttributes.getForegroundColor());

    // Calculate the ascent to render the text on the same line as the caret
    int ascent = editor.getAscent();

    // Draw the string with the custom attributes
    g.drawString(inlayText, (int) targetRegion.getX(), (int) targetRegion.getY() + ascent);
  }

  public String getInlayText() {
    return inlayText;
  }

  private static Message createFimMessage(int offset, Document document) {
    int begin = Integer.max(0, offset - MAX_OFFSET);
    int end = Integer.min(document.getTextLength(), offset + MAX_OFFSET);
    var before = document.getText(new TextRange(begin, offset));
    var after = document.getText(new TextRange(offset, end));
    FillInTheMiddle fim = SettingsState.getInstance().getSelectedService().getFillInTheMiddle();
    return new Message(
        ConfigurationState.getInstance().getInlineCompletionPrompt()
            .replace("{pre}", fim.getPrefix())
            .replace("{codeBefore}", before)
            .replace("{suf}", fim.getSuffix())
            .replace("{codeAfter}", after)
            .replace("{mid}", fim.getMiddle()));
  }
}
