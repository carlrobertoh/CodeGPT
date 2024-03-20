package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.GeneralSettingsState;
import ee.carlrobert.codegpt.settings.service.ServiceType;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// BUG: Switching the "Original" model changes the Persona's model. Instead, it should temporarily use the "Original" model's service type.
// TODO: Change icon when not using Persona model. Have a popup appear when hovering that alerts the user that they are not using a Persona model.
// Saving leaves the edit popup. Save and close? Save? Revert changes?
// TODO: Make a more robust prompt injection with the persona. Make it happen in one place.
// TODO: Use conversation types to determine if a persona should be used
// TODO: Update Original dropdown to display the correct service type 
// TODO: Convince Carlo to let us change the way chat's are updated when the service type is changed

public class PersonaComboBoxAction extends ComboBoxAction {
    private final GeneralSettingsState settings;
    ModelComboBoxAction modelComboBoxAction;

    public PersonaComboBoxAction(Persona selectedPersona, ModelComboBoxAction modelComboBoxAction) {
        this.settings = GeneralSettings.getCurrentState();
        this.modelComboBoxAction = modelComboBoxAction;
        updateTemplatePresentation(selectedPersona);
    }
    public JComponent createCustomComponent(@NotNull String place) {
        return createCustomComponent(getTemplatePresentation(), place);
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(
            @NotNull Presentation presentation,
            @NotNull String place) {
        ComboBoxButton button = createComboBoxButton(presentation);
        button.setBorder(null);
        return button;
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent button) {
        var group = new DefaultActionGroup();
        boolean missingDefaultPersona = true;
        for (Persona persona : settings.getPersonas()) {
            if (persona.getName().equals("No Persona")) {
                missingDefaultPersona = false;
            }
            group.add(createPersonaAction(persona, getTemplatePresentation()));
        }
        if (missingDefaultPersona) {
            settings.getPersonas().add(new Persona("No Persona", "Choose this persona if you want the default system prompt.", ServiceType.OPENAI));
            group.add(createPersonaAction(new Persona("No Persona", "Choose this persona if you want the default system prompt.", ServiceType.OPENAI), getTemplatePresentation()));
        }
        group.addSeparator();
        group.add(new NewPersonaAction());
        group.add(new EditPersonasAction());
        return group;
    }

    @Override
    protected boolean shouldShowDisabledActions() {
        return true;
    }

    private AnAction createPersonaAction(Persona persona, Presentation comboBoxPresentation) {
        return new DumbAwareAction(persona.getName(), "", Icons.Persona) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent event) {
                System.out.println("Selected persona: " + persona.getName());
                settings.setSelectedPersona(persona);
                settings.setSelectedService(persona.getServiceType());
                modelComboBoxAction.updateTemplatePresentation(persona.getServiceType());
                comboBoxPresentation.setText(persona.getName());
            }

            @Override
            public void update(@NotNull AnActionEvent event) {
                var presentation = event.getPresentation();
                presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
                System.out.println("Updating persona: " + persona.getName());
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        };
    }

    private class EditPersonasAction extends DumbAwareAction {
        public EditPersonasAction() {
            super("Edit Personas", "", Icons.Edit);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            JDialog dialog = new JDialog();
            dialog.setTitle("Edit Personas");
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(true);

            JPanel panel = new JPanel(new BorderLayout());

            ArrayList<String> personaNames = new ArrayList<>();
            for (Persona persona : settings.getPersonas()) {
                personaNames.add(persona.getName());
            }

            JList<String> personaList = new JBList<>(personaNames);
            personaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            int selectedIndex = 0;
            for (String personaName : personaNames) {
                if (personaName.equals(settings.getSelectedPersona().getName())) {
                    break;
                } else {
                    selectedIndex++;
                }
            }
            personaList.setSelectedIndex(selectedIndex);

            JPanel editPanel = new JPanel(new VerticalFlowLayout());

            JBTextField nameField = new JBTextField();
            nameField.setText(settings.getSelectedPersona().getName());
            
            JBTextArea promptField = new JBTextArea();
            promptField.setText(settings.getSelectedPersona().getPromptText());
            if (nameField.getText().equals("No Persona")) {
                nameField.setEditable(false);
                promptField.setEditable(false);
            }

            PersonaModelComboBoxAction personaModelComboBoxAction = new PersonaModelComboBoxAction(settings.getSelectedPersona());

            promptField.setLineWrap(true);
            promptField.setWrapStyleWord(true);
            promptField.setRows(8);

            editPanel.add(new JBLabel("Name:"));
            editPanel.add(nameField);

            editPanel.add(new JBLabel("Prompt:"));
            editPanel.add(new JScrollPane(promptField));

            editPanel.add(new JBLabel("Service:"));
            editPanel.add(personaModelComboBoxAction.createCustomComponent(ActionPlaces.UNKNOWN));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e1 -> {
                String selectedName = personaList.getSelectedValue();
                if (selectedName != null) {
                    Persona selectedPersona = settings.getPersonas().stream()
                            .filter(persona -> persona.getName().equals(selectedName))
                            .findFirst()
                            .orElse(null);
                    if (selectedPersona != null) {
                        boolean doUpdateTemplatePresentation = settings.getSelectedPersona().getName().equals(selectedName);
                        String newName = nameField.getText();
                        String newPrompt = promptField.getText();

                        selectedPersona.setName(newName);
                        selectedPersona.setPromptText(newPrompt);
                        selectedPersona.setServiceType(personaModelComboBoxAction.getSelectedService());

                        int index = personaNames.indexOf(selectedName);
                        if (index != -1) {
                            personaNames.set(index, newName);
                        }

                        personaList.setListData(personaNames.toArray(new String[0]));
                        personaList.setSelectedValue(newName, true);

                        if (doUpdateTemplatePresentation) {
                            settings.setSelectedPersona(selectedPersona);
                            settings.setSelectedService(selectedPersona.getServiceType());
                            modelComboBoxAction.updateTemplatePresentation(selectedPersona.getServiceType());
                            updateTemplatePresentation(selectedPersona);
                        }
                    }
                }
            });
            saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editPanel.add(saveButton);

            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e1 -> {
                String selectedName = personaList.getSelectedValue();
                if (selectedName != null && !selectedName.equals("No Persona")) {
                    Persona selectedPersona = settings.getPersonas().stream()
                            .filter(persona -> persona.getName().equals(selectedName))
                            .findFirst()
                            .orElse(null);
                    if (selectedPersona != null) {
                        boolean doUpdateTemplatePresentation = settings.getSelectedPersona().getName().equals(selectedName);
                        settings.getPersonas().remove(selectedPersona);
                        personaNames.remove(selectedName);
                        personaList.setListData(personaNames.toArray(new String[0]));
                        if (!personaNames.isEmpty()) {
                            personaList.setSelectedIndex(0);
                        }
                        if (doUpdateTemplatePresentation) {
                            Persona randomPersona = settings.getRandomPersona();
                            settings.setSelectedPersona(randomPersona);
                            updateTemplatePresentation(randomPersona);
                        }
                    }
                }
            });
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editPanel.add(deleteButton);

