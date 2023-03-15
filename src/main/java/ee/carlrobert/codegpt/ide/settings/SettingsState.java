package ee.carlrobert.codegpt.ide.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.client.BaseModel;
import java.net.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.ide.settings.SettingsState",
    storages = @Storage("CodeGPTSettings.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public String apiKey = "";
  public String accessToken = "";
  public String reverseProxyUrl = "";
  public BaseModel textCompletionBaseModel = BaseModel.DAVINCI;
  public BaseModel chatCompletionBaseModel = BaseModel.CHATGPT_3_5;
  public boolean isGPTOptionSelected = true;
  public boolean isChatGPTOptionSelected;
  public boolean isChatCompletionOptionSelected = true;
  public boolean isTextCompletionOptionSelected;
  public String proxyHost = "";
  public int proxyPort;
  public Proxy.Type proxyType = Proxy.Type.SOCKS;
  public boolean isProxyAuthSelected;
  public String proxyUsername;
  public String proxyPassword;

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
}
