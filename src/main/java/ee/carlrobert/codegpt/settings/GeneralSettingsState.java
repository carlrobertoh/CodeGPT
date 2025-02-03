package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.settings.service.ProviderChangeNotifier;
import ee.carlrobert.codegpt.settings.service.ServiceType;

public class GeneralSettingsState {

  private String displayName = "";
  private String avatarBase64 = "";
  private ServiceType selectedService = ServiceType.CODEGPT;

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

  public String getAvatarBase64() {
    return avatarBase64;
  }

  public void setAvatarBase64(String avatarBase64) {
    this.avatarBase64 = avatarBase64;
  }

  public ServiceType getSelectedService() {
    return selectedService;
  }

  public void setSelectedService(ServiceType selectedService) {
    this.selectedService = selectedService;

    ApplicationManager.getApplication()
        .getMessageBus()
        .syncPublisher(ProviderChangeNotifier.getTOPIC())
        .providerChanged(selectedService);
  }
}
