package app.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

import app.dao.UserDAO;
import app.util.PinUtil;

public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_ATTEMPT_LIMIT = 5;
    private static final long OTP_LIFETIME_SECONDS = 300;
    private static final long RESEND_WAIT_SECONDS = 180;
    private static String email;
    private static String otpHash;
    private static Instant expiresAt;
    private static Instant resendAt;
    private static int attempts;
    private static boolean verified;

    private final UserDAO userDAO = new UserDAO();
    private final BrevoEmailService emailService = new BrevoEmailService();
    private String message = "";

    public String message() {
        return message;
    }

    public boolean sendOtp(String inputEmail) {
        clear();
        if (inputEmail == null || inputEmail.isBlank()) {
            message = "Enter your email address.";
            return false;
        }
        String normalizedEmail = inputEmail.trim().toLowerCase(Locale.ROOT);
        if (!userDAO.existsByEmail(normalizedEmail)) {
            message = "No account is registered with that email.";
            return false;
        }

        int otp = RANDOM.nextInt(900_000) + 100_000;
        String otpText = Integer.toString(otp);
        if (!sendCode(normalizedEmail, otpText)) {
            return false;
        }
        email = normalizedEmail;
        startOtp(otpText);
        message = "Verification code sent to your email.";
        return true;
    }

    public boolean resendOtp() {
        if (email == null) {
            message = "Request a verification code first.";
            return false;
        }
        if (resendSecondsRemaining() > 0) {
            message = "Please wait before requesting another code.";
            return false;
        }

        int otp = RANDOM.nextInt(900_000) + 100_000;
        String otpText = Integer.toString(otp);
        if (!sendCode(email, otpText)) {
            return false;
        }
        startOtp(otpText);
        message = "A new verification code was sent.";
        return true;
    }

    public long resendSecondsRemaining() {
        if (resendAt == null || !Instant.now().isBefore(resendAt)) {
            return 0;
        }
        return java.time.Duration.between(Instant.now(), resendAt).toSeconds() + 1;
    }

    private boolean sendCode(String recipient, String otp) {
        try {
            if (!emailService.sendOtp(recipient, otp)) {
                message = "Could not send the verification email. Check your Brevo settings.";
                return false;
            }
        } catch (RuntimeException e) {
            message = e.getMessage();
            return false;
        }
        return true;
    }

    private void startOtp(String otp) {
        otpHash = PinUtil.hash(otp);
        expiresAt = Instant.now().plusSeconds(OTP_LIFETIME_SECONDS);
        resendAt = Instant.now().plusSeconds(RESEND_WAIT_SECONDS);
        attempts = 0;
        verified = false;
    }

    public boolean verifyOtp(String otp) {
        if (otpHash == null || expiresAt == null || Instant.now().isAfter(expiresAt)) {
            clear();
            message = "The verification code expired. Request a new one.";
            return false;
        }
        if (attempts >= OTP_ATTEMPT_LIMIT) {
            clear();
            message = "Too many incorrect codes. Request a new one.";
            return false;
        }
        if (!PinUtil.matches(otp, otpHash)) {
            attempts++;
            message = "Incorrect verification code.";
            return false;
        }
        verified = true;
        message = "Code verified.";
        return true;
    }

    public boolean changePin(String pin, String confirmPin) {
        if (!verified || email == null) {
            message = "Verify the email code first.";
            return false;
        }
        if (expiresAt == null || Instant.now().isAfter(expiresAt)) {
            clear();
            message = "The verification code expired. Request a new one.";
            return false;
        }
        if (pin == null || !pin.matches("\\d{6}")) {
            message = "PIN must be exactly 6 digits.";
            return false;
        }
        if (!pin.equals(confirmPin)) {
            message = "PINs do not match.";
            return false;
        }
        if (!userDAO.updatePinByEmail(email, PinUtil.hash(pin))) {
            message = "Could not update the PIN. Please try again.";
            return false;
        }
        clear();
        message = "PIN updated. You can now log in.";
        return true;
    }

    private static void clear() {
        email = null;
        otpHash = null;
        expiresAt = null;
        resendAt = null;
        attempts = 0;
        verified = false;
    }
}
