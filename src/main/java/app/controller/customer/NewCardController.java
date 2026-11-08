package app.controller.customer;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import app.controller.common.AppRoutes;
import app.controller.common.HeaderController;
import app.dto.view.CardView;
import app.dto.view.UserRecord;
import app.services.CardService;
import app.services.InsufficientBalanceException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

public class NewCardController extends HeaderController {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    private final CardService cardService = new CardService();
    private UserRecord userRecord;
    private String selectedColor = "blue";
    private String selectedCardType = "debit";
    private final String expiryDate = YearMonth.now().plusYears(5).format(EXPIRY_FORMAT);
    private StackPane previewCard;
    private Scale previewScale;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label cardTypePreviewLabel;
    private Label cardPurposePreviewLabel;
    @FXML
    private VBox newCardColumn;
    @FXML
    private Label feeLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label balanceError;
    @FXML
    private Label formError;
    @FXML
    private Button debitButton;
    @FXML
    private Button creditButton;
    @FXML
    private Button prepaidButton;
    @FXML
    private Button blueButton;
    @FXML
    private Button graphiteButton;
    @FXML
    private Button onyxButton;
    @FXML
    private Button emeraldButton;
    @FXML
    private StackPane cardPreviewHost;
    @FXML
    private GridPane newCardGrid;
    @FXML
    private VBox formPanel;
    @FXML
    private VBox previewPanel;
    @FXML
    private PasswordField pinField;
    @FXML
    private PasswordField confirmPinField;
    @FXML
    private Label pinError;
    @FXML
    private Label confirmPinError;
    @FXML
    private Button createCardButton;
    @FXML
    private Button backButton;
    @FXML
    private VBox existingCardsList;

    @FXML
    public void initialize() {
        setupHeader(welcomeLabel, dateTimeLabel);
        backButton.setOnAction(event -> AppRoutes.cards());
        createCardButton.setOnAction(event -> createCard());
        configureTypeButtons();
        configureResponsiveLayout();
        buildPreviewCard();
        configureColorButtons();
        if (user == null) {
            createCardButton.setDisable(true);
            formError.setText("Sign in to create a card.");
            return;
        }
        userRecord = cardService.findUserRecord(user.getId());
        if (userRecord == null) {
            createCardButton.setDisable(true);
            formError.setText("Unable to load your account.");
            return;
        }
        feeLabel.setText(MONEY.format(CardService.CARD_FEE));
        balanceLabel.setText("Your balance: " + MONEY.format(userRecord.balances()));
        updateBalanceEligibility();
        updatePreview();
        loadExistingCards();
    }

    private static final String SEG_BASE = "-fx-background-radius:16; -fx-border-radius:16;"
            + " -fx-border-width:1; -fx-padding:10 12 10 12; -fx-cursor:hand;";
    private static final String SEG_OFF = "-fx-background-color:transparent; -fx-border-color:transparent;" + SEG_BASE;
    private static final String SEG_ON = "-fx-background-color:#FFFFFF; -fx-border-color:#DDE2EA;" + SEG_BASE;

    private static final String TILE_BASE = "-fx-background-radius:16; -fx-border-radius:16;"
            + " -fx-border-width:2; -fx-padding:12; -fx-cursor:hand;";
    private static final String TILE_OFF = "-fx-background-color:#FFFFFF; -fx-border-color:transparent;" + TILE_BASE;
    private static final String TILE_ON = "-fx-background-color:#F7F9FC; -fx-border-color:#3454E1;" + TILE_BASE;

    private void configureTypeButtons() {
        debitButton.setOnAction(event -> selectType("debit", debitButton));
        creditButton.setOnAction(event -> selectType("credit", creditButton));
        prepaidButton.setOnAction(event -> selectType("prepaid", prepaidButton));
        selectType("debit", debitButton);
    }

    private void selectType(String type, Button selected) {
        selectedCardType = type;
        for (Button button : List.of(debitButton, creditButton, prepaidButton)) {
            button.getStyleClass().removeAll("seg-card", "seg-card-active");
            button.setStyle(button == selected ? SEG_ON : SEG_OFF);
        }
        updatePreview();
    }

    private void configureColorButtons() {
        blueButton.setOnAction(event -> selectColor("blue", blueButton));
        graphiteButton.setOnAction(event -> selectColor("graphite", graphiteButton));
        onyxButton.setOnAction(event -> selectColor("onyx", onyxButton));
        emeraldButton.setOnAction(event -> selectColor("emerald", emeraldButton));
        selectColor("blue", blueButton);
    }

    @FXML
    private void selectBlue() {
        selectColor("blue", blueButton);
    }

    @FXML
    private void selectGraphite() {
        selectColor("graphite", graphiteButton);
    }

    @FXML
    private void selectOnyx() {
        selectColor("onyx", onyxButton);
    }

