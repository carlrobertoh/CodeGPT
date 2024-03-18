package ee.carlrobert.codegpt.settings;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.Persona;

import java.util.ArrayList;
import java.util.List;

public class GeneralSettingsState {

  private String displayName = "";
  private ServiceType selectedService = ServiceType.OPENAI;
  private Persona selectedPersona;

  private List<Persona> personas = new ArrayList<>();

  // Apparently this is an instance initializer block
  {
    personas.add(new Persona("No Persona", "Choose this persona if you want the default system prompt."));
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

  public void setSelectedPersona(Persona selectedPersona) {
    this.selectedPersona = selectedPersona;
  }

  public Persona getRandomPersona() {
    if (personas.isEmpty()) {
      return null;
    }
    int randomIndex = (int) (Math.random() * personas.size());
    return personas.get(randomIndex);
  }

  public Persona getSelectedPersona() {
    if (selectedPersona == null && !personas.isEmpty()) {
      selectedPersona = personas.get(0);
    }
    return selectedPersona;
  }

  public List<Persona> getPersonas() {
    return personas;
  }

  public void setPersonas(List<Persona> personas) {
    this.personas = personas;
    if (selectedPersona == null && !personas.isEmpty()) {
      selectedPersona = personas.get(0);
    }
  }
}
