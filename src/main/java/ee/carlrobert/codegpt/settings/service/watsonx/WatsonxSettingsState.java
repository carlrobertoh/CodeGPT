package ee.carlrobert.codegpt.settings.service.watsonx;

import java.util.Objects;

public class WatsonxSettingsState {

    private String onPremHost;
    private String username;
    private boolean isOnPrem = false;
    private boolean isZenApiKey = false;
    private String apiVersion = "2024-03-14";
    // use this model as default
    private String model = "ibm/granite-3b-code-instruct";
    private String spaceId;
    private String projectId;
    private boolean isGreedyDecoding = false;
    private boolean codeCompletionsEnabled = false;
    private Double temperature = 0.9;
    private Integer topK = 40;
    private Double topP = 0.9;
    private Integer maxNewTokens= 4000;
    private Integer minNewTokens = 0;
    private Boolean includeStopSequence = false;
    private String stopSequences = "";
    private Double repetitionPenalty = 1.1;
    private Integer randomSeed;

    public boolean isOnPrem() {
        return isOnPrem;
    }

    public void setOnPrem(boolean onPrem) {
        isOnPrem = onPrem;
    }

    public String getOnPremHost() {
        return onPremHost;
    }

    public void setOnPremHost(String onPremHost) {
        this.onPremHost = onPremHost;
    }

    public boolean isZenApiKey() {
        return isZenApiKey;
    }

    public void setZenApiKey(boolean zenApiKey) {
        isZenApiKey = zenApiKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiVersion() {
        return apiVersion;
    }
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getSpaceId() {return spaceId;}
    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getProjectId() {return projectId;}
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Boolean isGreedyDecoding() {
        return isGreedyDecoding;
    }
    public void setGreedyDecoding(boolean isGreedyDecoding) {
        this.isGreedyDecoding = isGreedyDecoding;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxNewTokens() {
        return maxNewTokens;
    }
    public void setMaxNewTokens(Integer maxNewTokens) {
        this.maxNewTokens = maxNewTokens;
    }
    public Integer getMinNewTokens() {
        return minNewTokens;
    }
    public void setMinNewTokens(Integer minNewTokens) {
        this.minNewTokens = minNewTokens;
    }

    public Double getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Double repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getTopK() {
        return topK;
    }
    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Boolean getIncludeStopSequence() {
        return includeStopSequence;
    }

    public void setIncludeStopSequence(Boolean includeStopSequence) {
        this.includeStopSequence = includeStopSequence;
    }

    public String getStopSequences() {
        return stopSequences;
    }
    public void setStopSequences(String stopSequences){
        this.stopSequences = stopSequences;
    }

    public Integer getRandomSeed() {
        return randomSeed;
    }
    public void setRandomSeed(Integer randomSeed){
        this.randomSeed=randomSeed;
    }

    public boolean isCodeCompletionsEnabled() {
        return codeCompletionsEnabled;
    }

    public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
        this.codeCompletionsEnabled = codeCompletionsEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettingsState that = (ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettingsState) o;
        return Objects.equals(apiVersion, that.apiVersion) && Objects.equals(spaceId, that.spaceId) && Objects.equals(projectId, that.projectId) && Objects.equals(model, that.model) && Objects.equals(temperature,that.temperature) && Objects.equals(topP,that.topP) && Objects.equals(topK,that.topK) && Objects.equals(randomSeed,that.randomSeed) && Objects.equals(repetitionPenalty,that.repetitionPenalty) && Objects.equals(maxNewTokens, that.maxNewTokens) && Objects.equals(minNewTokens,that.minNewTokens) && Objects.equals(isGreedyDecoding,that.isGreedyDecoding) && Objects.equals(isOnPrem,that.isOnPrem) && Objects.equals(isZenApiKey,that.isZenApiKey);

    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, model, apiVersion, projectId, spaceId,temperature,topP,topK,randomSeed,includeStopSequence,stopSequences,repetitionPenalty, maxNewTokens,minNewTokens,isGreedyDecoding,isOnPrem,isZenApiKey);
    }
}
