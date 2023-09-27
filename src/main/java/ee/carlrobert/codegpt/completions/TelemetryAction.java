package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.TelemetryService;
import ee.carlrobert.codegpt.telemetry.core.service.TelemetryMessageBuilder.ActionMessage;

public enum TelemetryAction {

  COMPLETION("CodeGPT-Completion"),
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
