package ee.carlrobert.codegpt.toolwindow;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import java.util.NoSuchElementException;
import javax.swing.SwingConstants;

public class ModelIconLabel extends JBLabel {

  public ModelIconLabel(String clientCode, String modelCode) {
    if ("you.chat.completion".equals(clientCode)) {
      setIcon(Icons.YouIcon);
      return;
    }

    if ("chat.completion".equals(clientCode)) {
      setIcon(Icons.OpenAIIcon);
    }
    if ("azure.chat.completion".equals(clientCode)) {
      setIcon(Icons.AzureIcon);
    }
    if ("llama.chat.completion".equals(clientCode)) {
      setIcon(Icons.LlamaIcon);
    }
    setText(formatModelName(modelCode));
    setFont(JBFont.small().asBold());
    setHorizontalAlignment(SwingConstants.LEADING);
  }

  private String formatModelName(String modelCode) {
    try {
      return OpenAIChatCompletionModel.findByCode(modelCode).getDescription();
    } catch (NoSuchElementException e) {
      return modelCode;
    }
  }
}
