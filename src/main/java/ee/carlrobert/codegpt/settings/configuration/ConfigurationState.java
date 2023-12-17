package ee.carlrobert.codegpt.settings.configuration;

import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_COMMIT_MESSAGE_PROMPT;
import static ee.carlrobert.codegpt.completions.CompletionRequestProvider.COMPLETION_SYSTEM_PROMPT;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "CodeGPT_ConfigurationSettings_210",
    storages = @Storage("CodeGPT_ConfigurationSettings_210.xml"))
public class ConfigurationState implements PersistentStateComponent<ConfigurationState> {

  private String systemPrompt = COMPLETION_SYSTEM_PROMPT;
  private String commitMessagePrompt = COMPLETION_COMMIT_MESSAGE_PROMPT;
  private int maxTokens = 1000;
  private double temperature = 0.1;
  private int topK = 40;
  private double topP = 0.9;
  private double minP = 0.05;
  private double repeatPenalty = 1.1;
  private boolean checkForPluginUpdates = true;
  private boolean createNewChatOnEachAction;
  private boolean ignoreGitCommitTokenLimit;
  private boolean methodNameGenerationEnabled = true;
  private boolean autoFormattingEnabled = true;
  private Map<String, String> tableData = EditorActionsUtil.DEFAULT_ACTIONS;

  public static ConfigurationState getInstance() {
    return ApplicationManager.getApplication().getService(ConfigurationState.class);
  }

  @Nullable
  @Override
  public ConfigurationState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ConfigurationState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getSystemPrompt() {
    return systemPrompt;
  }

  public String getCommitMessagePrompt() {
    return commitMessagePrompt;
  }

  public void setSystemPrompt(String systemPrompt) {
    this.systemPrompt = systemPrompt;
  }

  public void setCommitMessagePrompt(String commitMessagePrompt) {
    this.commitMessagePrompt = commitMessagePrompt;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(int maxTokens) {
    this.maxTokens = maxTokens;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public int getTopK() {
    return topK;
  }

  public void setTopK(int topK) {
    this.topK = topK;
  }

  public double getTopP() {
    return topP;
  }

  public void setTopP(double topP) {
    this.topP = topP;
  }

  public double getMinP() {
    return minP;
  }

  public void setMinP(double minP) {
    this.minP = minP;
  }

  public double getRepeatPenalty() {
    return repeatPenalty;
  }

  public void setRepeatPenalty(double repeatPenalty) {
    this.repeatPenalty = repeatPenalty;
  }

  public boolean isCreateNewChatOnEachAction() {
    return createNewChatOnEachAction;
  }

  public void setCreateNewChatOnEachAction(boolean createNewChatOnEachAction) {
    this.createNewChatOnEachAction = createNewChatOnEachAction;
  }

  public Map<String, String> getTableData() {
    return tableData;
  }

  public void setTableData(Map<String, String> tableData) {
    this.tableData = tableData;
  }

  public boolean isCheckForPluginUpdates() {
    return checkForPluginUpdates;
  }

  public void setCheckForPluginUpdates(boolean checkForPluginUpdates) {
    this.checkForPluginUpdates = checkForPluginUpdates;
  }

  public boolean isIgnoreGitCommitTokenLimit() {
    return ignoreGitCommitTokenLimit;
  }

  public void setIgnoreGitCommitTokenLimit(boolean ignoreGitCommitTokenLimit) {
    this.ignoreGitCommitTokenLimit = ignoreGitCommitTokenLimit;
  }

  public boolean isMethodRefactoringEnabled() {
    return methodNameGenerationEnabled;
  }

  public void setMethodNameGenerationEnabled(boolean methodNameGenerationEnabled) {
    this.methodNameGenerationEnabled = methodNameGenerationEnabled;
  }

  public boolean isAutoFormattingEnabled() {
    return autoFormattingEnabled;
  }

  public void setAutoFormattingEnabled(boolean autoFormattingEnabled) {
    this.autoFormattingEnabled = autoFormattingEnabled;
  }
}
