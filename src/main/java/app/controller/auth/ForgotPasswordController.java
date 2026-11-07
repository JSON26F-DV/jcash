package app.controller.auth;

import app.controller.common.AppRoutes;
import app.services.PasswordResetService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ForgotPasswordController {

    private final PasswordResetService resetService = new PasswordResetService();

    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button sendCodeButton;
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        sendCodeButton.setOnAction(event -> sendCode());
        backButton.setOnAction(event -> AppRoutes.landing());
    }

    private void sendCode() {
        if (resetService.sendOtp(emailField.getText())) {
            AppRoutes.verifyOtp();
            return;
        }
        statusLabel.setText(resetService.message());
    }
}
