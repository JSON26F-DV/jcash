package app.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public final class I18n {
    private static final String BUNDLE = "app.i18n.messages";
    private static final String LANGUAGE_KEY = "language";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(I18n.class);
    private static volatile Locale locale = Locale.forLanguageTag(PREFERENCES.get(LANGUAGE_KEY, "en"));

    static {
        if (locale.getLanguage().isBlank()) {
            locale = Locale.ENGLISH;
        }
    }

    private I18n() {
    }

    public static Locale locale() {
        return locale;
    }

    public static synchronized void setLanguage(String tag) {
        Locale requested = Locale.forLanguageTag(tag == null ? "" : tag.trim());
        locale = requested.getLanguage().isBlank() ? Locale.ENGLISH : requested;
        PREFERENCES.put(LANGUAGE_KEY, locale.toLanguageTag());
        ResourceBundle.clearCache(I18n.class.getClassLoader());
    }

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle(BUNDLE, locale);
    }

    public static String get(String key) {
        try {
            return bundle().getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public static String format(String key, Object... args) {
        return String.format(locale, get(key), args);
    }
}
