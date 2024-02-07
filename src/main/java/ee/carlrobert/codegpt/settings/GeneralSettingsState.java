package ee.carlrobert.codegpt.settings;

import ee.carlrobert.codegpt.settings.service.ServiceType;

public class GeneralSettingsState {

  private String email = "";
  private String displayName = "";
  private ServiceType selectedService = ServiceType.OPENAI;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDisplayName() {
    if (displayName == null || displayName.isEmpty()) {
      var systemUserName = System.getProperty("user.name");
      if (systemUserName == null || systemUserName.isEmpty()) {
        return "User";
      }
      return systemUserName;
    }
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public ServiceType getSelectedService() {
    return selectedService;
  }

  public void setSelectedService(ServiceType selectedService) {
    this.selectedService = selectedService;
  }
}
