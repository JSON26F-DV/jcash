package app.services;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jdbi.v3.core.Handle;
import org.mindrot.jbcrypt.BCrypt;

import app.config.DatabaseConnection;
import app.dao.CardDao;
import app.dao.TransactionDAO;
import app.dto.view.CardView;
import app.dto.view.UserRecord;

public class CardService {
    public static final BigDecimal CARD_FEE = new BigDecimal("250.00");
    private static final int CARD_NUMBER_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CardDao cards;
    private final TransactionDAO transactions;

    public CardService() {
        this(new CardDao(), new TransactionDAO());
    }

    CardService(CardDao cards, TransactionDAO transactions) {
        this.cards = cards;
        this.transactions = transactions;
    }

    public record CardCreationResult(CardView cardView, UserRecord userRecord) {
    }

    public UserRecord findUserRecord(long userId) {
        try (Handle handle = DatabaseConnection.get().open()) {
            return cards.findUserRecord(handle.getConnection(), userId);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load the user record.", e);
        }
    }

    public List<CardView> listByUser(long userId) {
        try (Handle handle = DatabaseConnection.get().open()) {
            return cards.listCardViews(handle.getConnection(), userId);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load cards.", e);
        }
    }

    public CardCreationResult createCard(long userId, String cardType, String color,
            String expiry, String pin) {
        validateCardInput(cardType, color, expiry, pin);
        for (int attempt = 0; attempt < CARD_NUMBER_ATTEMPTS; attempt++) {
            try {
                return createCardTransaction(userId, cardType, color, expiry, pin);
            } catch (SQLException e) {
                if (!isDuplicateKey(e) || attempt == CARD_NUMBER_ATTEMPTS - 1) {
                    throw new IllegalStateException("Unable to create the card.", e);
                }
            }
        }
        throw new IllegalStateException("Unable to generate a unique card number.");
    }

    public boolean toggleFreeze(long userId, long cardId) {
        try (Handle handle = DatabaseConnection.get().open()) {
            Connection connection = handle.getConnection();
            return cards.toggleFreeze(connection, userId, cardId);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to update the card status.", e);
        }
    }

    private CardCreationResult createCardTransaction(long userId, String cardType, String color,
            String expiry, String pin) throws SQLException {
        try (Handle handle = DatabaseConnection.get().open()) {
            Connection connection = handle.getConnection();
            boolean previousAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                BigDecimal balance = cards.lockBalance(connection, userId);
                if (balance == null) {
                    throw new SQLException("User not found.");
                }
                if (balance.compareTo(CARD_FEE) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance for a new card.");
                }

                cards.deductBalance(connection, userId, CARD_FEE);
                String cardNumber = generateCardNumber();
                String pinHash = BCrypt.hashpw(pin, BCrypt.gensalt());
                long cardId = cards.insertCard(connection, userId, cardType, color, cardNumber, expiry, pinHash);
                transactions.insertCardFee(connection, userId, CARD_FEE, cardType);
                UserRecord updatedUser = cards.findUserRecord(connection, userId);
                CardView cardView = cards.toCardView(cardId, cardType, color, cardNumber, expiry, false,
                        updatedUser);
                connection.commit();
                return new CardCreationResult(cardView, updatedUser);
            } catch (Exception e) {
                rollback(connection, e);
                if (e instanceof SQLException sqlException) {
                    throw sqlException;
                }
                if (e instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new SQLException("Card creation failed.", e);
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            original.addSuppressed(rollbackError);
        }
    }

    private static boolean isDuplicateKey(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if ("23000".equals(current.getSQLState()) && current.getErrorCode() == 1062) {
                return true;
            }
        }
        return false;
    }

    private static String generateCardNumber() {
        StringBuilder digits = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            digits.append(RANDOM.nextInt(10));
        }
        return digits.substring(0, 4) + " " + digits.substring(4, 8) + " "
                + digits.substring(8, 12) + " " + digits.substring(12, 16);
    }

    private static void validateCardInput(String cardType, String color, String expiry, String pin) {
        if (!List.of("debit", "credit", "prepaid").contains(cardType)) {
            throw new IllegalArgumentException("Invalid card type.");
        }
        if (!List.of("blue", "graphite", "onyx", "emerald").contains(color)) {
            throw new IllegalArgumentException("Invalid card color.");
        }
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("Expiry must use MM/YY format.");
        }
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must contain exactly four digits.");
        }
    }
}