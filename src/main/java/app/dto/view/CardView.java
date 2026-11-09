package app.dto.view;

import app.i18n.I18n;

public record CardView(
        long id,
        String cardType,
        String color,
        String maskedNumber,
        String expiryDate,
        boolean frozen,
        String holderName) {

    public String typeLabel() {
        return labelForType(cardType);
    }

    public String localizedCardType() {
        return localizedType(cardType);
    }

    public static String localizedType(String cardType) {
        if (cardType == null) {
            return I18n.get("newCard.debit");
        }
        return switch (cardType.toLowerCase(java.util.Locale.ROOT)) {
            case "credit" -> I18n.get("newCard.credit");
            case "prepaid" -> I18n.get("newCard.prepaid");
            default -> I18n.get("newCard.debit");
        };
    }

    public static String labelForType(String cardType) {
        if (cardType == null) {
            return I18n.get("card.label.checking");
        }
        return switch (cardType.toLowerCase(java.util.Locale.ROOT)) {
            case "credit" -> I18n.get("card.label.credit");
            case "prepaid" -> I18n.get("card.label.prepaid");
            default -> I18n.get("card.label.checking");
        };
    }
}