    @FXML
    private void selectEmerald() {
        selectColor("emerald", emeraldButton);
    }

    private void selectColor(String color, Button selected) {
        selectedColor = color;
        if (previewCard != null) {
            previewCard.getStyleClass().removeAll("card-blue", "card-graphite", "card-onyx", "card-emerald");
            previewCard.getStyleClass().add("card-" + color);
        }
        for (Button button : List.of(blueButton, graphiteButton, onyxButton, emeraldButton)) {
            button.getStyleClass().removeAll("style-tile", "style-tile-active");
            button.setStyle(button == selected ? TILE_ON : TILE_OFF);
        }
    }

    private void configureResponsiveLayout() {
        newCardColumn.widthProperty().addListener((observable, oldWidth, width) -> {
            boolean stacked = width.doubleValue() < 850;
            newCardGrid.getColumnConstraints().clear();
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(stacked ? 100 : 58.33);
            newCardGrid.getColumnConstraints().add(column);
            if (!stacked) {
                ColumnConstraints second = new ColumnConstraints();
                second.setPercentWidth(41.67);
                newCardGrid.getColumnConstraints().add(second);
            }
            GridPane.setColumnIndex(formPanel, 0);
            GridPane.setRowIndex(formPanel, 0);
            GridPane.setColumnIndex(previewPanel, stacked ? 0 : 1);
            GridPane.setRowIndex(previewPanel, stacked ? 1 : 0);
            newCardGrid.setHgap(stacked ? 0 : 20);
        });
        cardPreviewHost.widthProperty().addListener((observable, oldWidth, width) -> {
            double previewWidth = Math.max(0, width.doubleValue());
            cardPreviewHost.setPrefHeight(previewWidth * 270 / 432);
            if (previewScale != null) {
                double scale = Math.min(1, previewWidth / 432);
                previewScale.setX(scale);
                previewScale.setY(scale);
            }
        });
    }

    private void buildPreviewCard() {
        previewCard = new StackPane();
        previewCard.getStyleClass().addAll("credit-card", "card-blue");
        previewCard.setMinSize(432, 270);
        previewCard.setPrefSize(432, 270);
        previewCard.setMaxSize(432, 270);
        Rectangle clip = new Rectangle(432, 270);
        clip.setArcWidth(44);
        clip.setArcHeight(44);
        previewCard.setClip(clip);

        Pane decoration = new Pane();
        decoration.setPrefSize(432, 270);
        decoration.setMouseTransparent(true);
        Circle topRight = new Circle(432, 0, 95);
        topRight.getStyleClass().add("card-circle");
        Circle bottomLeft = new Circle(0, 270, 90);
        bottomLeft.getStyleClass().add("card-circle");
        decoration.getChildren().addAll(topRight, bottomLeft);

        BorderPane body = new BorderPane();
        body.getStyleClass().add("card-body");
        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);
        cardTypePreviewLabel = cardLabel("DEBIT CARD", "card-label");
        Region headingSpace = new Region();
        HBox.setHgrow(headingSpace, Priority.ALWAYS);
        heading.getChildren().addAll(cardTypePreviewLabel, headingSpace, cardLabel("VISA", "card-visa"));

