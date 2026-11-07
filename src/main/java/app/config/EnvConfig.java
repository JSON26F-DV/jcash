package app.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class EnvConfig {

    private static final Properties DOT_ENV = load();

    private EnvConfig() {
    }

    public static String get(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return DOT_ENV.getProperty(key, "").trim();
    }

    private static Properties load() {
        Properties properties = new Properties();
        Path file = Path.of(".env");
        if (Files.exists(file)) {
            try (var reader = Files.newBufferedReader(file)) {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read .env file.", e);
            }
        }
        return properties;
    }
}
