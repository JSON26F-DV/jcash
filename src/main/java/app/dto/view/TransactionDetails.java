package app.dto.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import app.dto.TransactionDTO;

public record TransactionDetails(long id, String title, BigDecimal amount, String message, LocalDateTime dateTime) {

    public static TransactionDetails from(TransactionDTO transaction) {
        return new TransactionDetails(
                transaction.getId(),
                transaction.getDetails(),
                transaction.getAmount(),
                transaction.getMessage(),
                transaction.getDateTime());
    }
}
