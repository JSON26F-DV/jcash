package app.controller.transaction;

import java.math.BigDecimal;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.controller.customer.DetailsController;
import app.dto.view.TransactionDetails;
import app.services.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CashInController extends HeaderController {
    private final TransactionService transactions = new TransactionService();

    @FXML
    private Button backButton;
    @FXML
    private Button cashInButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField amountField;

    @FXML
    public void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        balanceLabel.setText("Available Balance: PHP " + user.getBalance());
        backButton.setOnAction(event -> AppRoutes.dashboard());
        cashInButton.setOnAction(event -> submit());
    }

    private void submit() {
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

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm cash in of PHP " + amount.toPlainString() + "?", ButtonType.CANCEL, ButtonType.OK);
        confirmation.setTitle("Confirm Cash In");
        confirmation.setHeaderText("Review your cash-in amount");
        confirmation.initOwner(cashInButton.getScene().getWindow());
        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty())
            return;

        if (transactions.cashIn(amount, "")) {
            DetailsController.select(TransactionDetails.from(transactions.lastTransaction()));
            AppRoutes.details();
        } else {
            errorLabel.setText("Cash in failed. Please try again.");
        }
    }
}
