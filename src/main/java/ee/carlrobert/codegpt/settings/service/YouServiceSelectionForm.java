package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationHandler;
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationError;
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationService;
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;
import ee.carlrobert.codegpt.completions.you.auth.response.YouUser;
import ee.carlrobert.codegpt.credentials.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;

public class YouServiceSelectionForm extends JPanel {

  private final JBTextField emailField;
  private final JBPasswordField passwordField;
  private final JButton signInButton;
  private final JTextPane signUpTextPane;
  private final AsyncProcessIcon loadingSpinner;

  public YouServiceSelectionForm(Disposable parentDisposable) {
    super(new BorderLayout());
    var settings = SettingsState.getInstance();
    emailField = new JBTextField(settings.getEmail(), 25);
    passwordField = new JBPasswordField();
    passwordField.setColumns(25);
    if (!settings.getEmail().isEmpty()) {
      passwordField.setText(YouCredentialsManager.getInstance().getAccountPassword());
    }
    signInButton = new JButton(CodeGPTBundle.get("settingsConfigurable.service.you.signIn.label"));
    signUpTextPane = createSignUpTextPane();
    loadingSpinner = new AsyncProcessIcon("sign_in_spinner");
    loadingSpinner.setBorder(JBUI.Borders.emptyLeft(8));
    loadingSpinner.setVisible(false);

    var emailValidator = createInputValidator(parentDisposable, emailField);
    var passwordValidator = createInputValidator(parentDisposable, passwordField);

    signInButton.addActionListener(e -> {
      emailValidator.revalidate();
      passwordValidator.revalidate();
      if (emailValidator.getValidationInfo() == null
          && passwordValidator.getValidationInfo() == null) {
        loadingSpinner.resume();
        loadingSpinner.setVisible(true);
        YouAuthenticationService.getInstance()
            .signInAsync(
                emailField.getText(),
                new String(passwordField.getPassword()),
                new UserAuthenticationHandler());
      }
    });

    if (YouUserManager.getInstance().getAuthenticationResponse() == null) {
      add(createUserAuthenticationPanel(emailField, passwordField, null));
    } else {
      add(createUserInformationPanel(
          YouUserManager.getInstance().getAuthenticationResponse().getData().getUser()));
    }
  }

  public String getEmail() {
    return emailField.getText();
  }

  public void setEmail(String email) {
    emailField.setText(email);
  }

  public String getPassword() {
    return new String(passwordField.getPassword());
  }

  private ComponentValidator createInputValidator(
      Disposable parentDisposable,
      JComponent component) {
    var validator = new ComponentValidator(parentDisposable)
        .withValidator(() -> {
          String value;
          if (component instanceof JBTextField) {
            value = ((JBTextField) component).getText();
            if (!isValidEmail(value)) {
              return new ValidationInfo(
                  CodeGPTBundle.get("validation.error.invalidEmail"), component)
                  .withOKEnabled();
            }
          } else {
            value = new String(((JPasswordField) component).getPassword());
          }

          if (StringUtil.isEmpty(value)) {
            return new ValidationInfo(
                CodeGPTBundle.get("validation.error.fieldRequired"), component)
                .withOKEnabled();
          }

          return null;
        })
        .andStartOnFocusLost()
        .installOn(component);
    validator.enableValidation();
    return validator;
  }

  private boolean isValidEmail(String email) {
    // RFC 5322
    return Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        .matcher(email)
        .matches();
  }

  private JTextPane createSignUpTextPane() {
    var textPane = SwingUtils.createTextPane(
        "<html><a href=\"https://you.com/code\">Don't have an account? Sign up</a></html>");
    textPane.setBorder(JBUI.Borders.emptyLeft(4));
    textPane.setOpaque(false);
    return textPane;
  }

  private JPanel createFooterPanel() {
    var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setBorder(JBUI.Borders.empty());
    panel.add(signInButton);
    panel.add(signUpTextPane);
    panel.add(loadingSpinner);
    return panel;
  }

