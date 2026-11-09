package app.services;

import java.util.Locale;

import app.dao.UserDAO;
import app.i18n.I18n;
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
            message = I18n.get("auth.lockout");
            return false;
        }

        User user = userDAO.findByMobileNumber(mobile);

        if (user == null) {
            message = I18n.get("auth.invalidMobile");
            return false;
        }

        if (userDAO.isDeleted(mobile)) {
            message = I18n.get("auth.deleted");
            return false;
        }

        if (!PinUtil.matches(pin, user.getPin())) {
            attempt++;
            if (attempt >= 3) {
                message = I18n.get("auth.lockout");
            } else {
                message = I18n.format("auth.invalidPinAttempts", 3 - attempt);
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
            message = I18n.get("register.error.generic");
            return false;
        }

        String mobile = user.getMobileNumber() == null ? "" : user.getMobileNumber().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase(Locale.ROOT);

        user.setMobileNumber(mobile);
        user.setEmail(email);

        if (mobile.isBlank() || email.isBlank() || user.getPin() == null || user.getPin().isBlank()) {
            message = I18n.get("register.error.generic");
            return false;
        }

        if (userDAO.findByMobileNumber(mobile) != null) {
            message = I18n.get("register.error.mobileExists");
            return false;
        }

        if (userDAO.existsByEmail(email)) {
            message = I18n.get("register.error.emailExists");
            return false;
        }

        user.setPin(PinUtil.hash(user.getPin()));
        try {
            if (!userDAO.create(user)) {
                message = I18n.get("register.error.generic");
                return false;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            message = I18n.get("register.error.generic");
            return false;
        }
        message = "";
        return true;
    }
}
