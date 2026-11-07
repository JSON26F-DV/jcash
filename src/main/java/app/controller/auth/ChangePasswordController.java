package app.controller.auth;

import app.controller.common.AppRoutes;
import app.services.PasswordResetService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextFormatter;

public class ChangePasswordController {

    private final PasswordResetService resetService = new PasswordResetService();

    @FXML
    private PasswordField pinField;
    @FXML
    private PasswordField confirmPinField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button changePinButton;

    @FXML
    private void initialize() {
        restrictToSixDigits(pinField);
        restrictToSixDigits(confirmPinField);
        changePinButton.setOnAction(event -> changePin());
    }

    private void restrictToSixDigits(PasswordField field) {
        field.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,6}") ? change : null));
    }

    private void changePin() {
        if (!resetService.changePin(pinField.getText(), confirmPinField.getText())) {
            statusLabel.setText(resetService.message());
            return;
        }
        AppRoutes.landing();
    }
}
