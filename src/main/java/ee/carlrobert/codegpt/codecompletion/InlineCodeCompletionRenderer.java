package ee.carlrobert.codegpt.codecompletion;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.util.Key;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

public class InlineCodeCompletionRenderer implements EditorCustomElementRenderer, DocumentListener, KeyListener, EditorMouseListener {
    public static final Key<Inlay<InlineCodeCompletionRenderer>> INLAY_KEY = Key.create("codegpt.inlay");
    public static final String INLINE_TEXT = "testing test";
    private static final Logger LOG = Logger.getInstance(InlineCodeCompletionInitializer.class);
    private Timer typingTimer;
    private final Editor editor;

    public InlineCodeCompletionRenderer(Editor editor) {
        this.editor = editor;
        ((EditorEx) editor).getDocument().addDocumentListener(this);
        editor.getContentComponent().addKeyListener(this);
        editor.addEditorMouseListener(this);
        initTypingTimer();


        ActionManager actionManager = ActionManager.getInstance();
        String actionId = "InsertInlineTextAction";
        InsertInlineTextAction insertAction = new InsertInlineTextAction();
        actionManager.registerAction(actionId, insertAction);

        Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
        keymap.addShortcut(actionId, new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
    }

    private void initTypingTimer() {
        typingTimer = new Timer(5000, e -> ApplicationManager.getApplication().invokeLater(() -> {
            if (editor != null && !editor.isDisposed()) {
                Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(INLAY_KEY);
                if (inlay == null) {
                    createInlay();
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
        resetInlay();
    }

    private void resetInlay() {
        typingTimer.restart();
        removeInlay();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        resetInlay();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        resetInlay();
    }

    @Override
    public void keyReleased(KeyEvent e) {
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
        FontMetrics metrics = g.getFontMetrics(font);
        int ascent = editor.getAscent();

        // Draw the string with the custom attributes
        g.drawString(INLINE_TEXT, (int) targetRegion.getX(), (int) targetRegion.getY() + ascent);
    }

}

class InsertInlineTextAction extends EditorAction {
    public InsertInlineTextAction(
    ) {
        super(new EditorActionHandler() {
            @Override
            protected void doExecute(@NotNull Editor editor, Caret caret, DataContext dataContext) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(InlineCodeCompletionRenderer.INLAY_KEY);
                    if (inlay != null) {
                        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                            int offset = inlay.getOffset();
                            editor.getDocument().insertString(offset, InlineCodeCompletionRenderer.INLINE_TEXT);
                            editor.getCaretModel().moveToOffset(offset + InlineCodeCompletionRenderer.INLINE_TEXT.length());
                            Inlay<InlineCodeCompletionRenderer> currentInlay = editor.getUserData(InlineCodeCompletionRenderer.INLAY_KEY);
                            if (currentInlay != null) {
                                currentInlay.dispose();
                                editor.putUserData(InlineCodeCompletionRenderer.INLAY_KEY, null);
                            }
                        });
                    }
                });
            }
        });
    }
}
