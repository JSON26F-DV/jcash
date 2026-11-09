package app.controller.transaction;

import java.math.BigDecimal;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.i18n.I18n;
import app.model.User;
import app.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ExpressSendController extends HeaderController {
    private final UserService userService = new UserService();

    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField mobileNumberField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField messageField;

    @FXML
    public void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        balanceLabel.setText(I18n.format("send.balance", user.getBalance()));
        backButton.setOnAction(event -> AppRoutes.dashboard());
        nextButton.setOnAction(event -> submit());
    }

    private void submit() {
        User receiver = userService.findByMobileNumber(mobileNumberField.getText());
        if (receiver == null) {
            errorLabel.setText(I18n.get("send.error.mobile"));
            return;
        }
        if (receiver.getId() == user.getId()) {
            errorLabel.setText(I18n.get("send.error.self"));
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
            if (amount.signum() <= 0) {
                errorLabel.setText(I18n.get("send.error.positive"));
                return;
            }
        } catch (NumberFormatException | NullPointerException e) {
            errorLabel.setText(I18n.get("send.error.invalid"));
            return;
        }
        if (amount.compareTo(user.getBalance()) > 0) {
            errorLabel.setText(I18n.get("send.error.balance"));
            return;
        }
        String message = "";
        if (messageField.getText() != null) {
            message = messageField.getText().trim();
        }
        ConfirmationController.select(receiver, amount, message);
        AppRoutes.confirmation();
    }
}
