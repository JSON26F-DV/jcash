package app.controller.auth;

import app.controller.common.AppRoutes;
import app.i18n.I18n;
import app.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LandingPageController {
    private final AuthService authService = new AuthService();

    @FXML
    private TextField mobileNumberField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button javaBtn;
    @FXML
    private Button forgotPasswordButton;
    @FXML
    private Button registerBtn;
    @FXML
    private Label loginHeadingLabel;
    @FXML
    private Label termsLink;
    @FXML
    private Label privacyLink;
    @FXML
    private Label errorLabel;

    @FXML
    protected void initialize() {
        if (javaBtn != null)
            javaBtn.setOnAction(e -> handleLogin());
        if (forgotPasswordButton != null)
            forgotPasswordButton.setOnAction(e -> AppRoutes.forgotPassword());
        if (registerBtn != null)
            registerBtn.setOnAction(e -> AppRoutes.register());
        if (termsLink != null)
            termsLink.setOnMouseClicked(e -> AppRoutes.terms());
        if (privacyLink != null)
            privacyLink.setOnMouseClicked(e -> AppRoutes.privacy());
    }

    private void handleLogin() {
        try {
            if (authService.login(mobileNumberField.getText(), passwordField.getText())) {
                AppRoutes.dashboard();
                return;
            }
            if (I18n.get("auth.deleted").equals(authService.message())) {
                AppRoutes.deletedAccount();
                return;
            }
            if (I18n.get("auth.lockout").equals(authService.message())) {
                errorLabel.setText(authService.message());
                Platform.exit();
                return;
            }
            errorLabel.setText(authService.message());
        } catch (Exception e) {
            errorLabel.setText(I18n.format("auth.loginFailed", e.getMessage()));
        }
    }

    @FXML
    private void selectLocale(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Label label && label.getId() != null) {
            AppRoutes.setLanguage(label.getId());
        }
    }
}
