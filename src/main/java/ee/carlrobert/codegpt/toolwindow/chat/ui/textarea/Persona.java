
package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

public class Persona {
    private String name;
    private String promptText;

    public Persona() {
        this("", "");
    }
    public Persona(String name, String promptText) {
        this.name = name;
        this.promptText = promptText;
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

}
