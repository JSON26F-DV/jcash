package app.services;

import java.util.Locale;

import app.dao.UserDAO;
import app.model.User;
import app.session.UserSession;
import app.util.PinUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static int attempt;
    private String message = "";

    public static User currentUser() {
        return UserSession.user();
    }

    public static void logout() {
        UserSession.end();
    }

    public String message() {
        return message;
    }

    public boolean login(String mobile, String pin) {
        if (attempt >= 3) {
            message = "Too many failed attempts. Exiting.";
            return false;
        }

        User user = userDAO.findByMobileNumber(mobile);

        if (user == null) {
            message = "Invalid mobile number.";
            return false;
        }

        if (userDAO.isDeleted(mobile)) {
            message = "Account deleted.";
            return false;
        }

        if (!PinUtil.matches(pin, user.getPin())) {
            attempt++;
            if (attempt >= 3) {
                message = "Too many failed attempts. Exiting.";
            } else {
                message = "Invalid PIN. " + (3 - attempt) + " attempt(s) left.";
            }
            return false;
        }

        attempt = 0;
        message = "";
        UserSession.start(user);
        return true;
    }

    public boolean deleteAccount(long id) {
        return userDAO.markDeleted(id);
    }

    public boolean register(User user) {
        if (user == null) {
            message = "Registration failed.";
            return false;
        }

        String mobile = user.getMobileNumber().trim();
        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);

        user.setMobileNumber(mobile);
        user.setEmail(email);

        if (userDAO.findByMobileNumber(mobile) != null) {
            message = "Mobile number is already registered.";
            return false;
        }

        if (userDAO.existsByEmail(email)) {
            message = "Email is already registered.";
            return false;
        }

        user.setPin(PinUtil.hash(user.getPin()));
        try {
            if (!userDAO.create(user)) {
                message = "Registration failed.";
                return false;
            }
        } catch (RuntimeException e) {
            message = "Registration failed.";
            return false;
        }
        message = "";
        return true;
    }
}
