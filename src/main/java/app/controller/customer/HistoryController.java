package app.controller.customer;

import java.time.format.DateTimeFormatter;
import java.util.List;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.TransactionDTO;
import app.dto.view.TransactionDetails;
import app.services.TransactionService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryController extends HeaderController {
    private final TransactionService transactionService = new TransactionService();

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private VBox transactionsListBox;

    @FXML
    protected void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        List<TransactionDTO> transactions = transactionService.selectAll(user.getId());
        for (TransactionDTO transaction : transactions)
            transactionsListBox.getChildren().add(row(transaction));
    }

    private Button row(TransactionDTO transaction) {
        Label title = label(transaction.getCategory() == null ? "Transaction" : transaction.getCategory(), "tx-title");
        Label date = label(transaction.getDateTime() == null ? ""
                : transaction.getDateTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")), "tx-date");
        Label currency = label("₱", "tx-amount");
        Label amount = label(transaction.getAmount().toString(), "tx-amount");
        HBox money = new HBox(2, currency, amount);
        money.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox content = new HBox(12, new VBox(2, title, date), spacer, money);
        content.setAlignment(Pos.CENTER_LEFT);
        content.prefWidthProperty().bind(transactionsListBox.widthProperty().subtract(28));
        Button row = new Button();
        row.setGraphic(content);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setStyle("-fx-background-color:transparent; -fx-cursor:hand; -fx-alignment:CENTER_LEFT;");
        row.getStyleClass().add("tx-line");
        row.setOnAction(e -> {
            DetailsController.select(TransactionDetails.from(transaction));
            AppRoutes.details();
        });
        return row;
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
