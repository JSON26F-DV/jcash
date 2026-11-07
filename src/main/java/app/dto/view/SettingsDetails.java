package app.dto.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import app.model.User;
import app.session.UserSession;

public record SettingsDetails(
        String initials,
        String fullName,
        String email,
        String mobileNumber,
        BigDecimal balance,
        LocalDateTime sessionStartedAt,
        String sessionToken) {

    public static SettingsDetails from(User user) {
        String[] parts = user.getFullName().trim().split("\\s+");
        String initials = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials = parts[0].substring(0, 1);
            if (parts.length > 1) {
                initials += parts[parts.length - 1].substring(0, 1);
            }
        }

        String token = UserSession.token();
        if (token != null) {
            token = token.substring(0, Math.min(9, token.length())) + "…";
        }

        return new SettingsDetails(
                initials.toUpperCase(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getBalance(),
                UserSession.startedAt(),
                token);
    }
}
