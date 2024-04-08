package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

/**
 * Represents a persona with a name, prompt text, service type, and model.
 */
public class Persona {
  private String name;
  private String description;
  private String instructions;
  private ServiceType modelProvider;
  private String modelId;

  /**
   * Constructs a new Persona with default values.
   */
  public Persona() {
    this("", "", "", ServiceType.OPENAI);
  }

  /**
   * Constructs a new Persona with the specified name, prompt text, and service
   * type.
   *
   * @param name          the name of the persona
   * @param description   the description of the persona
   * @param instructions  the prompt text associated with the persona
   * @param modelProvider the service type of the persona
   */
  public Persona(String name, String description, String instructions, ServiceType modelProvider) {
    this.name = name;
    this.description = description;
    this.instructions = instructions;
    this.modelProvider = modelProvider;
    this.modelId = OpenAISettings.getCurrentState().getModel();
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
   * Returns the description of the persona.
   *
   * @return the description of the persona
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the persona.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the prompt text associated with the persona.
   *
   * @return the prompt text
   */
  public String getInstructions() {
    return instructions;
  }

  /**
   * Sets the prompt text associated with the persona.
   *
   * @param promptText the prompt text to set
   */
  public void setInstructions(String promptText) {
    this.instructions = promptText;
  }

  /**
   * Returns the service type of the persona.
   *
   * @return the service type
   */
  public ServiceType getModelProvider() {
    return modelProvider;
  }

  /**
   * Sets the service type of the persona.
   *
   * @param serviceType the service type to set
   */
  public void setModelProvider(ServiceType serviceType) {
    this.modelProvider = serviceType;
  }

  /**
   * Returns the model associated with the persona.
   *
   * @return the model
   */
  public String getModelId() {
    return modelId;
  }

  /**
   * Sets the model associated with the persona.
   *
   * @param model the model to set
   */
  public void setModelId(String model) {
    this.modelId = model;
  }
}
