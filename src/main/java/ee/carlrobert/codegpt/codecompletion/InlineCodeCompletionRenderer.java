package ee.carlrobert.codegpt.codecompletion;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import ee.carlrobert.codegpt.actions.editor.InsertInlineTextAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

public class InlineCodeCompletionRenderer implements EditorCustomElementRenderer, DocumentListener, KeyListener, CaretListener, EditorMouseListener, SelectionListener {
    public static final Key<Inlay<InlineCodeCompletionRenderer>> INLAY_KEY = Key.create("codegpt.inlay");
    public static final String ACTION_ID = "InsertInlineTextAction";
    public static final String INLINE_TEXT = "testing test";
    private Timer typingTimer;
    private final Editor editor;

    public InlineCodeCompletionRenderer(Editor editor) {
        this.editor = editor;
        ((EditorEx) editor).getDocument().addDocumentListener(this);
        editor.getContentComponent().addKeyListener(this);
        editor.addEditorMouseListener(this);
        editor.getCaretModel().addCaretListener(this);
        editor.getSelectionModel().addSelectionListener(this);
        initTypingTimer();
    }

    private void initTypingTimer() {
        typingTimer = new Timer(5000, e -> ApplicationManager.getApplication().invokeLater(() -> {
            if (editor != null && !editor.isDisposed()) {
                Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(INLAY_KEY);
                if (inlay == null) {
                    createInlay();

                    ActionManager actionManager = ActionManager.getInstance();
                    InsertInlineTextAction insertAction = new InsertInlineTextAction(INLAY_KEY, INLINE_TEXT);
                    actionManager.registerAction(ACTION_ID, insertAction);

                    Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
                    keymap.addShortcut(ACTION_ID, new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
                }
            }
        }));
        typingTimer.setRepeats(true);
        typingTimer.start();
    }

    private void createInlay() {
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            int offset = editor.getCaretModel().getOffset();
            Inlay<InlineCodeCompletionRenderer> inlay = editor.getInlayModel().addInlineElement(offset, true, this);
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

    private void resetSuggestion() {
        typingTimer.restart();
        ActionManager actionManager = ActionManager.getInstance();
        actionManager.unregisterAction(ACTION_ID);
        removeInlay();
    }

    private void enableSuggestions() {
        if (!typingTimer.isRunning()) {
            typingTimer.start();
        }
    }

    private void disableSuggestions() {
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
        FontMetrics fontMetrics = editor.getContentComponent().getFontMetrics(editor.getColorsScheme().getFont(EditorFontType.PLAIN));
        return fontMetrics.stringWidth(INLINE_TEXT);
    }


    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics2D g, @NotNull Rectangle2D targetRegion, @NotNull TextAttributes textAttributes) {
        // Create custom TextAttributes for different font type or color
        TextAttributes customAttributes = new TextAttributes();
        customAttributes.setFontType(Font.ITALIC);
        customAttributes.setForegroundColor(JBColor.GRAY);

        // Apply the custom attributes to the Graphics object
        Font font = editor.getColorsScheme().getFont(EditorFontType.ITALIC).deriveFont(customAttributes.getFontType());
        g.setFont(font);
        g.setColor(customAttributes.getForegroundColor());

        // Calculate the ascent to render the text on the same line as the caret
        int ascent = editor.getAscent();

        // Draw the string with the custom attributes
        g.drawString(INLINE_TEXT, (int) targetRegion.getX(), (int) targetRegion.getY() + ascent);
    }

}
