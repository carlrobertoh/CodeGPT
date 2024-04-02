package ee.carlrobert.codegpt.settings;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.Persona;

import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the general settings state of the application.
 * It includes the display name of the user, the selected persona, and a list of
 * all available personas.
 */
public class GeneralSettingsState {

  private String displayName = "";
  private Persona selectedPersona;

  private List<Persona> personas = new ArrayList<>();

  public static final String COMPLETION_SYSTEM_PROMPT = getResourceContent(
      "/prompts/default-completion-system-prompt.txt");
  public static final String RUBBER_DUCK_PROMP = getResourceContent(
      "/prompts/rubber-duck-prompt.txt");

  /**
   * Constructor for GeneralSettingsState.
   * Initializes the default personas.
   */
  public GeneralSettingsState() {
    initializeDefaultPersonas();
  }

  /**
   * Initializes the default personas.
   */
  public void initializeDefaultPersonas() {
    selectedPersona = new Persona("Default Assistant", COMPLETION_SYSTEM_PROMPT, ServiceType.OPENAI);
    personas.add(selectedPersona);
    personas.add(new Persona("Rubber Duck", RUBBER_DUCK_PROMP, ServiceType.OPENAI));
  }

  /**
   * Returns the display name of the user.
   * If the display name is not set, it returns the system user name.
   * If the system user name is not set, it returns "User".
   */
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

  /**
   * Sets the display name of the user.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Sets the selected persona.
   */
  public void setSelectedPersona(Persona selectedPersona) {
    this.selectedPersona = selectedPersona;
  }

  /**
   * Returns a random persona from the list of personas.
   * If the list is empty, it returns null.
   */
  public Persona getRandomPersona() {
    if (personas.isEmpty()) {
      return null;
    }
    int randomIndex = (int) (Math.random() * personas.size());
    return personas.get(randomIndex);
  }

  /**
   * Returns the selected persona.
   * If no persona is selected and the list of personas is not empty, it sets and
   * returns the first persona in the list.
   */
  public Persona getSelectedPersona() {
    if (selectedPersona == null && !personas.isEmpty()) {
      selectedPersona = personas.get(0);
    }
    return selectedPersona;
  }

  /**
   * Returns the list of all personas.
   */
  public List<Persona> getPersonas() {
    return personas;
  }

  /**
   * Sets the list of personas.
   * If no persona is selected and the list is not empty, it sets the selected
   * persona to the first persona in the list.
   */
  public void setPersonas(List<Persona> personas) {
    this.personas = personas;
    if (selectedPersona == null && !personas.isEmpty()) {
      selectedPersona = personas.get(0);
    }
  }
}
