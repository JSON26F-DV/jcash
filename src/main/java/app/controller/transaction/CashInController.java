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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cash In");
        confirmation.setHeaderText(null);
        confirmation.initOwner(cashInButton.getScene().getWindow());
        DialogPane pane = confirmation.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/app/css/app.css").toExternalForm());
        pane.setStyle("-fx-background-color:#FFFFFF; -fx-font-family:'Inter'; -fx-padding:18;");
        pane.setMinWidth(400);

        Label title = new Label("Review your cash-in amount");
        title.setStyle("-fx-font-family:'Plus Jakarta Sans'; -fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#333948;");
        Label caption = new Label("CASH IN AMOUNT");
        caption.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:#9DA3A9;");
        Label amountLabel = new Label("₱" + amount.toPlainString());
        amountLabel.setStyle("-fx-font-family:'Plus Jakarta Sans'; -fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:#3454E1;");
        Label note = new Label("This amount will be added to your balance.");
        note.setStyle("-fx-font-size:12px; -fx-text-fill:#9DA3A9;");
        VBox amountBox = new VBox(4, caption, amountLabel, note);
        amountBox.setStyle("-fx-background-color:#F7F9FC; -fx-background-radius:16; -fx-padding:16;");
        VBox content = new VBox(14, title, amountBox);
        pane.setContent(content);

        pane.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Cancel");
        cancelButton.setStyle("-fx-background-color:#FFFFFF; -fx-background-radius:16; -fx-border-color:#DDE2EA;"
                + " -fx-border-width:1; -fx-border-radius:16; -fx-text-fill:#333948; -fx-font-size:13px;"
                + " -fx-font-weight:bold; -fx-padding:0 18 0 18; -fx-pref-height:40; -fx-cursor:hand;");
        Button confirmButton = (Button) pane.lookupButton(ButtonType.OK);
        confirmButton.setText("Confirm");
        confirmButton.setStyle("-fx-background-color:#3454E1; -fx-background-radius:16; -fx-text-fill:#FFFFFF;"
                + " -fx-font-size:13px; -fx-font-weight:bold; -fx-padding:0 18 0 18; -fx-pref-height:40; -fx-cursor:hand;");
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
