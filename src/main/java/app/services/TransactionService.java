package app.services;

import java.math.BigDecimal;
import java.util.List;

import app.dao.TransactionDAO;
import app.dao.UserDAO;
import app.dto.TransactionDTO;
import app.model.Transaction;
import app.model.User;

public class TransactionService {

    public static final String CASH_IN_CATEGORY = "Cash in";
    public static final String SEND_MONEY_CATEGORY = "Send Money";

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserDAO userDAO = new UserDAO();
    private TransactionDTO lastTransaction;

    public TransactionDTO lastTransaction() {
        return lastTransaction;
    }

    public List<TransactionDTO> latestForUser(long userId, int limit) {
        return transactionDAO.latestForUser(userId, limit);
    }

    public List<TransactionDTO> selectAll(long userId) {
        return transactionDAO.selectAll(userId).stream().map(transaction -> {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(transaction.getId());
            dto.setSenderId(transaction.getSenderId());
            dto.setReceiverId(transaction.getReceiverId());
            dto.setDateTime(transaction.getDateTime());
            dto.setCategory(transaction.getCategory());
            dto.setDetails(transaction.getDetails());
            dto.setMessage(transaction.getMessage());
            dto.setAmount(transaction.getAmount());
            return dto;
        }).toList();
    }

    public boolean cashIn(BigDecimal amount, String message) {
        User user = AuthService.currentUser();
        if (user == null || !isPositive(amount)) {
            return false;
        }
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.CASH_IN);
        transaction.setSenderId(null);
        transaction.setReceiverId(user.getId());
        transaction.setAmount(amount);
        transaction.setCategory(CASH_IN_CATEGORY);
        transaction.setDetails("Cash in of user " + user.getId());
        transaction.setMessage(message);

        try {
            if (!transactionDAO.create(transaction, BigDecimal.ZERO, amount)) {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
        refreshBalance(user, user.getBalance().add(amount));
        lastTransaction = toDTO(transaction);
        return true;
    }

    public boolean send(User receiver, BigDecimal amount, String message) {
        User sender = AuthService.currentUser();
        if (sender == null || receiver == null || sender.getId() == receiver.getId()
                || !isPositive(amount)) {
            return false;
        }
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TRANSFER);
        transaction.setSenderId(sender.getId());
        transaction.setReceiverId(receiver.getId());
        transaction.setAmount(amount);
        transaction.setCategory(SEND_MONEY_CATEGORY);
        transaction.setDetails("Transfer from " + sender.getMobileNumber() + " to " + receiver.getMobileNumber());
        transaction.setMessage(message);

        try {
            if (!transactionDAO.create(transaction, amount.negate(), amount)) {
                return false;
            }
        } catch (RuntimeException e) {
            return false;
        }
        refreshBalance(sender, sender.getBalance().subtract(amount));
        refreshBalance(receiver, receiver.getBalance().add(amount));
        lastTransaction = toDTO(transaction);
        return true;
    }

    private TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setSenderId(transaction.getSenderId());
        dto.setReceiverId(transaction.getReceiverId());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setDetails(transaction.getDetails());
        dto.setMessage(transaction.getMessage());
        dto.setDateTime(transaction.getDateTime());
        return dto;
    }

    private boolean isPositive(BigDecimal amount) {
        return amount != null && amount.signum() > 0;
    }

    private void refreshBalance(User user, BigDecimal fallback) {
        try {
            BigDecimal balance = userDAO.getBalanceById(user.getId());
            user.setBalance(balance == null ? fallback : balance);
        } catch (RuntimeException e) {
            user.setBalance(fallback);
        }
    }
}
