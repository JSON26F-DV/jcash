package app.dto.view;

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

    public static String labelForType(String cardType) {
        if (cardType == null) {
            return "Checking account";
        }
        return switch (cardType.toLowerCase(java.util.Locale.ROOT)) {
            case "credit" -> "Credit limit";
            case "prepaid" -> "Holiday fund";
            default -> "Checking account";
        };
    }
}