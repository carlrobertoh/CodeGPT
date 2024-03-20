
package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import ee.carlrobert.codegpt.settings.service.ServiceType;

public class Persona {
    private String name;
    private String promptText;
    private ServiceType serviceType;

	public Persona() {
        this("", "", ServiceType.OPENAI);
    }

    public Persona(String name, String promptText, ServiceType serviceType) {
        this.name = name;
        this.promptText = promptText;
        this.serviceType = serviceType;
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

    public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

}
