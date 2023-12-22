// Copyright © 2023 Spencer Rak <spencer.rak@snhu.edu>
// SPDX-License-Header: MIT
package tempor;

import java.io.*;
import java.sql.*;
import java.util.Date;


public class DatabaseClient {
    private Connection conn;
    private String url;

    private boolean debug;
    private boolean demo;

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

    private PreparedStatement psReportTag;
    private PreparedStatement psReportTask;

    /**
     * Construct a DatabaseClient object
     * <br>
     * @param   url     the database url
     * @param   debug   print debug information
     * @return          the initialized object
     */
    public DatabaseClient(String url, boolean debug, boolean demo) {
        this.debug = debug;
        this.demo = demo;
        if (url != null) {
            this.url = "jdbc:sqlite:"+url;
        } else {
            this.url = "jdbc:sqlite:time.db";
        }
    }

    /**
     * Print error message
     * <br>
     * @param   line    the line to print
     */
    private void error(String line) {
        if (this.debug) {
            System.out.println("<tempor.DatabaseClient: ERROR>: "+line);
        }
    }

    /**
     * Print debug message if this.debug
     * <br>
     * @param   line    the line to print
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
     * Load demo data
     */
    public void loadDemo() throws
        IOException,
        SQLException
    {

        Statement stmt = this.conn.createStatement();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                this.getClass().getResourceAsStream("demo.sql")
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

        this.debug("Execute demo batch");
        stmt.executeBatch();
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

        if (this.demo) {
            this.loadDemo();
        }
    }  

    // use prepared statements to avoid SQL issues
    private void prepareStatements() throws SQLException {
        this.psReportTag = this.conn.prepareStatement(
            "SELECT * FROM vw_time_per_tag"
        );
        this.psReportTask = this.conn.prepareStatement(
            "SELECT * FROM vw_time_per_task"
        );
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

    /**
     * Create a task
     * <br>
     * @param   name        the name of the task
     * @param   dueBy       the due date
     * @param   allowedTime the the amount of time allowed for the task in seconds
     * @return              the number of rows affected
     */
    public int createTask(String name, java.util.Date dueBy, int allowedTime) throws SQLException {
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

    /**
     * Search tasks with a wildcard
     * <br>
     * @param   name    will be wrapped as %name%
     * @return          the result set of the search
     */
    public ResultSet showTasks(String name) throws SQLException {
        if (name.equals("")) {
            return this.psAllTasks.executeQuery();
        } else {
            this.psShowTasks.setString(1, "%"+name+"%");
            return this.psShowTasks.executeQuery();
        }
    }

    /**
     * Start a task
     * <br>
     * @param   name    the task to start
     * @return          number of rows affected
     */
    public int startTask(String name) throws SQLException {
        this.debug("Execute startTask with name = "+name);
        this.psStartTask.setString(1, name);
        return this.psStartTask.executeUpdate();
    }

    /**
     * Stops the current task
     * <br>
     * @return  the name of the stopped task
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

    /**
     * Deletes a task
     * <br>
     * @param name the task to delete
     * @return  the count of rows affected
     */
    public int deleteTask(String name) throws SQLException {
        this.psDeleteTask.setString(1, name);
        return this.psDeleteTask.executeUpdate();
    }

    /**
     * Creates a tag
     * <br>
     * @param   name    the name of the tag
     * @return          the number of rows affected
     */
    public int createTag(String name) throws SQLException {
        this.psCreateTag.setString(1, name);
        return this.psCreateTag.executeUpdate();
    }

    /**
     * Search tags with a wildcard
     * <br>
     * @param   name    will be wrapped as %name%
     * @return          the result set of the search
     */
    public ResultSet showTags(String name) throws SQLException {
        if (name.equals("")) {
            return this.psAllTags.executeQuery();
        } else {
            this.psShowTag.setString(1, "%"+name+"%");
            return this.psShowTag.executeQuery();
        }
    }

    /**
     * Delete a tag
     * <br>
     * @param   name    the tag to delete
     * @return          the number of rows affected
     */
    public int deleteTag(String name) throws SQLException {
        this.psDeleteTag.setString(1, name);
        return this.psDeleteTag.executeUpdate();
    }

    /**
     * Assign a tag to a task
     * <br>
     * @param   task    the task to reference
     * @param   tag     the tag to assign to the task
     * @return          the number of rows affected
     */
    public int assignTag(String task, String tag) throws SQLException {
        this.psAssignTag.setString(1, task);
        this.psAssignTag.setString(2, tag);
        return this.psAssignTag.executeUpdate();
    }

    /**
     * Remove a tag from a task
     * <br>
     * @param   task    the task to reference
     * @param   tag     the tag to remove from the task
     * @return          the number of rows affected
     */
    public int unassignTag(String task, String tag) throws SQLException {
        this.psUnassignTag.setString(1, task);
        this.psUnassignTag.setString(2, tag);
        return this.psUnassignTag.executeUpdate();
    }

    /**
     * Generate a time report by tag
     * <br>
     * @return  The result set of the query
     */
    public ResultSet reportTag() throws SQLException {
        return this.psReportTag.executeQuery();
    }

    /**
     * Generate a time report by task
     * <br>
     * @return  The result set of the query
     */
    public ResultSet reportTask() throws SQLException {
        return this.psReportTask.executeQuery();
    }
}
