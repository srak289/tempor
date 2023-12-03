package chronos;

import java.io.*;
import java.sql.*;


public class DatabaseClient {
    private Connection conn;
    private String url;

    private boolean debug;

    private PreparedStatement psCreateTask;
    private PreparedStatement psSearchTask;
    private PreparedStatement psStartTask;
    private PreparedStatement psStopTask;
    private PreparedStatement psDeleteTask;

    private PreparedStatement psRunningTask;

    private PreparedStatement psCreateTag;
    private PreparedStatement psSearchTag;
    private PreparedStatement psDeleteTag;


    public DatabaseClient(String url, boolean debug) {
        this.debug = debug;
        if (url != null) {
            this.url = "jdbc:sqlite:"+url;
        } else {
            this.url = "jdbc:sqlite:time.db";
        }
    }

    /**
     * Print error message
     */
    private void error(String line) {
        if (this.debug) {
            System.out.println("<chronos.DatabaseClient: ERROR>: "+line);
        }
    }

    /**
     * Print debug message if this.debug
     */
    private void debug(String line) {
        if (this.debug) {
            System.out.println("<chronos.DatabaseClient: DEBUG>: "+line);
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
            this.error("You may need the sqlite JDBC!");
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
                this.debug("Skipping empty or commented line");
                continue;
            // We split our batches on lines that contain a single ';'
            // in order to avoid errors defining triggers
            } else if (
                line.equals(";") ||
                line.equals(");")
            ) {
                sql += line+"\n";
                this.debug("Adding batch statement\n"+sql);
                stmt.addBatch(sql);
                sql = "";
            } else {
                this.debug("Concat line\n"+line);
                sql += line+"\n";
            }
        }

        this.debug("Execute batch");
        stmt.executeBatch();

        // prepare our statements after the database is initialized
        this.prepareStatements();
    }  

    // use prepared statements to avoid SQL issues
    private void prepareStatements() throws SQLException {
        this.psCreateTask = this.conn.prepareStatement(
            "INSERT INTO task (name, due_by, allowed_time) VALUES (?, ?, ?)"
        );
        this.psSearchTask = this.conn.prepareStatement(
            "SELECT * FROM task WHERE name = ?"
        );
        this.psStartTask = this.conn.prepareStatement(
            "UPDATE task SET in_progress = True WHERE name = ?"
        );
        this.psStopTask = this.conn.prepareStatement(
            "UPDATE task SET in_progress = NULL WHERE in_progress = True"
        );
        this.psRunningTask = this.conn.prepareStatement(
            "SELECT * FROM task WHERE in_progress = True"
        );
        this.psDeleteTask = this.conn.prepareStatement(
            "DELETE FROM task WHERE name = ?"
        );
        this.psCreateTag = this.conn.prepareStatement(
            "INSERT INTO tag (name) VALUES (?)"
        );
        this.psSearchTag = this.conn.prepareStatement(
            "SELECT * FROM tag WHERE name = ?"
        );
        this.psDeleteTag = this.conn.prepareStatement(
            "DELETE FROM tag WHERE name = ?"
        );
    }

    public int createTask(String name, Date dueBy, int allowedTime) throws SQLException {
        this.psCreateTask.setString(1, name);
        if (dueBy == null) {
            // 9999-01-01 0:0:0
            this.psCreateTask.setTimestamp(2, new Timestamp(253370782800000L));
        } else {
            this.psCreateTask.setTimestamp(2, new Timestamp(dueBy.getTime()));
        }
        if (allowedTime == 0) {
            this.psCreateTask.setInt(3, 0);
        } else {
            this.psCreateTask.setInt(3, allowedTime);
        }
        return this.psCreateTask.executeUpdate();
    }

    public ResultSet searchTask(String name) throws SQLException {
        this.psSearchTask.setString(1, name);
        return this.psSearchTask.executeQuery();
    }

    public int startTask(String name) throws SQLException {
        this.debug("Execute startTask with name = "+name);
        this.psStartTask.setString(1, name);
        return this.psStartTask.executeUpdate();
    }

    public int stopTask() throws SQLException {
        // might be nice to run a query and return name
        // of the task we stopped
        return this.psStopTask.executeUpdate();
    }

    public int deleteTask(String name) throws SQLException {
        this.psDeleteTask.setString(1, name);
        return this.psDeleteTask.executeUpdate();
    }

    public int createTag(String name) throws SQLException {
        this.psCreateTag.setString(1, name);
        return this.psCreateTag.executeUpdate();
    }

    public ResultSet searchTags(String name) throws SQLException {
        this.psSearchTag.setString(1, name);
        return this.psSearchTag.executeQuery();
    }

    public int deleteTags(String name) throws SQLException {
        this.psDeleteTag.setString(1, name);
        return this.psDeleteTag.executeUpdate();
    }
}
