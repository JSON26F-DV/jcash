package app.controller.common;

import java.io.IOException;

import app.i18n.I18n;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AppRoutes {
    private static Stage stage;
    private static String currentPage;

    private AppRoutes() {
    }

    public static final String LANDING = "/app/view/LandingPage.fxml";
    public static final String FORGOT_PASSWORD = "/app/view/auth/ForgotPassword.fxml";
    public static final String VERIFY_OTP = "/app/view/auth/VerifyOtp.fxml";
    public static final String CHANGE_PASSWORD = "/app/view/auth/ChangePassword.fxml";
    public static final String REGISTER = "/app/view/RegisterPage.fxml";
    public static final String TERMS = "/app/view/pages/Term.fxml";
    public static final String PRIVACY = "/app/view/pages/Privacy.fxml";
    public static final String DASHBOARD = "/app/view/customer/Dashboard.fxml";
    public static final String CASH_IN = "/app/view/customer/CashIn.fxml";
    public static final String EXPRESS_SEND = "/app/view/customer/ExpressSend.fxml";
    public static final String CONFIRMATION = "/app/view/auth/Confirmation.fxml";
    public static final String DETAILS = "/app/view/customer/Details.fxml";
    public static final String HISTORY = "/app/view/customer/History.fxml";
    public static final String CARDS = "/app/view/customer/cards.fxml";
    public static final String NEW_CARD = "/app/view/customer/new_card.fxml";
    public static final String SETTINGS = "/app/view/customer/Settings.fxml";
    public static final String DELETED_ACCOUNT = "/app/view/pages/DeletedAccount.fxml";

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void landing() {
        go(LANDING);
    }

    public static void deletedAccount() {
        go(DELETED_ACCOUNT);
    }

    public static void forgotPassword() {
        go(FORGOT_PASSWORD);
    }

    public static void verifyOtp() {
        go(VERIFY_OTP);
    }

    public static void changePassword() {
        go(CHANGE_PASSWORD);
    }

    public static void register() {
        go(REGISTER);
    }

    public static void terms() {
        go(TERMS);
    }

    public static void privacy() {
        go(PRIVACY);
    }

    public static void dashboard() {
        go(DASHBOARD);
    }

    public static void cashIn() {
        go(CASH_IN);
    }

    public static void expressSend() {
        go(EXPRESS_SEND);
    }

    public static void confirmation() {
        go(CONFIRMATION);
    }

    public static void details() {
        go(DETAILS);
    }

    public static void history() {
        go(HISTORY);
    }

    public static void cards() {
        go(CARDS);
    }

    public static void newCard() {
        go(NEW_CARD);
    }

    public static void settings() {
        go(SETTINGS);
    }

    private static void go(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(AppRoutes.class.getResource(path), I18n.bundle());
            Parent root = loader.load();
            root.getStylesheets().add(AppRoutes.class.getResource("/app/css/i18n.css").toExternalForm());
            if (java.util.Set.of("ja", "ko", "zh").contains(I18n.locale().getLanguage())) {
                root.getStyleClass().add("locale-cjk");
            }
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
            stage.show();
            currentPage = path;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open " + path + ": " + e.getMessage(), e);
        }
    }

    public static void setLanguage(String tag) {
        I18n.setLanguage(tag);
        reload();
    }

    public static void reload() {
        if (currentPage == null) {
            landing();
        } else {
            go(currentPage);
        }
    }

    public static boolean isCurrentPage(String path) {
        return path.equals(currentPage);
    }

}
