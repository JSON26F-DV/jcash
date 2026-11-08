package app.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import app.config.DatabaseConnection;
import app.dto.TransactionDTO;
import app.model.Transaction;

public class TransactionDAO implements DAO<Transaction> {

    private static final String INSERT = "INSERT INTO transactions (sender_id, receiver_id, amount, description, category, message) "
            + "VALUES (:senderId, :receiverId, :amount, :description, :category, :message)";

    private static final String MOVE_BALANCE = "UPDATE users SET balance = balance + :delta "
            + "WHERE id = :id AND balance + :delta >= 0";

    private static final String LATEST_FOR_USER = "SELECT id, sender_id, receiver_id, created_at, category, amount, description, message FROM transactions "
            + "WHERE sender_id = :id OR receiver_id = :id ORDER BY created_at DESC LIMIT :limit";

    private static final String ALL_FOR_USER = "SELECT id, sender_id, receiver_id, created_at, category, amount, description, message FROM transactions "
            + "WHERE sender_id = :id OR receiver_id = :id ORDER BY created_at DESC";

    @Override
    public boolean create(Transaction entity) {
        return create(entity, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public boolean create(Transaction transaction, BigDecimal senderDelta, BigDecimal receiverDelta) {
        if (transaction == null || transaction.getAmount() == null
                || transaction.getAmount().signum() <= 0) {
            return false;
        }
        return DatabaseConnection.get().inTransaction(handle -> {
            long transactionId = handle.createUpdate(INSERT)
                    .bind("senderId", transaction.getSenderId())
                    .bind("receiverId", transaction.getReceiverId())
                    .bind("amount", transaction.getAmount())
                    .bind("description", transaction.getDetails())
                    .bind("category", transaction.getCategory())
                    .bind("message", transaction.getMessage() == null ? "" : transaction.getMessage())
                    .executeAndReturnGeneratedKeys()
                    .mapTo(Long.class)
                    .one();
            if (transactionId <= 0) {
                throw new IllegalStateException("Transaction record was not inserted.");
            }
            transaction.setId(transactionId);
            transaction.setDateTime(LocalDateTime.now());

            Long senderId = transaction.getSenderId();
            Long receiverId = transaction.getReceiverId();
            if (senderId != null && receiverId != null && senderId > receiverId) {
                move(handle, receiverId, receiverDelta);
                move(handle, senderId, senderDelta);
            } else {
                move(handle, senderId, senderDelta);
                move(handle, receiverId, receiverDelta);
            }
            return true;
        });
    }

    public long insertCardFee(Connection connection, long userId, BigDecimal amount, String cardType)
            throws SQLException {
        String sql = "INSERT INTO transactions "
                + "(sender_id, receiver_id, amount, description, category, message) "
                + "VALUES (?, NULL, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setBigDecimal(2, amount);
            statement.setString(3, "Card creation fee for " + cardType + " card");
            statement.setString(4, "Card fee");
            statement.setString(5, "New card creation fee");
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Card fee transaction was not recorded.");
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Card fee transaction did not return an ID.");
                }
                return keys.getLong(1);
            }
        }
    }

    private void move(org.jdbi.v3.core.Handle handle, Long userId, BigDecimal delta) {
        if (userId == null || delta == null || delta.signum() == 0) {
            return;
        }
        int rows = handle.createUpdate(MOVE_BALANCE)
                .bind("delta", delta)
                .bind("id", userId)
                .execute();
        if (rows != 1) {
            throw new IllegalStateException("Balance update failed; transaction rolled back.");
        }
    }

    @Override
    public boolean update(Transaction entity) {

        return false;
    }

    @Override
    public boolean delete(long id) {

        return false;
    }

    @Override
    public List<Transaction> selectAll() {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(
                "SELECT id, sender_id, receiver_id, created_at, category, amount, description, message "
                        + "FROM transactions ORDER BY created_at DESC")
                .map((rs, ctx) -> mapTransaction(rs))
                .list());
    }

    @Override
    public Transaction selectOne(long id) {

        return null;
    }

    public List<TransactionDTO> latestForUser(long userId, int limit) {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(LATEST_FOR_USER)
                .bind("id", userId)
                .bind("limit", limit)
                .map((rs, ctx) -> {
                    TransactionDTO dto = new TransactionDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setSenderId(rs.getLong("sender_id"));
                    dto.setReceiverId(rs.getLong("receiver_id"));
                    dto.setDateTime(parseDateTime(rs.getString("created_at")));
                    dto.setCategory(rs.getString("category"));
                    dto.setDetails(rs.getString("description"));
                    dto.setMessage(rs.getString("message"));
                    dto.setAmount(rs.getBigDecimal("amount"));
                    return dto;
                })
                .list());
    }

    @Override
    public List<Transaction> selectAll(long userId) {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(ALL_FOR_USER)
                .bind("id", userId)
                .map((rs, ctx) -> mapTransaction(rs))
                .list());
    }

    private Transaction mapTransaction(java.sql.ResultSet rs) throws java.sql.SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setSenderId((Long) rs.getObject("sender_id"));
        transaction.setReceiverId((Long) rs.getObject("receiver_id"));
        transaction.setDateTime(parseDateTime(rs.getString("created_at")));
        transaction.setType(rs.getString("category"));
        transaction.setDetails(rs.getString("description"));
        transaction.setMessage(rs.getString("message"));
        transaction.setCategory(rs.getString("category"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        return transaction;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.startsWith("0000")) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.replace(' ', 'T'));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
