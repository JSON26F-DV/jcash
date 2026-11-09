package app.controller.customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.TransactionDTO;
import app.dto.view.CardView;
import app.dto.view.UserRecord;
import app.i18n.I18n;
import app.services.CardService;
import app.services.TransactionService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class DashboardController extends HeaderController {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    private final TransactionService transactions = new TransactionService();
    private final CardService cards = new CardService();

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label balanceAmountLabel;
    @FXML
    private Label mobileNumberLabel;
    @FXML
    private Button viewMoreButton;
    @FXML
    private Button balanceVisibilityButton;
    @FXML
    private SVGPath balanceVisibilityIcon;
    @FXML
    private Button cashInShortcutButton;
    @FXML
    private HBox cardsStrip;
    @FXML
    private VBox transactionsListBox;
    private String visibleBalanceText;
    private boolean balanceVisible = true;

    @FXML
    protected void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel))
            return;
        UserRecord userRecord = cards.findUserRecord(user.getId());
        BigDecimal balance = userRecord == null ? BigDecimal.ZERO : userRecord.balances();
        visibleBalanceText = MONEY.format(balance);
        balanceAmountLabel.setText(visibleBalanceText);
        mobileNumberLabel.setText(maskMobile(userRecord == null ? null : userRecord.username()));
        viewMoreButton.setOnAction(e -> AppRoutes.history());
        cashInShortcutButton.setOnAction(e -> AppRoutes.cashIn());
        balanceVisibilityButton.setOnAction(event -> toggleBalanceVisibility());
        buildCardsStrip(userRecord, balance);
        List<TransactionDTO> latest = transactions.latestForUser(user.getId(), 5);
        for (TransactionDTO transaction : latest)
            transactionsListBox.getChildren().add(row(transaction));
    }

    private void toggleBalanceVisibility() {
        balanceVisible = !balanceVisible;
        balanceAmountLabel.setText(balanceVisible ? visibleBalanceText : "••••••");
        balanceVisibilityButton
                .setAccessibleText(I18n.get(balanceVisible ? "dashboard.hideBalance" : "dashboard.showBalance"));
        balanceVisibilityIcon.setContent(balanceVisible
                ? "M12 5C7 5 2.73 8.11 1 12c1.73 3.89 6 7 11 7s9.27-3.11 11-7c-1.73-3.89-6-7-11-7zm0 11a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm0-2a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"
                : "M2 3.27 3.28 2 22 20.72 20.73 22l-3.12-3.12A11.9 11.9 0 0 1 12 20C7 20 2.73 16.89 1 13c.78-1.75 2.06-3.28 3.67-4.42L2 3.27zM12 6c5 0 9.27 3.11 11 7-.63 1.41-1.57 2.67-2.75 3.69l-3.08-3.08a5.2 5.2 0 0 0 .08-.92 5.25 5.25 0 0 0-5.25-5.25c-.32 0-.63.03-.93.08L9.2 5.65A12.6 12.6 0 0 1 12 6zM7 12.27l1.58 1.58a3.5 3.5 0 0 1 4.57-4.57l1.58 1.58a3.5 3.5 0 0 1-4.57 4.57L7 12.27z");
    }

    private void buildCardsStrip(UserRecord userRecord, BigDecimal balance) {
        cardsStrip.setAlignment(Pos.CENTER);
        cardsStrip.getChildren().clear();
        cardsStrip.getChildren().add(addCardTile());
        if (userRecord == null) {
            return;
        }
        for (CardView card : cards.listByUser(userRecord.id()).stream().limit(2).toList()) {
            cardsStrip.getChildren().add(cardTile(card, balance));
        }
    }

    private VBox addCardTile() {
        VBox tile = new VBox(8);
        tile.setAlignment(Pos.CENTER);
        tile.setMinSize(220, 188);
        tile.setPrefSize(220, 188);
        tile.setMaxSize(220, 188);
        tile.getStyleClass().add("dashboard-add-card");
        Label plus = new Label("+");
        plus.getStyleClass().add("dashboard-add-plus");
        Label text = new Label(I18n.get("dashboard.addCard"));
        text.getStyleClass().add("dashboard-add-label");
        tile.getChildren().addAll(plus, text);
        tile.setOnMouseClicked(event -> AppRoutes.newCard());
        return tile;
    }

    private VBox cardTile(CardView card, BigDecimal balance) {
        VBox tile = new VBox(8);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setMinWidth(300);
        tile.setPrefWidth(300);
        tile.setMaxWidth(300);
        tile.setAlignment(Pos.CENTER);
        tile.getChildren().add(cardVisual(card, balance));
        return tile;
    }

    private StackPane cardVisual(CardView card, BigDecimal balance) {
        StackPane visual = new StackPane();
        visual.getStyleClass().addAll("credit-card", "dashboard-card", "card-" + card.color());
        visual.setMinSize(300, 188);
        visual.setPrefSize(300, 188);
        visual.setMaxSize(300, 188);
        Rectangle clip = new Rectangle(300, 188);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        visual.setClip(clip);

        Pane decor = new Pane();
        decor.setPrefSize(300, 188);
        decor.setMouseTransparent(true);
        Circle topRight = new Circle(300, 0, 70);
        topRight.getStyleClass().add("card-circle");
        Circle bottomLeft = new Circle(0, 188, 64);
        bottomLeft.getStyleClass().add("card-circle");
        decor.getChildren().addAll(topRight, bottomLeft);

        BorderPane body = new BorderPane();
        body.getStyleClass().add("dashboard-card-body");
        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);
        Label type = cardLabel(CardView.localizedType(card.cardType()).toUpperCase(I18n.locale()) + " CARD",
                "card-label");
        Region headingSpace = new Region();
        HBox.setHgrow(headingSpace, Priority.ALWAYS);
        heading.getChildren().addAll(type, headingSpace, cardLabel("VISA", "card-visa"));

        VBox center = new VBox(2);
        center.setAlignment(Pos.CENTER_LEFT);
        center.getChildren().addAll(cardLabel(card.typeLabel(), "card-tiny"), chip());

        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_LEFT);
        VBox numberAndName = new VBox(1, cardLabel(card.maskedNumber(), "dashboard-card-number"),
                cardLabel(card.holderName(), "dashboard-card-holder"));
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);
        VBox expiry = new VBox(1, cardLabel("EXPIRES", "dashboard-card-meta"),
                cardLabel(card.expiryDate(), "dashboard-card-expiry"));
        expiry.setAlignment(Pos.BOTTOM_RIGHT);
        footer.getChildren().addAll(numberAndName, footerSpace, expiry);
        body.setTop(heading);
        body.setCenter(center);
        body.setBottom(footer);
        visual.getChildren().addAll(decor, body);
        return visual;
    }

    private static StackPane chip() {
        StackPane chip = new StackPane();
        chip.setMinSize(30, 22);
        chip.setPrefSize(30, 22);
        chip.setMaxSize(30, 22);
        Rectangle base = new Rectangle(30, 22);
        base.setArcWidth(6);
        base.setArcHeight(6);
        base.getStyleClass().add("card-chip-base");
        SVGPath detail = new SVGPath();
        detail.setContent("M4 3 H26 Q28 3 28 5 V17 Q28 19 26 19 H4 Q2 19 2 17 V5 Q2 3 4 3 Z "
                + "M2 8 H10 V14 H2 M10 8 H19 V3 M10 14 H19 V19 M19 8 H28 M19 14 H28");
        detail.getStyleClass().add("card-chip-detail");
        detail.setStrokeWidth(1);
        chip.getChildren().addAll(base, detail);
        return chip;
    }

    private static Label cardLabel(String text, String styleClass) {
        Label label = new Label(text == null ? "" : text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private static String maskMobile(String mobile) {
        String lastFour = mobile == null || mobile.length() < 4 ? "----" : mobile.substring(mobile.length() - 4);
        return "*** **** " + lastFour;
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
