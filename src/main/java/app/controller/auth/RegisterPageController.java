package app.controller.auth;

import java.util.Objects;

import app.controller.common.AppRoutes;
import app.model.User;
import app.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class RegisterPageController {
    private final AuthService authService = new AuthService();

    @FXML
    private Label backPage;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField mobileNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField pinField;
    @FXML
    private PasswordField confirmPinField;
    @FXML
    private Button submitBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private Label errorLabel;

    @FXML
    protected void initialize() {
        restrictToDigits(pinField);
        restrictToDigits(confirmPinField);
        if (submitBtn != null)
            submitBtn.setOnAction(e -> handleSubmit());
        if (loginBtn != null)
            loginBtn.setOnAction(e -> AppRoutes.landing());
        if (backPage != null)
            backPage.setOnMouseClicked(e -> AppRoutes.landing());
    }

    private void restrictToDigits(TextField field) {
        if (field != null) {
            field.setTextFormatter(
                    new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,6}") ? change : null));
        }
    }

    private void handleSubmit() {
        try {
            if (!Objects.equals(pinField.getText(), confirmPinField.getText())) {
                errorLabel.setText("PIN and Confirm PIN do not match.");
                return;
            }
            User user = new User();
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            user.setMobileNumber(mobileNumberField.getText());
            user.setEmail(emailField.getText().trim());
            user.setPin(pinField.getText());

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to create this account?", ButtonType.CANCEL, ButtonType.OK);
            confirmation.setTitle("Confirm Registration");
            confirmation.setHeaderText("Review your registration details");
            confirmation.initOwner(submitBtn.getScene().getWindow());
            if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty())
                return;

            if (authService.register(user)) {
                AppRoutes.landing();
            } else {
                errorLabel.setText(authService.message());
            }
        } catch (Exception e) {
            errorLabel.setText("Registration failed: " + e.getMessage());
        }
    }
}
