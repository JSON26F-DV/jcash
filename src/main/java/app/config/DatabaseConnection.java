package app.config;

import org.jdbi.v3.core.Jdbi;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/jcash_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private static final Jdbi JDBI = Jdbi.create(URL, USER, PASS);

    public static Jdbi get() {
        return JDBI;
    }
}
