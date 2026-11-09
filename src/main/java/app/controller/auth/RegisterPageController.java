package app.controller.auth;

import java.util.Objects;

import app.controller.common.AppRoutes;
import app.i18n.I18n;
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
        restrictToDigits(pinField, 6);
        restrictToDigits(confirmPinField, 6);
        restrictToDigits(mobileNumberField, 11);
        if (submitBtn != null)
            submitBtn.setOnAction(e -> handleSubmit());
        if (loginBtn != null)
            loginBtn.setOnAction(e -> AppRoutes.landing());
        if (backPage != null)
            backPage.setOnMouseClicked(e -> AppRoutes.landing());
    }

    private void restrictToDigits(TextField field, int maxDigits) {
        if (field != null) {
            field.setTextFormatter(
                    new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0," + maxDigits + "}") ? change : null));
        }
    }

    private void handleSubmit() {
        try {
            String pin = pinField.getText() == null ? "" : pinField.getText().trim();
            String confirmPin = confirmPinField.getText() == null ? "" : confirmPinField.getText().trim();
            String firstName = firstNameField.getText() == null ? "" : firstNameField.getText().trim();
            String lastName = lastNameField.getText() == null ? "" : lastNameField.getText().trim();
            String mobile = mobileNumberField.getText() == null ? "" : mobileNumberField.getText().trim();
            String email = emailField.getText() == null ? "" : emailField.getText().trim();

            if (firstName.isEmpty() || mobile.isEmpty() || email.isEmpty() || pin.isEmpty()) {
                errorLabel.setText(I18n.get("register.error.generic"));
                return;
            }

            if (pin.length() != 6) {
                errorLabel.setText(I18n.get("reset.pinLength"));
                return;
            }

            if (!Objects.equals(pin, confirmPin)) {
                errorLabel.setText(I18n.get("register.error.pin.mismatch"));
                return;
            }

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMobileNumber(mobile);
            user.setEmail(email);
            user.setPin(pin);

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    I18n.get("register.confirm.message"), ButtonType.CANCEL, ButtonType.OK);
            confirmation.setTitle(I18n.get("register.confirm.title"));
            confirmation.setHeaderText(I18n.get("register.confirm.header"));
            confirmation.initOwner(submitBtn.getScene().getWindow());
            if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty())
                return;

            if (authService.register(user)) {
                AppRoutes.landing();
            } else {
                errorLabel.setText(authService.message());
            }
        } catch (Exception e) {
            errorLabel.setText(I18n.format("register.error.failed", e.getMessage()));
        }
    }
}
