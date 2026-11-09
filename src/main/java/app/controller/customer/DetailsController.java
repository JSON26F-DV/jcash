package app.controller.customer;

import java.time.format.DateTimeFormatter;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.view.TransactionDetails;
import app.i18n.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DetailsController extends HeaderController {
    private static TransactionDetails selected;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label transactionTitleLabel;
    @FXML
    private Label amountValueLabel;
    @FXML
    private Label messageValueLabel;
    @FXML
    private Label dateTimeValueLabel;
    @FXML
    private Label referenceValueLabel;
    @FXML
    private Button backButton;

    public static void select(TransactionDetails details) {
        selected = details;
    }

    @FXML
    protected void initialize() {
        setupHeader(welcomeLabel, dateTimeLabel);
        backButton.setOnAction(e -> AppRoutes.history());
        TransactionDetails details = selected;
        selected = null;
        if (details == null)
            return;

        transactionTitleLabel.setText(details.title() == null ? I18n.get("details.title") : details.title());
        amountValueLabel.setText("₱" + details.amount());
        messageValueLabel.setText(details.message() == null || details.message().isBlank()
                ? "-"
                : details.message());
        dateTimeValueLabel.setText(details.dateTime() == null ? ""
                : details.dateTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")));
        referenceValueLabel.setText(String.valueOf(details.id()));
    }
}
