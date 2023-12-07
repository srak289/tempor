package tempor;

import java.io.*;
import java.sql.*;


public class DatabaseClient {
    private Connection conn;
    private String url;

    private boolean debug;

    private PreparedStatement psCreateTask;
    private PreparedStatement psAllTasks;
    private PreparedStatement psShowTasks;
    private PreparedStatement psStartTask;
    private PreparedStatement psStopTask;
    private PreparedStatement psDeleteTask;

    private PreparedStatement psRunningTask;

    private PreparedStatement psCreateTag;
    private PreparedStatement psShowTag;
    private PreparedStatement psAllTags;
    private PreparedStatement psAssignTag;
    private PreparedStatement psUnassignTag;
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
            System.out.println("<tempor.DatabaseClient: ERROR>: "+line);
        }
    }

    /**
     * Print debug message if this.debug
     */
    private void debug(String line) {
        if (this.debug) {
            System.out.println("<tempor.DatabaseClient: DEBUG>: "+line);
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
        this.psAllTasks = this.conn.prepareStatement(
            "SELECT * FROM task"
        );
        this.psShowTasks = this.conn.prepareStatement(
            "SELECT * FROM task WHERE name LIKE ?"
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
        this.psAllTags = this.conn.prepareStatement(
            "SELECT * FROM tag"
        );
        this.psShowTag = this.conn.prepareStatement(
            "SELECT * FROM tag WHERE name LIKE ?"
        );
        this.psDeleteTag = this.conn.prepareStatement(
            "DELETE FROM tag WHERE name = ?"
        );
        this.psAssignTag = this.conn.prepareStatement(
            "INSERT INTO task_tags (task_id, tag_id) VALUES "
            .concat("(")
            .concat("(SELECT id FROM task WHERE name = ?),")
            .concat("(SELECT id FROM tag WHERE name = ?)")
            .concat(")")
        );
        this.psUnassignTag = this.conn.prepareStatement(
            "DELETE FROM task_tags WHERE "
            .concat("task_id = ")
            .concat("(SELECT id FROM task WHERE name = ?)")
            .concat(" AND ")
            .concat("tag_id =")
            .concat("(SELECT id FROM tag WHERE name = ?)")
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

    public ResultSet showTasks(String name) throws SQLException {
        if (name.equals("")) {
            return this.psAllTasks.executeQuery();
        } else {
            this.psShowTasks.setString(1, "%"+name+"%");
            return this.psShowTasks.executeQuery();
        }
    }

    public int startTask(String name) throws SQLException {
        this.debug("Execute startTask with name = "+name);
        this.psStartTask.setString(1, name);
        return this.psStartTask.executeUpdate();
    }

    /**
     * Stops the current task
     * Returns the name of the stopped task
     */
    public String stopTask() throws SQLException {
        // might be nice to run a query and return name
        // of the task we stopped
        String t = this.psRunningTask.executeQuery().getString(2);
        int r = this.psStopTask.executeUpdate();
        if (r == 1) {
            return t;
        } else {
            return "";
        }
    }

    public int deleteTask(String name) throws SQLException {
        this.psDeleteTask.setString(1, name);
        return this.psDeleteTask.executeUpdate();
    }

    public int createTag(String name) throws SQLException {
        this.psCreateTag.setString(1, name);
        return this.psCreateTag.executeUpdate();
    }

    public ResultSet showTags(String name) throws SQLException {
        if (name.equals("")) {
            return this.psAllTags.executeQuery();
        } else {
            this.psShowTag.setString(1, "%"+name+"%");
            return this.psShowTag.executeQuery();
        }
    }

    public int deleteTag(String name) throws SQLException {
        this.psDeleteTag.setString(1, name);
        return this.psDeleteTag.executeUpdate();
    }

    public int assignTag(String task, String tag) throws SQLException {
        this.psAssignTag.setString(1, task);
        this.psAssignTag.setString(2, tag);
        return this.psAssignTag.executeUpdate();
    }

    public int unassignTag(String task, String tag) throws SQLException {
        this.psUnassignTag.setString(1, task);
        this.psUnassignTag.setString(2, tag);
        return this.psUnassignTag.executeUpdate();
    }
}
