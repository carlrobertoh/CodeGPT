package ee.carlrobert.codegpt.settings;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.Persona;

import java.util.ArrayList;
import java.util.List;

public class GeneralSettingsState {

    private String displayName = "";
    private Persona selectedPersona;

    private List<Persona> personas = new ArrayList<>();

    public GeneralSettingsState() {
        selectedPersona = new Persona("Default", "Default persona", ServiceType.OPENAI);
        personas.add(selectedPersona);
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