        VBox center = new VBox(4);
        center.setAlignment(Pos.CENTER_LEFT);
        cardPurposePreviewLabel = cardLabel(CardView.labelForType(selectedCardType), "card-tiny");
        center.getChildren().addAll(cardPurposePreviewLabel, createChip());

        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_LEFT);
        VBox cardNumberAndHolder = new VBox(2, cardLabel("•••• 0000", "card-number"),
                cardLabel("CARDHOLDER", "card-value"));
        Region footerSpace = new Region();
        HBox.setHgrow(footerSpace, Priority.ALWAYS);
        VBox expiry = new VBox(2, cardLabel("EXPIRES", "card-value"), cardLabel(expiryDate, "card-expiry"));
        expiry.setAlignment(Pos.BOTTOM_RIGHT);
        footer.getChildren().addAll(cardNumberAndHolder, footerSpace, expiry);
        body.setTop(heading);
        body.setCenter(center);
        body.setBottom(footer);
        previewCard.getChildren().addAll(decoration, body);
        previewScale = new Scale(1, 1);
        previewCard.getTransforms().add(previewScale);
        cardPreviewHost.getChildren().setAll(previewCard);
        updatePreview();
    }

    private static StackPane createChip() {
        StackPane chip = new StackPane();
        chip.setMinSize(38, 28);
        chip.setPrefSize(38, 28);
        chip.setMaxSize(38, 28);
        Rectangle base = new Rectangle(38, 28);
        base.setArcWidth(8);
        base.setArcHeight(8);
        base.getStyleClass().add("card-chip-base");
        SVGPath details = new SVGPath();
        details.setContent("M5 3 H33 Q35 3 35 5 V23 Q35 25 33 25 H5 Q3 25 3 23 V5 Q3 3 5 3 Z "
                + "M3 9 H13 V19 H3 M13 9 H25 V3 M13 19 H25 V25 M25 9 H35 M25 19 H35");
        details.getStyleClass().add("card-chip-detail");
        details.setStrokeWidth(1.2);
        chip.getChildren().addAll(base, details);
        return chip;
    }

    private void updatePreview() {
        if (previewCard == null) {
            return;
        }
        cardTypePreviewLabel.setText(selectedCardType.toUpperCase(Locale.ROOT) + " CARD");
        cardPurposePreviewLabel.setText(CardView.labelForType(selectedCardType));
        BorderPane body = (BorderPane) previewCard.getChildren().get(1);
        HBox footer = (HBox) body.getBottom();
        VBox cardNumberAndHolder = (VBox) footer.getChildren().get(0);
        ((Label) cardNumberAndHolder.getChildren().get(1)).setText(
                userRecord == null ? "CARDHOLDER" : userRecord.fullName().toUpperCase(Locale.ROOT));
        VBox expiry = (VBox) footer.getChildren().get(2);
        ((Label) expiry.getChildren().get(1)).setText(expiryDate);
    }

    private static Label cardLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private void createCard() {
        clearErrors();
        String type = selectedType();
        String pin = pinField.getText();
        String confirmPin = confirmPinField.getText();
        boolean valid = true;

        if (pin == null || !pin.matches("\\d{4}")) {
            pinError.setText("PIN must be exactly 4 digits.");
            valid = false;
        }
        if (confirmPin == null || !confirmPin.equals(pin)) {
            confirmPinError.setText("PINs do not match.");
            valid = false;
        }
        if (!valid) {
            return;
        }
        try {
            CardService.CardCreationResult result = cardService.createCard(
                    userRecord.id(), type, selectedColor, expiryDate, pin);
            userRecord = result.userRecord();
            balanceLabel.setText("Your balance: " + MONEY.format(userRecord.balances()));
            AppRoutes.cards();
        } catch (InsufficientBalanceException exception) {
            balanceError.setText("Insufficient balance. A new card costs " + MONEY.format(CardService.CARD_FEE) + ".");
            createCardButton.setDisable(true);
        } catch (RuntimeException exception) {
            formError.setText("Card creation failed. Please try again.");
        }
    }

    private String selectedType() {
        return selectedCardType;
    }

    private void updateBalanceEligibility() {
        boolean insufficient = userRecord.balances().compareTo(CardService.CARD_FEE) < 0;
        createCardButton.setDisable(insufficient);
        balanceError.setText(insufficient
                ? "Insufficient balance. A new card costs " + MONEY.format(CardService.CARD_FEE) + "."
                : "");
    }

    private void loadExistingCards() {
        existingCardsList.getChildren().clear();
        List<CardView> existing = cardService.listByUser(userRecord.id());
        if (existing.isEmpty()) {
            Label none = new Label("No cards yet.");
            none.getStyleClass().add("card-empty");
            existingCardsList.getChildren().add(none);
            return;
        }
        for (CardView card : existing.stream().limit(3).toList()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            StackPane mini = new StackPane();
            mini.setMinSize(40, 26);
            mini.setPrefSize(40, 26);
            mini.setMaxSize(40, 26);
            mini.setStyle("-fx-background-color:" + colorHex(card.color()) + "; -fx-background-radius:7;");
            Label description = new Label("Visa · " + capitalize(card.cardType()) + " · " + card.maskedNumber());
            description.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#333948;");
            Label meta = new Label(capitalize(card.color()) + " · Expires " + card.expiryDate());
            meta.setStyle("-fx-font-size:10px; -fx-text-fill:#9DA3A9;");
            VBox text = new VBox(2, description, meta);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label status = new Label(card.frozen() ? "FROZEN" : "ACTIVE");
            status.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:"
                    + (card.frozen() ? "#E2557A" : "#2FA672") + ";");
            row.getChildren().addAll(mini, text, spacer, status);
            existingCardsList.getChildren().add(row);
        }
    }

    private void clearErrors() {
        pinError.setText("");
        confirmPinError.setText("");
        formError.setText("");
        if (userRecord != null && userRecord.balances().compareTo(CardService.CARD_FEE) >= 0) {
            balanceError.setText("");
        }
    }

    private static String colorHex(String color) {
        return switch (color) {
            case "graphite" -> "#EBEBEB";
            case "onyx" -> "#22252E";
            case "emerald" -> "#0E9F6E";
            default -> "#3454E1";
        };
    }

    private static String capitalize(String value) {
        return value.isEmpty() ? value : value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}