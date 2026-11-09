package app.controller.transaction;

import java.math.BigDecimal;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.controller.customer.DetailsController;
import app.dto.view.TransactionDetails;
import app.i18n.I18n;
import app.model.User;
import app.services.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class ConfirmationController extends HeaderController {
    private final TransactionService transactions = new TransactionService();
    private static User receiver;
    private static BigDecimal amount = BigDecimal.ZERO;
    private static String message = "";

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label recipientInitialsLabel;
    @FXML
    private Label recipientNameLabel;
    @FXML
    private Label recipientNumberLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label cashBalanceLabel;
    @FXML
    private Label sourceAccountNumberLabel;
    @FXML
    private Label statusMessageLabel;
    @FXML
    private CheckBox confirmCheckBox;
    @FXML
    private Button sendButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button backButton;

    public static void select(User chosenReceiver, BigDecimal chosenAmount, String chosenMessage) {
        receiver = chosenReceiver;
        amount = chosenAmount;
        message = chosenMessage;
    }

    @FXML
    public void initialize() {
        if (setupHeader(welcomeLabel, dateTimeLabel)) {
            sourceAccountNumberLabel.setText("•••• " + lastFour(user.getMobileNumber()));
            cashBalanceLabel.setText("₱" + user.getBalance());
        }
        if (receiver != null) {
            recipientNameLabel.setText(maskName(receiver.getFirstName()) + " " + maskName(receiver.getLastName()));
            recipientInitialsLabel.setText(initials(receiver));
            recipientNumberLabel.setText(receiver.getMobileNumber());
        }
        amountLabel.setText("₱" + amount);
        totalAmountLabel.setText("₱" + amount);
        sendButton.setText(I18n.format("confirmation.send", "₱" + amount));
        confirmCheckBox.selectedProperty().addListener((obs, old, checked) -> setSendEnabled(checked));
        setSendEnabled(false);
        sendButton.setOnAction(event -> send());
        cancelButton.setOnAction(event -> AppRoutes.dashboard());
        backButton.setOnAction(event -> AppRoutes.expressSend());
    }

    private void send() {
        if (user == null || receiver == null)
            return;
        if (amount.compareTo(user.getBalance()) > 0) {
            statusMessageLabel.setText(I18n.get("confirmation.error.balance"));
            return;
        }
        if (transactions.send(receiver, amount, message)) {
            DetailsController.select(TransactionDetails.from(transactions.lastTransaction()));
            AppRoutes.details();
        } else {
            statusMessageLabel.setText(I18n.get("confirmation.error.failed"));
        }
    }

    private void setSendEnabled(boolean enabled) {
        sendButton.setDisable(!enabled);
        if (enabled) {
            sendButton.setOpacity(1);
        } else {
            sendButton.setOpacity(0.55);
        }
    }

    private String initials(User user) {
        String first = "";
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            first = user.getFirstName().substring(0, 1);
        }
        String last = "";
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            last = user.getLastName().substring(0, 1);
        }
        return (first + last).toUpperCase();
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 3) {
            return name == null ? "" : name;
        }
        return name.substring(0, 2) + "*".repeat(name.length() - 3) + name.substring(name.length() - 1);
    }

    private String lastFour(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "";
        }
        return mobile.substring(mobile.length() - 4);
    }
}
