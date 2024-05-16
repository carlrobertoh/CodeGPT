package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OLLAMA;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel.GPT_4_O;
import static ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel.GPT_4_VISION_PREVIEW;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.CurrentTheme.DragAndDrop;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.actions.AttachImageAction;
import ee.carlrobert.codegpt.completions.CompletionRequestHandler;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.ui.IconActionButton;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

public class UserPromptTextArea extends JPanel {

  private static final Logger LOG = Logger.getInstance(UserPromptTextArea.class);

  private static final JBColor BACKGROUND_COLOR = JBColor.namedColor(
      "Editor.SearchField.background", com.intellij.util.ui.UIUtil.getTextFieldBackground());

  private final AtomicReference<CompletionRequestHandler> requestHandlerRef =
      new AtomicReference<>();
  private final JBTextArea textArea;
  private final int textAreaRadius = 16;
  private final Project project;
  private final Consumer<String> onSubmit;
  private IconActionButton stopButton;
  private boolean submitEnabled = true;
  private boolean isDragActive = false;

  public UserPromptTextArea(Project project, Consumer<String> onSubmit,
      TotalTokensPanel totalTokensPanel) {
    super(new BorderLayout());
    this.project = project;
    this.onSubmit = onSubmit;

    textArea = new JBTextArea();
    textArea.getDocument().addDocumentListener(getDocumentAdapter(totalTokensPanel));
    textArea.setOpaque(false);
    textArea.setBackground(BACKGROUND_COLOR);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    resetEmptyText();
    textArea.setBorder(JBUI.Borders.empty(8, 4));
    UIUtil.addShiftEnterInputMap(textArea, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          handleSubmit();
        } finally {
          totalTokensPanel.updateUserPromptTokens("");
        }
      }
    });
    textArea.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
      }

      @Override
      public void focusLost(FocusEvent e) {
        UserPromptTextArea.super.paintBorder(UserPromptTextArea.super.getGraphics());
      }
    });
    updateFont();
    init();
  }

  private DocumentAdapter getDocumentAdapter(TotalTokensPanel totalTokensPanel) {
    return new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent event) {
        if (submitEnabled) {
          try {
            var document = event.getDocument();
            var text = document.getText(
                document.getStartPosition().getOffset(),
                document.getEndPosition().getOffset() - 1);
            totalTokensPanel.updateUserPromptTokens(text);
          } catch (BadLocationException ex) {
            LOG.error("Something went wrong while processing user input tokens", ex);
          }
        }
      }
    };
  }

  public String getText() {
    return textArea.getText().trim();
  }

  public void focus() {
    textArea.requestFocus();
    textArea.requestFocusInWindow();
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
    super.paintComponent(g);
  }

  @Override
  protected void paintBorder(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (isDragActive) {
      g2.setColor(DragAndDrop.BORDER_COLOR);
      g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
          0, new float[]{9}, 0));
    } else {
      g2.setColor(JBUI.CurrentTheme.ActionButton.focusedBorder());
      if (textArea.isFocusOwner()) {
        g2.setStroke(new BasicStroke(1.5F));
      }
    }
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, textAreaRadius, textAreaRadius);
  }

  @Override
  public Insets getInsets() {
    return JBUI.insets(6, 12, 6, 6);
  }

  public void setSubmitEnabled(boolean submitEnabled) {
    this.submitEnabled = submitEnabled;
    stopButton.setEnabled(!submitEnabled);
  }

  public void setRequestHandler(@NotNull CompletionRequestHandler handler) {
    requestHandlerRef.set(handler);
  }

  private void handleSubmit() {
    if (submitEnabled && !textArea.getText().isEmpty()) {
      // Replacing each newline with two newlines to ensure proper Markdown formatting
      var text = textArea.getText().replace("\n", "\n\n");
      onSubmit.accept(text.trim());
      textArea.setText("");
    }
  }

  private void init() {
    setOpaque(false);
    add(textArea, BorderLayout.CENTER);

    stopButton = new IconActionButton(
        new AnAction("Stop", "Stop current inference", AllIcons.Actions.Suspend) {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            var handler = requestHandlerRef.get();
            if (handler != null) {
              handler.cancel();
            }
          }
        });
    stopButton.setEnabled(false);

    var flowLayout = new FlowLayout(FlowLayout.RIGHT);
    flowLayout.setHgap(8);
    JPanel iconsPanel = new JPanel(flowLayout);
    iconsPanel.add(new IconActionButton(
        new AnAction("Send Message", "Send message", Icons.Send) {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            handleSubmit();
          }
        }));
    if (isImageActionSupported()) {
      iconsPanel.add(new IconActionButton(new AttachImageAction()));
      setDropTarget(new DropTarget() {
        @Override
        public synchronized void dragEnter(DropTargetDragEvent evt) {
          isDragActive = true;
          var t = evt.getTransferable();
          var isSupportedFile = false;
          try {
            List<File> files = (List<File>) t.getTransferData(
                DataFlavor.javaFileListFlavor);
            isSupportedFile = files.size() == 1
                && AttachImageAction.SUPPORTED_EXTENSIONS.contains(
                FilenameUtils.getExtension(files.get(0).getName().toLowerCase()));
          } catch (UnsupportedFlavorException | IOException | ClassCastException ex) {
            LOG.debug("Unable to get image file list:", ex);
          }
          if (isSupportedFile) {
            textArea.getEmptyText()
                .setText(CodeGPTBundle.get("toolwindow.chat.textArea.drag.allowed"));
            evt.acceptDrag(DnDConstants.ACTION_COPY);
          } else {
            textArea.getEmptyText()
                .setText(CodeGPTBundle.get("toolwindow.chat.textArea.drag.notAllowed"));
            evt.rejectDrag();
          }
          repaint();
        }

        @Override
        public synchronized void dragExit(DropTargetEvent dte) {
          isDragActive = false;
          resetEmptyText();
          repaint();
        }

        @Override
        public synchronized void drop(DropTargetDropEvent evt) {
          isDragActive = false;
          resetEmptyText();
          try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transferable = evt.getTransferable();
            List<File> files = (List<File>) transferable.getTransferData(
                DataFlavor.javaFileListFlavor);
            if (files.size() != 1) {
              return;
            }
            AttachImageAction.addImageAttachment(project, files.get(0).getAbsolutePath());
          } catch (UnsupportedFlavorException | IOException | ClassCastException ex) {
            LOG.error("Unable to drop image file:", ex);
          }
        }
      });
      textArea.getDropTarget().setActive(false);
    }
    iconsPanel.add(stopButton);
    add(iconsPanel, BorderLayout.EAST);
  }

  private void resetEmptyText() {
    textArea.getEmptyText().setText(CodeGPTBundle.get("toolwindow.chat.textArea.emptyText"));
  }

  private boolean isImageActionSupported() {
    var selectedService = GeneralSettings.getSelectedService();
    if (selectedService == ANTHROPIC || selectedService == OLLAMA) {
      return true;
    }

    var model = OpenAISettings.getCurrentState().getModel();
    return selectedService == OPENAI && (
        GPT_4_VISION_PREVIEW.getCode().equals(model) || GPT_4_O.getCode().equals(model));
  }

  private void updateFont() {
    if (Registry.is("ide.find.use.editor.font", false)) {
      textArea.setFont(EditorUtil.getEditorFont());
    } else {
      textArea.setFont(UIManager.getFont("TextField.font"));
    }
  }
}
