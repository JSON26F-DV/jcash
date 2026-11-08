package app.controller.components;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class SidebarController extends HeaderController {
    private static final String ACTIVE_STYLE = "-fx-background-color:#EEF1FE; -fx-background-radius:16; -fx-text-fill:#3454E1; -fx-font-size:13px; -fx-font-weight:bold; -fx-padding:0 0 0 14; -fx-cursor:hand;";
    private static final String INACTIVE_STYLE = "-fx-background-color:transparent; -fx-background-radius:16; -fx-text-fill:#9DA3A9; -fx-font-size:13px; -fx-padding:0 0 0 14; -fx-cursor:hand;";

    @FXML
    private Button navDashboardButton;
    @FXML
    private Button navCashInButton;
    @FXML
    private Button navCardsButton;
    @FXML
    private Button navExpressSendButton;
    @FXML
    private Button navTransactionsButton;
    @FXML
    private Button navSettingsButton;
    @FXML
    private Label userInitialsLabel;
    @FXML
    private Label userNameLabel;

    @FXML
    public void initialize() {
        highlight(navDashboardButton, AppRoutes.DASHBOARD, AppRoutes::dashboard);
        highlight(navCashInButton, AppRoutes.CASH_IN, AppRoutes::cashIn);
        highlight(navCardsButton, AppRoutes.CARDS, AppRoutes::cards);
        highlight(navExpressSendButton, AppRoutes.EXPRESS_SEND, AppRoutes::expressSend);
        highlight(navTransactionsButton, AppRoutes.HISTORY, AppRoutes::history);
        highlight(navSettingsButton, AppRoutes.SETTINGS, AppRoutes::settings);
        if (user != null) {
            userNameLabel.setText(user.getFullName());
            userInitialsLabel.setText(initials(user));
        }
    }

    private void highlight(Button button, String page, Runnable openPage) {
        if (button == null)
            return;
        button.setOnAction(event -> openPage.run());
        Platform.runLater(() -> {
            boolean active = AppRoutes.isCurrentPage(page);
            button.setStyle(active ? ACTIVE_STYLE : INACTIVE_STYLE);
            if (button.getGraphic() instanceof SVGPath icon) {
                icon.setFill(active ? Color.web("#3454E1") : Color.web("#9DA3A9"));
            }
        });
    }

    private String initials(User user) {
        String[] parts = user.getFullName().trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty())
            return "";
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}
