package app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class PinUtil {
    protected PinUtil() {
    }

    public static String hash(String pin) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(pin.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean matches(String rawPin, String storedPin) {
        if (rawPin == null || storedPin == null) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(rawPin).getBytes(StandardCharsets.UTF_8),
                storedPin.getBytes(StandardCharsets.UTF_8));
    }
}
