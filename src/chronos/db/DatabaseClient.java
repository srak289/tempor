package chronos.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public class DatabaseClient {
    private Connection conn;
    private String url;

    public DatabaseClient(String url) {
        if (url != null) {
            this.url = "jdbc:sqlite:"+url;
        } else {
            this.url = "jdbc:sqlite:time.db";
        }
    }

    /**
     * Connect to the database
     * Use the default file location if none given
     */
    public void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("You may need the sqlite JDBC!");
            System.exit(255);
        }
        this.conn = DriverManager.getConnection(this.url);
    }

    /**
     * Initialize the database for our app
     */
    public void setupTables() throws
        IOException,
        SQLException
    {

        Statement stmt = this.conn.createStatement();

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
            // We split our batches on lines that contain a single ';'
            // in order to avoid errors defining triggers
            } else if (line.equals(";\n")) {
                sql += line+"\n";
                // System.out.println("Adding batch statement\n"+sql);
                stmt.addBatch(sql);
                sql = "";
            } else {
                // System.out.println("Concat line\n"+line);
                sql += line+"\n";
            }
        }

        // System.out.println("Execute batch");
        stmt.executeBatch();
    }  

    // TODO we need an interface to query and receive things
    // probably letting the console class make SQL calls is good enough
    // but we need to figure out how the objects should be returned
    //
    // We may also want prepared statements for things like pulling reports
    // of tasks and such

    public ResultSet exec(String query) throws SQLException {
        Statement stmt = this.conn.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE
        );
        return stmt.executeQuery("select * from task");
    }

    /**
     * Returns null when query is UPDATE or INSERT
     */
    public ResultSet execQuery(String query) throws SQLException {
        Statement stmt = this.conn.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE
        );
        return stmt.executeQuery("select * from task");
    }
}
