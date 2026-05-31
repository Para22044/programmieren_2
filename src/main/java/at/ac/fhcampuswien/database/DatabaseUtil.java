package at.ac.fhcampuswien.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Connection for Database
public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:h2:~/moviesdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    // Java connects with "h2" - Connection gives back the Object
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
    // Creates Tables
    public static void initializeDatabase() {       // Called when the server starts
        String sql = """
            CREATE TABLE IF NOT EXISTS movies (
                id UUID PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                genre VARCHAR(100) NOT NULL,
                release_year INT NOT NULL
            );
            """;

        try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {

            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize database", e);
        }
    }
}
