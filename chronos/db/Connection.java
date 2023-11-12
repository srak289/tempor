import java.sql.*;

public class ConnectionManager {
    private Connection conn;
    private static String url = "jdbc:sqlite:time.db";

    public ConnectionManager() {
        try {
            conn = DriverManager.getConnection(self.url);
        } catch(SQLException e) {
            System.out.println(e.getStackTrace());
            throw e;
        }
    }
}
