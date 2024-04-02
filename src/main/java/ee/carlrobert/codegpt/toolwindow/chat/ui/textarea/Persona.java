package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

//TODO: Implement Import/Export
//TODO: Write a note to Carl Robert about the persona refactor and why we thinks it's a good idea.

/**
 * Represents a persona with a name, prompt text, service type, and model.
 */
public class Persona {
  private String name;
  private String promptText;
  private ServiceType serviceType;
  private String model;

  /**
   * Constructs a new Persona with default values.
   */
  public Persona() {
    this("", "", ServiceType.OPENAI);
  }

  /**
   * Constructs a new Persona with the specified name, prompt text, and service
   * type.
   *
   * @param name        the name of the persona
   * @param promptText  the prompt text associated with the persona
   * @param serviceType the service type of the persona
   */
  public Persona(String name, String promptText, ServiceType serviceType) {
    this.name = name;
    this.promptText = promptText;
    this.serviceType = serviceType;
    this.model = OpenAISettings.getCurrentState().getModel();
  }

  /**
   * Returns the name of the persona.
   *
   * @return the name of the persona
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the persona.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the prompt text associated with the persona.
   *
   * @return the prompt text
   */
  public String getPromptText() {
    return promptText;
  }

  /**
   * Sets the prompt text associated with the persona.
   *
   * @param promptText the prompt text to set
   */
  public void setPromptText(String promptText) {
    this.promptText = promptText;
  }

  /**
   * Returns the service type of the persona.
   *
   * @return the service type
   */
  public ServiceType getServiceType() {
    return serviceType;
  }

  /**
   * Sets the service type of the persona.
   *
   * @param serviceType the service type to set
   */
  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType;
  }

  /**
   * Returns the model associated with the persona.
   *
   * @return the model
   */
  public String getModel() {
    return model;
  }

  /**
   * Sets the model associated with the persona.
   *
   * @param model the model to set
   */
  public void setModel(String model) {
    this.model = model;
  }
}