  private JPanel createUserAuthenticationPanel(
      JBTextField emailAddressField,
      JBPasswordField passwordField,
      @Nullable YouAuthenticationError error) {
    var couponLabel = new JBLabel(
        "<html>"
            + "<body>"
            + "<h1 style=\"text-align: center; padding: 0; margin: 0;\">Free GPT-4</h1>"
            + "<p style=\"text-align: center; margin-top: 8px; margin-bottom: 8px;\">"
            + "Your coupon code"
            + "</p>"
            + "<h1 style=\"text-align: center; border: 2px dotted #646464; padding: 4px 32px; "
            + "margin: 0 0 12px 0; background-color: #45494a; cursor: pointer;\">"
            + "CODEGPT"
            + "</h1>"
            + "</body>"
            + "</html>")
        .withBorder(JBUI.Borders.emptyLeft(45)) // TODO
        .setCopyable(true);

    var contentPanelBuilder = FormBuilder.createFormBuilder()
        .addComponentToRightColumn(JBUI.Panels.simplePanel().addToLeft(couponLabel))
        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.service.you.email.label"),
            emailAddressField)
        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.service.you.password.label"),
            passwordField)
        .addVerticalGap(4)
        .addComponentToRightColumn(createFooterPanel())
        .addVerticalGap(4);

    if (error != null) {
      var invalidCredentialsLabel = new JBLabel(error.getErrorMessage());
      invalidCredentialsLabel.setForeground(JBColor.red);
      invalidCredentialsLabel.setBorder(JBUI.Borders.emptyLeft(4));
      contentPanelBuilder.addComponentToRightColumn(invalidCredentialsLabel);
    }

    var contentPanel = contentPanelBuilder.getPanel();
    contentPanel.setBorder(JBUI.Borders.emptyLeft(16));

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.you.authentication.title")))
        .addComponent(contentPanel)
        .getPanel();
  }

  private JPanel createUserInformationPanel(YouUser user) {
    var userManager = YouUserManager.getInstance();
    var contentPanelBuilder = FormBuilder.createFormBuilder()
        .addLabeledComponent(CodeGPTBundle.get("settingsConfigurable.service.you.email.label"),
            new JBLabel(user.getEmails().get(0).getEmail()).withFont(JBFont.label().asBold()));

    var signOutButton = new JButton(
        CodeGPTBundle.get("settingsConfigurable.service.you.signOut.label"));
    signOutButton.addActionListener(e -> {
      userManager.clearSession();
      refreshView(createUserAuthenticationPanel(emailField, passwordField, null));
    });

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.you.userInformation.title")))
        .addVerticalGap(8)
        .addComponent(JBUI.Panels
            .simplePanel(contentPanelBuilder.addVerticalGap(4)
                .addComponent(signOutButton)
                .getPanel())
            .withBorder(JBUI.Borders.emptyLeft(16)))
        .getPanel();
  }

  class UserAuthenticationHandler implements AuthenticationHandler {

    @Override
    public void handleAuthenticated(YouAuthenticationResponse authenticationResponse) {
      SwingUtilities.invokeLater(() -> {
        var email = emailField.getText();
        var password = passwordField.getPassword();
        SettingsState.getInstance().setEmail(email);
        YouCredentialsManager.getInstance().setAccountPassword(new String(password));
        refreshView(createUserInformationPanel(authenticationResponse.getData().getUser()));
      });
    }

    @Override
    public void handleGenericError() {
      SwingUtilities.invokeLater(() -> refreshView(createUserAuthenticationPanel(
          emailField,
          passwordField,
          new YouAuthenticationError("unknown", "Something went wrong."))));
    }

    @Override
    public void handleError(YouAuthenticationError error) {
      SwingUtilities.invokeLater(() -> refreshView(createUserAuthenticationPanel(
          emailField,
          passwordField,
          error)));
    }
  }

  private void refreshView(JPanel contentPanel) {
    loadingSpinner.suspend();
    loadingSpinner.setVisible(false);
    removeAll();
    add(contentPanel);
    revalidate();
    repaint();
  }
}
