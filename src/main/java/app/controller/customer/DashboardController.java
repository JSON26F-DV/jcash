package app.controller.customer;

import java.time.format.DateTimeFormatter;
import java.util.List;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.TransactionDTO;
import app.services.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardController extends HeaderController {
    private final TransactionService transactions = new TransactionService();

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label balanceAmountLabel;
    @FXML
    private Button viewMoreButton;
    @FXML
    private VBox transactionsListBox;

    @FXML
    protected void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        balanceAmountLabel.setText(user.getBalance().toString());
        viewMoreButton.setOnAction(e -> AppRoutes.history());
        List<TransactionDTO> latest = transactions.latestForUser(user.getId(), 5);
        for (TransactionDTO transaction : latest)
            transactionsListBox.getChildren().add(row(transaction));
    }

    private HBox row(TransactionDTO transaction) {
        VBox details = new VBox(
                label(transaction.getCategory(), "tx-title"),
                label(transaction.getDateTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")), "tx-date"));
        HBox row = new HBox(details, label(transaction.getAmount().toString(), "tx-amount"));
        row.getStyleClass().add("tx-line");
        HBox.setHgrow(details, Priority.ALWAYS);
        return row;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
