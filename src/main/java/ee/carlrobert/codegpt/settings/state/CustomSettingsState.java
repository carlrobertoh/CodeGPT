package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.settings.CustomServicePreset;
import ee.carlrobert.codegpt.settings.CustomServicePresetDetails;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_CustomSettings", storages = @Storage("CodeGPT_CustomSettings.xml"))
public class CustomSettingsState implements PersistentStateComponent<CustomSettingsState> {

  private static final CustomServicePresetDetails defaultPresetDetails = CustomServicePreset.ANTHROPIC.getPresetDetails();

  private String url = defaultPresetDetails.getUrl();
  private String httpMethod = defaultPresetDetails.getHttpMethod();
  private Map<String, String> headers = new HashMap<>(defaultPresetDetails.getHeaders());
  private Map<String, String> queryParams = new HashMap<>(defaultPresetDetails.getQueryParams());
  private String bodyJson = defaultPresetDetails.getBodyJson();
  private String responseJson = defaultPresetDetails.getResponseJson();

  public static CustomSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(CustomSettingsState.class);
  }

  @Override
  public CustomSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull CustomSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  public void setQueryParams(Map<String, String> queryParams) {
    this.queryParams = queryParams;
  }

  public String getBodyJson() {
    return bodyJson;
  }

  public void setBodyJson(String bodyJson) {
    this.bodyJson = bodyJson;
  }

  public String getResponseJson() {
    return responseJson;
  }

  public void setResponseJson(String responseJson) {
    this.responseJson = responseJson;
  }
}