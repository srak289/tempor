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

    /**
     * Initialize an empty database for our app
     */
    public void setupTables(self) {

        String sql = "CREATE TABLE IF NOT EXISTS employees (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text NOT NULL,\n"  
                + " capacity real\n"  
                + ");";  
          
        try {  
            self.conn.createStatement().execute(sql);  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }  
    }  
}
