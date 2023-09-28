package ee.carlrobert.codegpt;

import ee.carlrobert.codegpt.telemetry.core.service.TelemetryMessageBuilder.ActionMessage;

public enum TelemetryAction {

  COMPLETION("CodeGPT-Completion"),
  COMPLETION_ERROR("CodeGPT-Completion-Error"),
  IDE_ACTION("CodeGPT-Action");

  private final String code;

  TelemetryAction(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static ActionMessage createActionMessage(TelemetryAction action) {
    return TelemetryService.instance().action(action.getCode());
  }
}
