package app.controller.customer;

import java.util.List;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.view.CardView;
import app.dto.view.UserRecord;
import app.i18n.I18n;
import app.services.CardService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CardsController extends HeaderController {
    private static final String PANEL_STYLE = "-fx-background-color:#FFFFFF; -fx-background-radius:26; "
            + "-fx-border-color:#EEF1F6; -fx-border-width:1; -fx-border-radius:26; "
            + "-fx-effect:dropshadow(gaussian, rgba(30,40,80,0.10), 24, 0, 0, 12);";
    private final CardService cardService = new CardService();

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Button newCardButton;
    @FXML
    private GridPane cardsGrid;

    @FXML
    public void initialize() {
        if (!setupHeader(welcomeLabel, dateTimeLabel)) {
            AppRoutes.landing();
            return;
        }
        newCardButton.setOnAction(event -> AppRoutes.newCard());
        UserRecord userRecord;
        List<CardView> cards;
        try {
            userRecord = cardService.findUserRecord(user.getId());
            if (userRecord == null) {
                showLoadError(I18n.get("cards.accountError"));
                return;
            }
            cards = cardService.listByUser(userRecord.id());
        } catch (RuntimeException exception) {
            showLoadError(I18n.get("cards.loadError"));
            return;
        }
        if (cards.isEmpty()) {
            Label empty = new Label(I18n.get("cards.empty"));
            empty.getStyleClass().add("card-empty");
            cardsGrid.add(empty, 0, 0);
            return;
        }
        int index = 0;
        for (CardView card : cards) {
            cardsGrid.add(createCardPanel(card, userRecord.balances()), index % 2, index / 2);
            index++;
        }
    }

    private void showLoadError(String message) {
        Label error = new Label(message);
        error.getStyleClass().add("card-load-error");
        cardsGrid.add(error, 0, 0);
    }

    private VBox createCardPanel(CardView card, java.math.BigDecimal balance) {
        VBox panel = new VBox(14);
        panel.setStyle(PANEL_STYLE);
        panel.setPadding(new Insets(22, 24, 22, 24));

        panel.getChildren().addAll(cardVisual(card, balance), statusRow(card), actionButton(card));
        return panel;
    }

    private StackPane cardVisual(CardView card, java.math.BigDecimal balance) {
        StackPane visual = new StackPane();
        visual.getStyleClass().addAll("credit-card", "card-" + card.color());
        visual.setMinSize(432, 270);
        visual.setPrefSize(432, 270);
        visual.setMaxSize(432, 270);
        Rectangle clip = new Rectangle(432, 270);
        clip.setArcWidth(44);
        clip.setArcHeight(44);
        visual.setClip(clip);

        Pane decor = new Pane();
        decor.setPrefSize(432, 270);
        decor.setMouseTransparent(true);
        Circle topRight = new Circle(432, 0, 95);
        topRight.getStyleClass().add("card-circle");
        Circle bottomLeft = new Circle(0, 270, 90);
        bottomLeft.getStyleClass().add("card-circle");
        decor.getChildren().addAll(topRight, bottomLeft);

        BorderPane body = new BorderPane();
        body.getStyleClass().add("card-body");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label type = text(CardView.localizedType(card.cardType()).toUpperCase(I18n.locale()) + " CARD", "card-label");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        top.getChildren().addAll(type, topSpacer, text("VISA", "card-visa"));

        VBox center = new VBox(4);
        center.setAlignment(Pos.CENTER_LEFT);
        center.getChildren().addAll(text(card.typeLabel(), "card-tiny"), chip());
        BorderPane.setAlignment(center, Pos.CENTER_LEFT);

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.BOTTOM_LEFT);
        VBox number = new VBox(2, text(card.maskedNumber(), "card-number"),
                text(card.holderName(), "card-value"));
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        VBox expiry = new VBox(2, text("EXPIRES", "card-value"),
                text(card.expiryDate(), "card-expiry"));
        expiry.setAlignment(Pos.BOTTOM_RIGHT);
        bottom.getChildren().addAll(number, bottomSpacer, expiry);

        body.setTop(top);
        body.setCenter(center);
        body.setBottom(bottom);
        visual.getChildren().addAll(decor, body);
        return visual;
    }

    private HBox statusRow(CardView card) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label status = new Label(I18n.get(card.frozen() ? "cards.frozen" : "cards.active"));
        status.getStyleClass().addAll("pill", card.frozen() ? "pill-red" : "pill-green");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label caption = text("VISA · " + CardView.localizedType(card.cardType()).toUpperCase(I18n.locale()), null);
        caption.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:#9DA3A9;");
        row.getChildren().addAll(status, spacer, caption);
        return row;
    }

    private static StackPane chip() {
        StackPane chip = new StackPane();
        chip.setMinSize(38, 28);
        chip.setPrefSize(38, 28);
        chip.setMaxSize(38, 28);
        Rectangle body = new Rectangle(38, 28);
        body.setArcWidth(8);
        body.setArcHeight(8);
        body.getStyleClass().add("card-chip-base");
        SVGPath chipDetails = new SVGPath();
        chipDetails.setContent("M5 3 H33 Q35 3 35 5 V23 Q35 25 33 25 H5 Q3 25 3 23 V5 Q3 3 5 3 Z "
                + "M3 9 H13 V19 H3 M13 9 H25 V3 M13 19 H25 V25 M25 9 H35 M25 19 H35");
        chipDetails.getStyleClass().add("card-chip-detail");
        chipDetails.setStrokeWidth(1.2);
        chip.getChildren().addAll(body, chipDetails);
        return chip;
    }

    private Button actionButton(CardView card) {
        Button button = new Button(I18n.get(card.frozen() ? "cards.unfreeze" : "cards.freeze"));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(44);
        button.setStyle("-fx-background-radius:16; -fx-border-radius:16; -fx-font-size:13px; -fx-font-weight:bold;");
        button.getStyleClass().add("btn-outline");
        button.setOnAction(event -> {
            try {
                cardService.toggleFreeze(user.getId(), card.id());
                AppRoutes.cards();
            } catch (RuntimeException exception) {
                button.setText(I18n.get("cards.retry"));
            }
        });
        return button;
    }

    private static Label text(String value, String styleClass) {
        Label label = new Label(value == null ? "" : value);
        if (styleClass != null) {
            label.getStyleClass().add(styleClass);
        }
        return label;
    }

}
