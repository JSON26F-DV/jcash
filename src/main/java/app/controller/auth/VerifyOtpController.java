package app.controller.auth;

import app.controller.common.AppRoutes;
import app.services.PasswordResetService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class VerifyOtpController {

    private final PasswordResetService resetService = new PasswordResetService();
    private final TextField[] digits = new TextField[6];

    @FXML
    private TextField otp1;
    @FXML
    private TextField otp2;
    @FXML
    private TextField otp3;
    @FXML
    private TextField otp4;
    @FXML
    private TextField otp5;
    @FXML
    private TextField otp6;
    @FXML
    private Label statusLabel;
    @FXML
    private Button verifyButton;
    @FXML
    private Button backButton;
    @FXML
    private Button resendButton;

    private Timeline resendTimer;

    @FXML
    private void initialize() {
        digits[0] = otp1;
        digits[1] = otp2;
        digits[2] = otp3;
        digits[3] = otp4;
        digits[4] = otp5;
        digits[5] = otp6;
        for (int i = 0; i < digits.length; i++) {
            setupDigit(digits[i], i);
        }

        verifyButton.setOnAction(event -> verify());
        backButton.setOnAction(event -> AppRoutes.forgotPassword());
        resendButton.setOnAction(event -> resend());
        updateResendButton();
        resendTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateResendButton()));
        resendTimer.setCycleCount(Timeline.INDEFINITE);
        resendTimer.play();
    }

    private void setupDigit(TextField field, int index) {
        field.setTextFormatter(
                new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,1}") ? change : null));
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 1 && index < digits.length - 1) {
                digits[index + 1].requestFocus();
            }
        });
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && index > 0) {
                digits[index - 1].clear();
                digits[index - 1].requestFocus();
            }
        });
    }

    private void verify() {
        if (resetService.verifyOtp(code())) {
            AppRoutes.changePassword();
            return;
        }
        statusLabel.setText(resetService.message());
    }

    private void resend() {
        if (resetService.resendOtp()) {
            clearDigits();
            statusLabel.setText(resetService.message());
            if (resendTimer != null) {
                resendTimer.playFromStart();
            }
        } else {
            statusLabel.setText(resetService.message());
        }
        updateResendButton();
    }

    private String code() {
        StringBuilder code = new StringBuilder(6);
        for (TextField digit : digits) {
            code.append(digit.getText());
        }
        return code.toString();
    }

    private void clearDigits() {
        for (TextField digit : digits) {
            digit.clear();
        }
        digits[0].requestFocus();
    }

    private void updateResendButton() {
        long seconds = resetService.resendSecondsRemaining();
        if (seconds == 0) {
            resendButton.setDisable(false);
            resendButton.setText("Resend code");
            if (resendTimer != null) {
                resendTimer.stop();
            }
            return;
        }
        resendButton.setDisable(true);
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        resendButton.setText(String.format("Resend code in %02d:%02d", minutes, remainingSeconds));
    }
}
