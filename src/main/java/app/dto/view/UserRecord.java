package app.dto.view;

import java.math.BigDecimal;

public record UserRecord(long id, String fullName, String username, BigDecimal balances) {
}