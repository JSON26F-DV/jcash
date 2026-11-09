package app.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import app.config.DatabaseConnection;
import app.model.User;

public class UserDAO implements DAO<User> {

    private static final String INSERT = "INSERT INTO users (mobile_number, pin, first_name, last_name, email, balance, created_at, is_deleted) "
            + "VALUES (:mobileNumber, :pin, :firstName, :lastName, :email, :balance, NOW(), 0)";
    private static final String SELECT_ALL = "SELECT * FROM users";
    private static final String SELECT_ONE = "SELECT * FROM users WHERE id = :id";
    private static final String SELECT_BY_MOBILE = "SELECT * FROM users WHERE mobile_number = :mobile LIMIT 1";
    private static final String SELECT_BY_EMAIL = "SELECT 1 FROM users WHERE email = :email LIMIT 1";
    private static final String UPDATE_PIN_BY_EMAIL = "UPDATE users SET pin = :pin WHERE email = :email";

    @Override
    public boolean create(User user) {
        if (user == null || existsByEmail(user.getEmail())) {
            return false;
        }
        return DatabaseConnection.get().withHandle(handle -> handle.createUpdate(INSERT)
                .bind("mobileNumber", user.getMobileNumber())
                .bind("pin", user.getPin())
                .bind("firstName", user.getFirstName())
                .bind("lastName", user.getLastName())
                .bind("email", user.getEmail())
                .bind("balance", user.getBalance())
                .execute()) > 0;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public List<User> selectAll() {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(SELECT_ALL)
                .map((rs, ctx) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setMobileNumber(rs.getString("mobile_number"));
                    user.setPin(rs.getString("pin"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setBalance(rs.getBigDecimal("balance"));
                    user.setCreatedAt(parseDateTime(rs.getString("created_at")));
                    return user;
                })
                .list());
    }

    @Override
    public List<User> selectAll(long userId) {
        User user = selectOne(userId);
        return user == null ? List.of() : List.of(user);
    }

    @Override
    public User selectOne(long id) {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(SELECT_ONE)
                .bind("id", id)
                .map((rs, ctx) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setMobileNumber(rs.getString("mobile_number"));
                    user.setPin(rs.getString("pin"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setBalance(rs.getBigDecimal("balance"));
                    user.setCreatedAt(parseDateTime(rs.getString("created_at")));
                    return user;
                })
                .findOne()
                .orElse(null));
    }

    public User findByMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return null;
        }
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(SELECT_BY_MOBILE)
                .bind("mobile", mobileNumber.trim())
                .map((rs, ctx) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setMobileNumber(rs.getString("mobile_number"));
                    user.setPin(rs.getString("pin"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setBalance(rs.getBigDecimal("balance"));
                    user.setCreatedAt(parseDateTime(rs.getString("created_at")));
                    return user;
                })
                .findOne()
                .orElse(null));
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(SELECT_BY_EMAIL)
                .bind("email", email.trim().toLowerCase(Locale.ROOT))
                .mapTo(Integer.class)
                .findOne()
                .isPresent());
    }

    public boolean updatePinByEmail(String email, String pin) {
        return DatabaseConnection.get().withHandle(handle -> handle.createUpdate(UPDATE_PIN_BY_EMAIL)
                .bind("email", email)
                .bind("pin", pin)
                .execute()) == 1;
    }

    public boolean isDeleted(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return false;
        }
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(
                "SELECT is_deleted FROM users WHERE mobile_number = :mobile LIMIT 1")
                .bind("mobile", mobileNumber.trim())
                .mapTo(Integer.class)
                .findOne()
                .orElse(0)) == 1;
    }

    public boolean markDeleted(long id) {
        return DatabaseConnection.get().withHandle(handle -> handle.createUpdate(
                "UPDATE users SET is_deleted = 1 WHERE id = :id")
                .bind("id", id)
                .execute()) == 1;
    }

    public BigDecimal getBalanceById(long id) {
        return DatabaseConnection.get().withHandle(handle -> handle.createQuery(
                "SELECT balance FROM users WHERE id = :id")
                .bind("id", id)
                .mapTo(BigDecimal.class)
                .findOne()
                .orElse(null));
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
