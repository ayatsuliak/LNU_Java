import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String jdbcUrl = "jdbc:postgresql://localhost/yourDB";
    private static final String username = "username";
    private static final String password = "username";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}