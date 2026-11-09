package app.controller.customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.view.SettingsDetails;
import app.i18n.I18n;
import app.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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
    private Button deleteAccountButton;
    @FXML
    private Button langEnBtn;
    @FXML
    private Button langFilBtn;
    @FXML
    private Button langCebBtn;
    @FXML
    private Button langEsBtn;
    @FXML
    private Button langJaBtn;
    @FXML
    private Button langKoBtn;
    @FXML
    private Button langZhBtn;

    @FXML
    public void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        SettingsDetails settings = SettingsDetails.from(user);
        profileInitialsLabel.setText(settings.initials());
        profileNameLabel.setText(settings.fullName());
        profileEmailUsernameLabel.setText(settings.email() + " · " + settings.mobileNumber());
        roleValueLabel.setText(I18n.get("settings.customer"));
        statusValueLabel.setText(I18n.get("settings.active"));
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
        deleteAccountButton.setOnAction(event -> confirmDeleteAccount());
        signOutButton.setOnAction(event -> {
            AuthService.logout();
            AppRoutes.landing();
        });
        highlightActiveLanguage();
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Node node && node.getId() != null) {
            AppRoutes.setLanguage(node.getId());
        }
    }

    @FXML
    private void selectLocale(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Node node && node.getId() != null) {
            AppRoutes.setLanguage(node.getId());
        }
    }

    private void highlightActiveLanguage() {
        String currentLang = I18n.locale().getLanguage();
        List<Button> buttons = List.of(langEnBtn, langFilBtn, langCebBtn, langEsBtn, langJaBtn, langKoBtn, langZhBtn);
        for (Button btn : buttons) {
            if (btn == null)
                continue;
            boolean active = btn.getId() != null && btn.getId().equalsIgnoreCase(currentLang);
            if (active) {
                btn.setStyle("-fx-background-color:#EEF1FE; -fx-background-radius:14; -fx-border-color:#3454E1;"
                        + " -fx-border-width:1.5; -fx-border-radius:14; -fx-text-fill:#3454E1; -fx-font-size:12px;"
                        + " -fx-font-weight:bold; -fx-padding:8 16 8 16; -fx-cursor:hand;");
            } else {
                btn.setStyle("-fx-background-color:#F7F9FC; -fx-background-radius:14; -fx-border-color:#EEF1F6;"
                        + " -fx-border-width:1; -fx-border-radius:14; -fx-text-fill:#333948; -fx-font-size:12px;"
                        + " -fx-padding:8 16 8 16; -fx-cursor:hand;");
            }
        }
    }

    private void confirmDeleteAccount() {
        TextField input = new TextField();
        input.setPromptText(I18n.get("settings.delete.prompt"));
        input.setStyle("-fx-background-color:#FFFFFF; -fx-background-radius:12; -fx-border-color:#DDE2EA;"
                + " -fx-border-radius:12; -fx-border-width:1; -fx-padding:0 14 0 14; -fx-pref-height:44;"
                + " -fx-font-size:13;");
        Label hint = new Label(I18n.get("settings.delete.warning"));
        hint.setStyle("-fx-font-size:12px; -fx-text-fill:#9DA3A9;");
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("settings.delete.title"));
        dialog.setHeaderText(I18n.get("settings.delete.header"));
        ButtonType confirm = new ButtonType(I18n.get("settings.delete"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().setStyle("-fx-background-color:#FFFFFF; -fx-font-family:'Inter','Noto Sans CJK JP','Noto Sans CJK KR','Noto Sans CJK SC',sans-serif; -fx-padding:18;");
        dialog.getDialogPane().setContent(new VBox(10, hint, input));
        dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);
        Node confirmButton = dialog.getDialogPane().lookupButton(confirm);
        confirmButton.setStyle("-fx-background-color:#E2557A; -fx-background-radius:16; -fx-text-fill:#FFFFFF;"
                + " -fx-font-size:13px; -fx-font-weight:bold; -fx-padding:0 18 0 18; -fx-pref-height:40; -fx-cursor:hand;");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL))
                .setStyle("-fx-background-color:#FFFFFF; -fx-background-radius:16; -fx-border-color:#DDE2EA;"
                        + " -fx-border-width:1; -fx-border-radius:16; -fx-text-fill:#333948; -fx-font-size:13px;"
                        + " -fx-font-weight:bold; -fx-padding:0 18 0 18; -fx-pref-height:40; -fx-cursor:hand;");
        confirmButton.setDisable(true);
        String confirmationText = I18n.get("settings.delete.confirmText");
        input.textProperty()
                .addListener((observable, old, value) -> confirmButton.setDisable(!confirmationText.equals(value)));
        dialog.setResultConverter(type -> type == confirm ? input.getText() : null);
        dialog.showAndWait()
                .filter(confirmationText::equals)
                .ifPresent(value -> {
                    if (new AuthService().deleteAccount(user.getId())) {
                        AuthService.logout();
                        AppRoutes.deletedAccount();
                    }
                });
    }

}
