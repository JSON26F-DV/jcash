package app.controller.transaction;

import java.math.BigDecimal;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
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
        balanceLabel.setText("Available Balance: PHP " + user.getBalance());
        backButton.setOnAction(event -> AppRoutes.dashboard());
        nextButton.setOnAction(event -> submit());
    }

    private void submit() {
        User receiver = userService.findByMobileNumber(mobileNumberField.getText());
        if (receiver == null) {
            errorLabel.setText("Mobile number not found.");
            return;
        }
        if (receiver.getId() == user.getId()) {
            errorLabel.setText("You cannot send money to your own number.");
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
            if (amount.signum() <= 0) {
                errorLabel.setText("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException | NullPointerException e) {
            errorLabel.setText("Enter a valid amount.");
            return;
        }
        if (amount.compareTo(user.getBalance()) > 0) {
            errorLabel.setText("Amount is more than your available balance.");
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
