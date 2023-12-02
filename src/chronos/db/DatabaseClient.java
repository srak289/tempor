package chronos.db;

import chronos.exceptions.ChronosDatabaseException;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class DatabaseClient {
    private Connection conn;
    private String url;

    // default constuctor
    public DatabaseClient() {
        this.url = "jdbc:sqlite:time.db";
    }

    public DatabaseClient(String url) {
        this.url = "jdbc:sqlite:"+url;
    }

    /**
     * Connect to the database
     * Use the default file location if none given
     */
    public void connect() throws ChronosDatabaseException {
        try {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new ChronosDatabaseException(e);
            }
            this.conn = DriverManager.getConnection(this.url);
        } catch(SQLException e) {
            throw new ChronosDatabaseException(e);
        }
    }

    /**
     * Initialize the database for our app
     */
    public void setupTables() throws ChronosDatabaseException {

        try {  
            Statement stmt = this.conn.createStatement();

            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        this.getClass().getResourceAsStream("schema.sql")
                    )
                );
                String line, sql = "";
                // read table statements from schema.sql and batch them
                while ((line = reader.readLine()) != null) {
                    if (
                        line.startsWith("--") ||
                        line.isEmpty()
                    ) {
                        // System.out.println("Skipping empty or commented line");
                        continue;
                    } else if (line.endsWith(";")) {
                        sql += line+"\n";
                        // System.out.println("Adding batch statement\n"+sql);
                        stmt.addBatch(sql);
                        sql = "";
                    } else {
                        // System.out.println("Concat line\n"+line);
                        sql += line+"\n";
                    }
                }
            } catch (IOException e) {
                throw new ChronosDatabaseException(e);
            }

            // System.out.println("Execute batch");
            stmt.executeBatch();
        } catch (SQLException e) {  
            throw new ChronosDatabaseException(e);
        }  
    }  

    public void create() throws ChronosDatabaseException {
        try {
            Statement stmt = this.conn.createStatement();
        } catch (SQLException e) {
            throw new ChronosDatabaseException(e);
        }
    }

    public void read() throws ChronosDatabaseException {
        try {
            Statement stmt = this.conn.createStatement();
        } catch (SQLException e) {
            throw new ChronosDatabaseException(e);
        }
    }

    public void update() throws ChronosDatabaseException {
        try {
            Statement stmt = this.conn.createStatement();
        } catch (SQLException e) {
            throw new ChronosDatabaseException(e);
        }
    }

    public void delete() throws ChronosDatabaseException {
        try {
            Statement stmt = this.conn.createStatement();
        } catch (SQLException e) {
            throw new ChronosDatabaseException(e);
        }
    }
}