            personaList.addListSelectionListener(e1 -> {
                if (!e1.getValueIsAdjusting()) {
                    String selectedName = personaList.getSelectedValue();
                    if (selectedName != null) {
                        Persona selectedPersona = settings.getPersonas().stream()
                                .filter(persona -> persona.getName().equals(selectedName))
                                .findFirst()
                                .orElse(null);
                        if (selectedPersona != null) {
                            if (selectedName.equals("No Persona")) {
                                nameField.setEditable(false);
                                promptField.setEditable(false);
                            } else {
                                nameField.setEditable(true);
                                promptField.setEditable(true);
                            }
                            nameField.setText(selectedPersona.getName());
                            promptField.setText(selectedPersona.getPromptText());
                            personaModelComboBoxAction.setPersona(selectedPersona);
                        }
                    }
                }
            });
            personaList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e1 -> dialog.dispose());
            closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            editPanel.add(closeButton);

            panel.add(new JScrollPane(personaList), BorderLayout.WEST);
            panel.add(editPanel, BorderLayout.CENTER);

            dialog.getContentPane().add(panel);
            dialog.getContentPane().setPreferredSize(new Dimension(600, 400));
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }
    private class NewPersonaAction extends DumbAwareAction {
        public NewPersonaAction() {
            super("New Persona", "", Icons.New);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            JDialog dialog = new JDialog();
            dialog.setTitle("New Persona");
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(true);

            JPanel panel = new JPanel(new VerticalFlowLayout());

            JBTextField nameField = new JBTextField();
            JBTextArea promptField = new JBTextArea();
            PersonaModelComboBoxAction personaModelComboBoxAction = new PersonaModelComboBoxAction(new Persona());

            promptField.setLineWrap(true);
            promptField.setWrapStyleWord(true);
            promptField.setRows(8);

            panel.add(new JBLabel("Name:"));
            panel.add(nameField);

            panel.add(new JBLabel("Prompt:"));
            panel.add(new JScrollPane(promptField));

            panel.add(new JBLabel("Service:"));
            panel.add(personaModelComboBoxAction.createCustomComponent(ActionPlaces.UNKNOWN));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e1 -> {
                String name = nameField.getText();
                String promptText = promptField.getText();
                ServiceType serviceType = personaModelComboBoxAction.getSelectedService();

                if (!name.isEmpty() && !promptText.isEmpty()) {
                    Persona newPersona = new Persona(name, promptText, serviceType);
                    settings.getPersonas().add(newPersona);
                    settings.setSelectedPersona(newPersona);
                    settings.setSelectedService(serviceType);
                    modelComboBoxAction.updateTemplatePresentation(newPersona.getServiceType());
                    updateTemplatePresentation(settings.getSelectedPersona());
                }
                dialog.dispose();
            });
            buttonPanel.add(okButton);
            okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e1 -> dialog.dispose());
            buttonPanel.add(cancelButton);
            cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(panel, BorderLayout.CENTER);
            dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

            dialog.getContentPane().setPreferredSize(new Dimension(500, 400));
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }
    private void updateTemplatePresentation(Persona selectedPersona) {
        var templatePresentation = getTemplatePresentation();
            templatePresentation.setIcon(Icons.Persona);
            templatePresentation.setText(selectedPersona.getName());
    }
}
