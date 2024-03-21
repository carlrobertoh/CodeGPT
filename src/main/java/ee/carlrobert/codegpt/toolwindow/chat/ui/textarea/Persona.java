
package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import ee.carlrobert.codegpt.settings.service.ServiceType;

public class Persona {
    private String name;
    private String promptText;
    private ServiceType defaultServiceType;
    private ServiceType currentServiceType;

	public Persona() {
        this("", "", ServiceType.OPENAI);
    }

    public Persona(String name, String promptText, ServiceType defaultServiceType) {
        this.name = name;
        this.promptText = promptText;
        this.defaultServiceType = defaultServiceType;
        this.currentServiceType = defaultServiceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPromptText() {
        return promptText;
    }
    
    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

	public ServiceType getDefaultServiceType() {
		return defaultServiceType;
	}

	public void setDefaultServiceType(ServiceType defaultServiceType) {
		this.defaultServiceType = defaultServiceType;
	}

	public ServiceType getCurrentServiceType() {
		return currentServiceType;
	}

	public void setCurrentServiceType(ServiceType currentServiceType) {
		this.currentServiceType = currentServiceType;
	}

}
