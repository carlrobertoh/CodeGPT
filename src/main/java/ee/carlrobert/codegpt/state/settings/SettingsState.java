package ee.carlrobert.codegpt.state.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.state.settings.SettingsState",
    storages = @Storage("CodeGPTSettings_170.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public String apiKey = "";
  public String organization = "";
  public String displayName = getDisplayName();
  public boolean useOpenAIAccountName = true;
  public String textCompletionBaseModel = TextCompletionModel.DAVINCI.getCode();
  public String chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();
  public boolean isChatCompletionOptionSelected = true;
  public boolean isTextCompletionOptionSelected;

  public static SettingsState getInstance() {
    return ApplicationManager.getApplication().getService(SettingsState.class);
  }

  @Nullable
  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  private String getDisplayName() {
    var name = System.getProperty("user.name");
    return name == null || name.isEmpty() ? "User" : name;
  }
}
