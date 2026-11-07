package app.controller.customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.view.SettingsDetails;
import app.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SettingsController extends HeaderController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label profileInitialsLabel;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailUsernameLabel;
    @FXML
    private Label roleValueLabel;
    @FXML
    private Label statusValueLabel;
    @FXML
    private Label sessionStartedValueLabel;
    @FXML
    private Label sessionTokenValueLabel;
    @FXML
    private Label fullNameValueLabel;
    @FXML
    private Label emailValueLabel;
    @FXML
    private Label mobileNumberValueLabel;
    @FXML
    private Label balanceValueLabel;
    @FXML
    private Button dashboardLinkButton;
    @FXML
    private Button newTransactionLinkButton;
    @FXML
    private Button transactionHistoryLinkButton;
    @FXML
    private Button signOutButton;

    @FXML
    public void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        SettingsDetails settings = SettingsDetails.from(user);
        profileInitialsLabel.setText(settings.initials());
        profileNameLabel.setText(settings.fullName());
        profileEmailUsernameLabel.setText(settings.email() + " · " + settings.mobileNumber());
        roleValueLabel.setText("Customer");
        statusValueLabel.setText("ACTIVE");
        fullNameValueLabel.setText(settings.fullName());
        emailValueLabel.setText(settings.email().isBlank() ? "-" : settings.email());
        mobileNumberValueLabel.setText(settings.mobileNumber());
        balanceValueLabel.setText("₱" + settings.balance());
        LocalDateTime startedAt = settings.sessionStartedAt();
        sessionStartedValueLabel.setText(startedAt == null ? "-"
                : startedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
        sessionTokenValueLabel.setText(settings.sessionToken() == null ? "-" : settings.sessionToken());
        dashboardLinkButton.setOnAction(event -> AppRoutes.dashboard());
        newTransactionLinkButton.setOnAction(event -> AppRoutes.expressSend());
        transactionHistoryLinkButton.setOnAction(event -> AppRoutes.history());
        signOutButton.setOnAction(event -> {
            AuthService.logout();
            AppRoutes.landing();
        });
    }

}
