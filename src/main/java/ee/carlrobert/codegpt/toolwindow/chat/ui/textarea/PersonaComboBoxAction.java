package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PersonaComboBoxAction extends ComboBoxAction {
  private final GeneralSettingsState settings;
  private static final String DEFAULT_ASSISTANT = "Default Assistant";
  private static final String RUBBER_DUCK = "Rubber Duck";

  public PersonaComboBoxAction(Persona selectedPersona) {
    this.settings = GeneralSettings.getCurrentState();
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
    for (Persona persona : settings.getPersonas()) {
      group.add(createPersonaAction(persona, getTemplatePresentation()));
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
    return new DumbAwareAction(persona.getName(), persona.getDescription(), Icons.Persona) {
      @Override
      public void actionPerformed(@NotNull AnActionEvent event) {
        settings.setSelectedPersona(persona);
        comboBoxPresentation.setText(persona.getName());
      }

      @Override
      public void update(@NotNull AnActionEvent event) {
        var presentation = event.getPresentation();
        presentation.setEnabled(!presentation.getText().equals(comboBoxPresentation.getText()));
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

      JList<String> personaList = new JList<>(personaNames.toArray(new String[0]));
      personaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      personaList.setLayoutOrientation(JList.VERTICAL);
      personaList.setVisibleRowCount(-1);
      personaList.setFixedCellHeight(24);
      personaList.setFixedCellWidth(200);
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

      JBTextArea descriptionField = new JBTextArea();
      descriptionField.setText(settings.getSelectedPersona().getDescription());

      JBTextArea promptField = new JBTextArea();
      promptField.setText(settings.getSelectedPersona().getInstructions());
      if (nameField.getText().equals("Default Assistant") || nameField.getText().equals("Rubber Duck")) {
        nameField.setEditable(false);
        promptField.setEditable(false);
        descriptionField.setEditable(false);
      }

      PersonaModelComboBoxAction personaModelComboBoxAction = new PersonaModelComboBoxAction(
          settings.getSelectedPersona());

      descriptionField.setLineWrap(true);
      descriptionField.setWrapStyleWord(true);
      descriptionField.setRows(8);
      descriptionField.setCaretPosition(0);

      promptField.setLineWrap(true);
      promptField.setWrapStyleWord(true);
      promptField.setRows(16);
      promptField.setCaretPosition(0);

      editPanel.add(new JBLabel("Name:"));
      editPanel.add(nameField);

      editPanel.add(new JBLabel("Description:"));
      editPanel.add(new JScrollPane(descriptionField));

      editPanel.add(new JBLabel("Prompt:"));
      editPanel.add(new JScrollPane(promptField));

      editPanel.add(new JBLabel("Service:"));
      editPanel.add(personaModelComboBoxAction.createCustomComponent(ActionPlaces.UNKNOWN));

      JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      JButton saveButton = createButton("Save", event -> {
        String selectedName = personaList.getSelectedValue();
        if (selectedName != null) {
          Persona selectedPersona = settings.getPersonas().stream()
              .filter(persona -> persona.getName().equals(selectedName))
              .findFirst()
              .orElse(null);
          if (selectedPersona != null) {
            boolean doUpdateTemplatePresentation = settings.getSelectedPersona().getName().equals(selectedName);
            final String newName = nameField.getText();
            if (settings.getPersonas().stream().anyMatch(persona -> persona.getName().equals(newName)) &&
                !newName.equals(selectedPersona.getName())) {
              JOptionPane.showMessageDialog(null, "Error: Persona with this name already exists.", "Error",
                  JOptionPane.ERROR_MESSAGE);
              nameField.setText(selectedPersona.getName());
              descriptionField.setText(selectedPersona.getDescription());
              promptField.setText(selectedPersona.getInstructions());
              return;
            }
            String newPrompt = promptField.getText();

            selectedPersona.setName(newName);
            selectedPersona.setDescription(descriptionField.getText());
            selectedPersona.setInstructions(newPrompt);
            selectedPersona.setModelProvider(personaModelComboBoxAction.getSelectedService());
            if (selectedPersona.getModelProvider() == OPENAI) {
              selectedPersona.setModelId(OpenAISettings.getCurrentState().getModel());
            }
            personaModelComboBoxAction.setPersona(selectedPersona);

            int index = personaNames.indexOf(selectedName);
            if (index != -1) {
              personaNames.set(index, newName);
            }

            personaList.setListData(personaNames.toArray(new String[0]));
            personaList.setSelectedValue(newName, true);

            if (doUpdateTemplatePresentation) {
              settings.setSelectedPersona(selectedPersona);
              if (selectedPersona.getModelProvider() == OPENAI) {
                OpenAISettings.getCurrentState().setModel(selectedPersona.getModelId());
              }
              updateTemplatePresentation(selectedPersona);
              dialog.dispose();
            }
          }
        }
      });

      JButton duplicateButton = createButton("Duplicate", event -> {
        String selectedName = personaList.getSelectedValue();
        if (selectedName != null) {
          Persona selectedPersona = settings.getPersonas().stream()
              .filter(persona -> persona.getName().equals(selectedName))
              .findFirst()
              .orElse(null);
          if (selectedPersona != null) {
            String newName = getNewName(selectedPersona.getName());
            Persona duplicatedPersona = new Persona(
                newName,
                selectedPersona.getDescription(),
                selectedPersona.getInstructions(),
                selectedPersona.getModelProvider());
            if (selectedPersona.getModelProvider() == OPENAI) {
              duplicatedPersona.setModelId(selectedPersona.getModelId());
            }
            settings.getPersonas().add(duplicatedPersona);
            personaNames.add(duplicatedPersona.getName());
            personaList.setListData(personaNames.toArray(new String[0]));
            personaList.setSelectedValue(duplicatedPersona.getName(), true);
          }
        }
      });
      JButton importButton = createButton("Import", event -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        int result = fileChooser.showOpenDialog(dialog);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          try {
            String json = Files.readString(selectedFile.toPath());
            Gson gson = new Gson();
            Persona importedPersona = gson.fromJson(json, Persona.class);

            if (settings.getPersonas().stream()
                .anyMatch(persona -> persona.getName().equals(importedPersona.getName()))) {
              JOptionPane.showMessageDialog(null, "Error: Persona with this name already exists.", "Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            settings.getPersonas().add(importedPersona);
            personaNames.add(importedPersona.getName());
            personaList.setListData(personaNames.toArray(new String[0]));
            personaList.setSelectedValue(importedPersona.getName(), true);
            settings.setSelectedPersona(importedPersona);
            updateTemplatePresentation(importedPersona);
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading JSON file: " + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          } catch (JsonSyntaxException ex) {
            JOptionPane.showMessageDialog(null, "Invalid JSON format: " + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      });

      JButton exportButton = createButton("Export", event -> {
        if (personaList.getSelectedValuesList().size() > 1) {
          handleMultipleSelectionExport(personaList, dialog);
        } else {
          handleSingleSelectionExport(personaList, dialog);
        }
      });

      JButton deleteButton = createButton("Delete", event -> {
        if (personaList.getSelectedValuesList().size() > 1) {
          handleMultipleSelectionDelete(personaList, personaNames);
        } else {
          handleSingleSelectionDelete(personaList, personaNames);
        }
      });
      deleteButton.setEnabled(!isDefaultOrRubberDuck(settings.getSelectedPersona()));

      personaList.addListSelectionListener(e1 -> {
        if (!e1.getValueIsAdjusting()) {
          String selectedName = personaList.getSelectedValue();
          if (selectedName != null) {
            Persona selectedPersona = settings.getPersonas().stream()
                .filter(persona -> persona.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
            if (selectedPersona != null) {
              if (selectedName.equals("Default Assistant") || selectedName.equals("Rubber Duck")) {
                nameField.setEditable(false);
                descriptionField.setEditable(false);
                promptField.setEditable(false);
                deleteButton.setEnabled(false);
              } else {
                nameField.setEditable(true);
                descriptionField.setEditable(true);
                promptField.setEditable(true);
                deleteButton.setEnabled(true);
              }
              nameField.setText(selectedPersona.getName());
              descriptionField.setText(selectedPersona.getDescription());
              descriptionField.setCaretPosition(0);
              promptField.setText(selectedPersona.getInstructions());
              promptField.setCaretPosition(0);
              personaModelComboBoxAction.setPersona(selectedPersona);
              if (personaList.getSelectedValuesList().size() > 1) {
                deleteButton.setText("Delete All");
                exportButton.setText("Export All");
              } else {
                deleteButton.setText("Delete");
                exportButton.setText("Export");
              }
            }
          }
        }
      });
      personaList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      JButton closeButton = createButton("Close", event -> dialog.dispose());

      leftButtonPanel.add(importButton);
      leftButtonPanel.add(exportButton);
      leftButtonPanel.add(duplicateButton);

      rightButtonPanel.add(saveButton);
      rightButtonPanel.add(deleteButton);
      rightButtonPanel.add(closeButton);

      JPanel buttonPanel = new JPanel(new BorderLayout());
      buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));
      buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
      buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

      JScrollPane listScroller = new JScrollPane(personaList);
      listScroller.setPreferredSize(new Dimension(250, 80));
      listScroller.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)));

      panel.add(listScroller, BorderLayout.WEST);
      panel.add(editPanel, BorderLayout.CENTER);
      panel.add(buttonPanel, BorderLayout.SOUTH);

      dialog.getContentPane().add(panel);
      dialog.getContentPane().setPreferredSize(new Dimension(1200, 900));
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }

    private void handleMultipleSelectionExport(JList<String> personaList, JDialog dialog) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int result = fileChooser.showSaveDialog(dialog);
      if (result == JFileChooser.APPROVE_OPTION) {
        File selectedDirectory = fileChooser.getSelectedFile();
        try {
          ZipOutputStream zipOut = new ZipOutputStream(
              new FileOutputStream(new File(selectedDirectory, "personas.zip")));
          Gson gson = new Gson();
          for (String name : personaList.getSelectedValuesList()) {
            Persona selectedPersona = settings.getPersonas().stream()
                .filter(persona -> persona.getName().equals(name))
                .findFirst()
                .orElse(null);
            if (selectedPersona != null) {
              File tempFile = File.createTempFile(name.toString(), ".json");
              String json = gson.toJson(selectedPersona);
              Files.writeString(tempFile.toPath(), json);
              FileInputStream fis = new FileInputStream(tempFile);
              ZipEntry zipEntry = new ZipEntry(tempFile.getName());
              zipOut.putNextEntry(zipEntry);
              byte[] bytes = new byte[1024];
              int length;
              while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
              }
              fis.close();
              tempFile.delete();
            }
          }
          zipOut.close();
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(null, "Error writing ZIP file: " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }

    private void handleSingleSelectionExport(JList<String> personaList, JDialog dialog) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
      int result = fileChooser.showSaveDialog(dialog);
      if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
          Gson gson = new Gson();
          Persona selectedPersona = settings.getPersonas().stream()
              .filter(persona -> persona.getName().equals(personaList.getSelectedValue()))
              .findFirst()
              .orElse(null);
          if (selectedPersona == null) {
            JOptionPane.showMessageDialog(null, "Error: Persona does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String json = gson.toJson(selectedPersona);
          Files.writeString(selectedFile.toPath(), json);
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(null, "Error writing JSON file: " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }

    private void handleMultipleSelectionDelete(JList<String> personaList, ArrayList<String> personaNames) {
      ArrayList<String> selectedNames = new ArrayList<>(personaList.getSelectedValuesList());
      if (selectedNames.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Error: No personas selected", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      int dialogResult = JOptionPane.showConfirmDialog(null,
          "Are you sure you want to delete selected personas?",
          "Warning", JOptionPane.YES_NO_OPTION);
      if (dialogResult == JOptionPane.NO_OPTION) {
        return;
      }
      for (String name : selectedNames) {
        Persona selectedPersona = settings.getPersonas().stream()
            .filter(persona -> persona.getName().equals(name))
            .findFirst()
            .orElse(null);
        if (selectedPersona != null) {
          settings.getPersonas().remove(selectedPersona);
          personaNames.remove(name);
        }
      }
      personaList.setListData(personaNames.toArray(new String[0]));
      if (!personaNames.isEmpty()) {
        personaList.setSelectedIndex(0);
      }
      Persona selectedPersona = personaList.getSelectedValue() == null ? settings.getRandomPersona()
          : settings.getPersonas().stream()
              .filter(persona -> persona.getName().equals(personaList.getSelectedValue()))
              .findFirst()
              .orElse(null);
      settings.setSelectedPersona(selectedPersona);
      if (selectedPersona.getModelProvider() == OPENAI) {
        OpenAISettings.getCurrentState().setModel(selectedPersona.getModelId());
      }
      updateTemplatePresentation(selectedPersona);
    }

    private void handleSingleSelectionDelete(JList<String> personaList, ArrayList<String> personaNames) {
      String selectedName = personaList.getSelectedValue();
      int index = personaNames.indexOf(selectedName);
      if (selectedName == null) {
        JOptionPane.showMessageDialog(null, "Error: Persona does not exist", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      Persona selectedPersona = settings.getPersonas().stream()
          .filter(persona -> persona.getName().equals(selectedName))
          .findFirst()
          .orElse(null);
      if (selectedPersona == null) {
        JOptionPane.showMessageDialog(null, "Error: Persona does not exist", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      int dialogResult = JOptionPane.showConfirmDialog(null,
          "Are you sure you want to delete " + selectedPersona.getName() + "?",
          "Warning", JOptionPane.YES_NO_OPTION);
      if (dialogResult == JOptionPane.NO_OPTION) {
        return;
      }
      boolean doUpdateTemplatePresentation = settings.getSelectedPersona().getName().equals(selectedName);
      settings.getPersonas().remove(selectedPersona);
      personaNames.remove(selectedName);
      personaList.setListData(personaNames.toArray(new String[0]));
      if (!personaNames.isEmpty()) {
        personaList.setSelectedIndex(index == 0 ? 0 : index - 1);
      }
      if (doUpdateTemplatePresentation) {
        Persona randomPersona = settings.getRandomPersona();
        settings.setSelectedPersona(randomPersona);
        if (randomPersona.getModelProvider() == OPENAI) {
          OpenAISettings.getCurrentState().setModel(randomPersona.getModelId());
        }
        updateTemplatePresentation(randomPersona);
      }
    }
  }

  private boolean isDefaultOrRubberDuck(Persona persona) {
    String name = persona.getName();
    return DEFAULT_ASSISTANT.equals(name) || RUBBER_DUCK.equals(name);
  }

  private JButton createButton(String text, ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setPreferredSize(new Dimension(100, 30));
    button.addActionListener(actionListener);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return button;
  }

  private String getNewName(String originalName) {
    String name = originalName;
    Pattern pattern = Pattern.compile("\\((\\d+)\\)$");
    Matcher matcher = pattern.matcher(name);
    boolean duplicate = false;
    do {
      if (matcher.find()) {
        int duplicateCount = Integer.parseInt(matcher.group(1));
        duplicateCount++;
        name = name.substring(0, matcher.start()) + "(" + duplicateCount + ")";
      } else if (duplicate) {
        name += " (1)";
      }

      final String finalNewName = name;
      duplicate = settings.getPersonas().stream().anyMatch(persona -> persona.getName().equals(finalNewName));
      if (duplicate) {
        matcher = pattern.matcher(name);
      }
    } while (duplicate);

    return name;
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
      JBTextArea descriptionField = new JBTextArea();
      JBTextArea promptField = new JBTextArea();
      PersonaModelComboBoxAction personaModelComboBoxAction = new PersonaModelComboBoxAction(new Persona());

      descriptionField.setLineWrap(true);
      descriptionField.setWrapStyleWord(true);
      descriptionField.setRows(8);

      promptField.setLineWrap(true);
      promptField.setWrapStyleWord(true);
      promptField.setRows(16);

      panel.add(new JBLabel("Name:"));
      panel.add(nameField);

      panel.add(new JBLabel("Description:"));
      panel.add(new JScrollPane(descriptionField));

      panel.add(new JBLabel("Prompt:"));
      panel.add(new JScrollPane(promptField));

      panel.add(new JBLabel("Service:"));
      panel.add(personaModelComboBoxAction.createCustomComponent(ActionPlaces.UNKNOWN));

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton okButton = new JButton("OK");
      okButton.addActionListener(e1 -> {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String promptText = promptField.getText();
        ServiceType serviceType = personaModelComboBoxAction.getSelectedService();

        if (!name.isEmpty() && !description.isEmpty() && !promptText.isEmpty()) {
          Persona newPersona = new Persona(getNewName(name), description, promptText, serviceType);
          settings.getPersonas().add(newPersona);
          settings.setSelectedPersona(newPersona);
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
      dialog.getContentPane().setPreferredSize(new Dimension(1000, 800));
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
