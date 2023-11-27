package chronos.db;

import chronos.exceptions.ChronosDatabaseException;

import java.io.*;
import java.sql.*;

public class ConnectionManager {
    private Connection conn;
    private static String url = "jdbc:sqlite:time.db";

    public ConnectionManager() {
        try {
            conn = DriverManager.getConnection(this.url);
        } catch(SQLException e) {
            System.out.println(e.getStackTrace());
            throw e;
        }
    }

    /**
     * Initialize an empty database for our app
     */
    public void setupTables() {

        // get resource from schema.sql
        try {  
            BufferedReader reader = new BufferedReader(
                ConnectionManager
                .class
                .getResourceAsStream("/schema.sql")
            );
            sql = reader.lines();

            this.conn.createStatement().execute(sql);  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }  
    }  
}
