package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import ee.carlrobert.codegpt.actions.editor.InsertInlineTextAction;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionResponseEventListener;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static ee.carlrobert.codegpt.codecompletions.InlineCodeCompletionRenderer.ACTION_ID;
import static ee.carlrobert.codegpt.codecompletions.InlineCodeCompletionRenderer.INLAY_KEY;

public class InlineCodeCompletionResponseEventListener implements CompletionResponseEventListener {

    private final InlineCodeCompletionRenderer renderer;

    public InlineCodeCompletionResponseEventListener(InlineCodeCompletionRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void handleMessage(String message) {
        CompletionResponseEventListener.super.handleMessage(message);
    }

    @Override
    public void handleError(ErrorDetails error, Throwable ex) {
        CompletionResponseEventListener.super.handleError(error, ex);
        renderer.restartTimer();
    }

    @Override
    public void handleTokensExceeded(Conversation conversation, Message message) {
        CompletionResponseEventListener.super.handleTokensExceeded(conversation, message);
        renderer.restartTimer();
    }

    @Override
    public void handleCompleted(String fullMessage, CallParameters callParameters) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ServiceType service = SettingsState.getInstance().getSelectedService();
            String inlineCode = fullMessage.replace(service.getFillInTheMiddle().getEot(),"");
            renderer.createInlay(inlineCode);
            InsertInlineTextAction insertAction = new InsertInlineTextAction(INLAY_KEY, inlineCode);
            ActionManager.getInstance().registerAction(ACTION_ID, insertAction);
            Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
            keymap.addShortcut(ACTION_ID, new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), null));
            renderer.restartTimer();
        });
    }

    @Override
    public void handleSerpResults(List<YouSerpResult> results, Message message) {
        CompletionResponseEventListener.super.handleSerpResults(results, message);
    }
}
