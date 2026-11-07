package app.controller.auth;

import app.controller.common.AppRoutes;
import app.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LandingPageController {
    private final AuthService authService = new AuthService();

    @FXML
    private TextField mobileNumberField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button javaBtn;
    @FXML
    private Button forgotPasswordButton;
    @FXML
    private Button registerBtn;
    @FXML
    private Label loginHeadingLabel;
    @FXML
    private Label termsLink;
    @FXML
    private Label privacyLink;
    @FXML
    private Label errorLabel;

    @FXML
    protected void initialize() {
        if (javaBtn != null)
            javaBtn.setOnAction(e -> handleLogin());
        if (forgotPasswordButton != null)
            forgotPasswordButton.setOnAction(e -> AppRoutes.forgotPassword());
        if (registerBtn != null)
            registerBtn.setOnAction(e -> AppRoutes.register());
        if (termsLink != null)
            termsLink.setOnMouseClicked(e -> AppRoutes.terms());
        if (privacyLink != null)
            privacyLink.setOnMouseClicked(e -> AppRoutes.privacy());
    }

    private void handleLogin() {
        try {
            if (authService.login(mobileNumberField.getText(), passwordField.getText())) {
                AppRoutes.dashboard();
                return;
            }
            if ("Too many failed attempts. Exiting.".equals(authService.message())) {
                errorLabel.setText(authService.message());
                Platform.exit();
                return;
            }
            errorLabel.setText(authService.message());
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void selectEnglish() {
        setLoginLanguage("Log into JCash Bank", "Mobile number", "Enter your 6-Digit PIN", "Log in",
                "Create new account", null);
    }

    @FXML
    private void selectFilipino() {
        setLoginLanguage("Mag-log in sa JCash Bank", "Numero ng mobile", "Ilagay ang iyong 6-na-digit na PIN",
                "Mag-log in", "Gumawa ng bagong account", null);
    }

    @FXML
    private void selectBisaya() {
        setLoginLanguage("Log in sa JCash Bank", "Numero sa mobile", "Isulod ang imong 6-ka-digit nga PIN", "Log in",
                "Paghimo og bag-ong account", null);
    }

    @FXML
    private void selectSpanish() {
        setLoginLanguage("Inicia sesión en JCash Bank", "Número de móvil", "Introduce tu PIN de 6 dígitos",
                "Iniciar sesión", "Crear una cuenta nueva", null);
    }

    @FXML
    private void selectJapanese() {
        setLoginLanguage("JCash Bankにログイン", "携帯電話番号", "6桁のPINを入力してください", "ログイン", "新しいアカウントを作成", "Noto Sans CJK JP");
    }

    @FXML
    private void selectKorean() {
        setLoginLanguage("JCash Bank 로그인", "휴대폰 번호", "6자리 PIN을 입력하세요", "로그인", "새 계정 만들기", "Noto Sans CJK KR");
    }

    @FXML
    private void selectChinese() {
        setLoginLanguage("登录 JCash Bank", "手机号码", "请输入 6 位 PIN", "登录", "创建新账户", "Noto Sans CJK SC");
    }

    private void setLoginLanguage(String heading, String mobilePrompt, String pinPrompt, String loginText,
            String registerText, String fontFamily) {
        loginHeadingLabel.setText(heading);
        mobileNumberField.setPromptText(mobilePrompt);
        passwordField.setPromptText(pinPrompt);
        javaBtn.setText(loginText);
        registerBtn.setText(registerText);
        String fontStyle = fontFamily == null ? "" : "-fx-font-family: '" + fontFamily + "';";
        loginHeadingLabel.setStyle(fontStyle);
        mobileNumberField.setStyle(fontStyle);
        passwordField.setStyle(fontStyle);
        javaBtn.setStyle(fontStyle);
        registerBtn.setStyle(fontStyle);
        errorLabel.setText("");
    }
}
