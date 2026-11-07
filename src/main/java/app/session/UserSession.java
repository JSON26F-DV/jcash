package app.session;

import java.time.LocalDateTime;
import java.util.UUID;

import app.model.User;

public class UserSession {

    private static User user;
    private static LocalDateTime startedAt;
    private static String token;

    private UserSession() {
    }

    public static void start(User signedIn) {
        user = signedIn;
        startedAt = LocalDateTime.now();
        token = "tok_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static void end() {
        user = null;
        startedAt = null;
        token = null;
    }

    public static User user() {
        return user;
    }

    public static LocalDateTime startedAt() {
        return startedAt;
    }

    public static String token() {
        return token;
    }
}
