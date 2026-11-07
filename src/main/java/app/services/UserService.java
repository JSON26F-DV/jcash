package app.services;

import java.math.BigDecimal;

import app.dao.UserDAO;
import app.model.User;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User findByMobileNumber(String mobileNumber) {
        return userDAO.findByMobileNumber(mobileNumber);
    }

    public BigDecimal balance(long userId) {
        return userDAO.getBalanceById(userId);
    }
}
