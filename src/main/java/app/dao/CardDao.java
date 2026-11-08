package app.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.dto.view.CardView;
import app.dto.view.UserRecord;

public class CardDao {
    private static final String USER_SELECT = "SELECT id, "
            + "TRIM(CONCAT_WS(' ', NULLIF(TRIM(first_name), ''), NULLIF(TRIM(last_name), ''))) AS full_name, "
            + "mobile_number AS username, balance AS balances FROM users WHERE id = ?";

    public UserRecord findUserRecord(Connection connection, long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(USER_SELECT)) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new UserRecord(result.getLong("id"), result.getString("full_name"),
                        result.getString("username"), result.getBigDecimal("balances"));
            }
        }
    }

    public List<CardView> listCardViews(Connection connection, long userId) throws SQLException {
        String sql = "SELECT c.id, c.card_type, c.color, c.card_number, c.expiry_date, c.is_frozen, "
                + "TRIM(CONCAT_WS(' ', NULLIF(TRIM(u.first_name), ''), NULLIF(TRIM(u.last_name), ''))) AS full_name "
                + "FROM cards c INNER JOIN users u ON u.id = c.user_id WHERE c.user_id = ? ORDER BY c.id DESC";
        List<CardView> cards = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    cards.add(new CardView(result.getLong("id"), result.getString("card_type"),
                            result.getString("color"), maskNumber(result.getString("card_number")),
                            result.getString("expiry_date"), result.getBoolean("is_frozen"),
                            result.getString("full_name").toUpperCase(Locale.ROOT)));
                }
            }
        }
        return cards;
    }

    public BigDecimal lockBalance(Connection connection, long userId) throws SQLException {
        try (PreparedStatement statement = connection
                .prepareStatement("SELECT balance FROM users WHERE id = ? FOR UPDATE")) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBigDecimal("balance") : null;
            }
        }
    }

    public void deductBalance(Connection connection, long userId, BigDecimal fee) throws SQLException {
        try (PreparedStatement statement = connection
                .prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ?")) {
            statement.setBigDecimal(1, fee);
            statement.setLong(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Unable to update the user's balance.");
            }
        }
    }

    public long insertCard(Connection connection, long userId, String cardType, String color,
            String cardNumber, String expiry, String pinHash) throws SQLException {
        String sql = "INSERT INTO cards (user_id, card_type, color, card_number, expiry_date, pin_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setString(2, cardType);
            statement.setString(3, color);
            statement.setString(4, cardNumber);
            statement.setString(5, expiry);
            statement.setString(6, pinHash);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Card insert did not return an ID.");
                }
                return keys.getLong(1);
            }
        }
    }

    public CardView toCardView(long id, String cardType, String color, String cardNumber,
            String expiry, boolean frozen, UserRecord user) {
        return new CardView(id, cardType, color, maskNumber(cardNumber), expiry, frozen,
                user.fullName().toUpperCase(Locale.ROOT));
    }

    public boolean toggleFreeze(Connection connection, long userId, long cardId) throws SQLException {
        String sql = "UPDATE cards SET is_frozen = 1 - is_frozen WHERE id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            statement.setLong(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Card not found for this user.");
            }
        }
        try (PreparedStatement statement = connection
                .prepareStatement("SELECT is_frozen FROM cards WHERE id = ? AND user_id = ?")) {
            statement.setLong(1, cardId);
            statement.setLong(2, userId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Card not found after updating its status.");
                }
                return result.getBoolean("is_frozen");
            }
        }
    }

    private static String maskNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "••••";
        }
        return "•••• " + cardNumber.substring(cardNumber.length() - 4);
    }
}