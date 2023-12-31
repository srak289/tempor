// Copyright © 2023 Spencer Rak <spencer.rak@snhu.edu>
// SPDX-License-Header: MIT
package tempor;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.reflect.*;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.jboss.jreadline.console.*;
import org.jboss.jreadline.console.settings.Settings;
import org.jboss.jreadline.complete.*;
import org.jboss.jreadline.edit.actions.Operation;

import tempor.DatabaseClient;


public class Console implements Completion {

    private org.jboss.jreadline.console.Console console;
    private DatabaseClient db;
    private HashMap<String, String> commands;
    private static final Pattern p = Pattern.compile("\"([^\"]+)\"");
    private static final Pattern datePattern = Pattern.compile("\\d{8}");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private boolean debug;

    public Console(DatabaseClient db, boolean debug) throws IOException {

        this.debug = debug;
        this.commands = new HashMap<String, String>();

        initCommands();

        this.db = db;
        this.console = new org.jboss.jreadline.console.Console();
        this.console.addCompletion(this);
    }

    @Override
    public void complete(CompleteOperation co) {
        List<String> completions = new ArrayList<String>();

        // get the buffer and trim whitespace for sanity
        String buf = co.getBuffer().trim();

        for (String key : this.commands.keySet()) {
            // don't check the keys that are shorter than our current buffer
            if (buf.length() <= key.length()) {
                if (buf.equalsIgnoreCase(key.substring(0, buf.length()))) {
                    completions.add(key);
                }
            }
        }
        co.setCompletionCandidates(completions);
    }

    /**
     * Initialize our command hashmap
     */
    private void initCommands() {
        // create <tag|task> <name> [allowed_hours] [due_date]
        // show [tags,tasks]
        // tag <name> <tag_name>
        // untag <name> <tag_name>
        // start <task_name> (can start while another task is started, it will stop the current)
        // stop (no argument, just stop the current task or report if there is none)
        // 
        // rollup <tag|task> <name> [date-date]

        // construct the full set of commands for our cli app
        // the keys of this map will be used in auto-completion

        // we'll use reflection to call the function referenced in this map
        this.commands.put("help", "showHelp");
        this.commands.put("quit", "quitConsole");
        this.commands.put("clear", "clearConsole");

        this.commands.put("start", "startTask");
        this.commands.put("stop", "stopTask");

        this.commands.put("create tag", "createTag");
        this.commands.put("create task", "createTask");

        this.commands.put("delete tag", "deleteTag");
        this.commands.put("delete task", "deleteTask");

        this.commands.put("show tags", "showTags");
        this.commands.put("show tasks", "showTasks");

        this.commands.put("tag", "assignTag");
        this.commands.put("untag", "unassignTag");

        this.commands.put("report", "printReport");
    }

    private void showHelp(String[] args) throws IOException {
        if (args.length > 0) {
            String cmd = String.join(" ", args);
            this.debug("Got args "+cmd);

            switch (cmd) {
                case "help":
                    this.print("Help for: help\n"
                        .concat("\thelp\t- show general help\n")
                        .concat("\thelp <cmd>\t- show help for <cmd>\n")
                    );
                    break;
                case "clear":
                    this.print("Help for: clear\n"
                        .concat("\tclear\t- clear the screen\n")
                    );
                    break;
                case "quit":
                    this.print("Help for: quit\n"
                        .concat("\tquit\t- quit the console\n")
                    );
                    break;
                case "start":
                    this.print("Help for: start\n"
                        .concat("\tstart <task_name> - start a task matching <task_name>")
                    );
                    break;
                case "stop":
                    this.print("Help for: stop\n"
                        .concat("\tstop\t - stops the current running task")
                    );
                    break;
                case "create":
                    this.print("Help for: create\n"
                        .concat("\tcreate <task|tag> <name> [dueBy] [allowedTime]")
                        .concat("*dueBy should be in the format yyyMMdd")
                        .concat("*allowedTime should be an integer in minutes")
                    );
                    break;
                case "delete":
                    this.print("Help for: delete\n"
                        .concat("\tdelete <task|tag> <name>")
                    );
                    break;
                case "show":
                    this.print("Help for: show\n"
                        .concat("\tshow <tasks|tags> <name>")
                    );
                    break;
                case "create tag":
                    this.print("Help for: create tag\n"
                        .concat("\tcreate tag <name>")
                    );
                    break;
                case "create task":
                    this.print("Help for: create task\n"
                        .concat("\tcreate task <name>")
                    );
                    break;
                case "delete tag":
                    this.print("Help for: delete tag\n"
                        .concat("\tdelete tag <name>")
                    );
                    break;
                case "delete task":
                    this.print("Help for: delete task\n"
                        .concat("\tdelete task <name>")
                    );
                    break;
                case "show tags":
                    this.print("Help for: show tags\n"
                        .concat("\tshow tags [name]\n")
                        .concat("\n*if name is not given show all tags\n")
                        .concat("*name may be short and will be used in a glob match")
                    );
                    break;
                case "show tasks":
                    this.print("Help for: show tasks\n"
                        .concat("\tshow tasks [name]\n")
                        .concat("\n*if name is not given show all tags\n")
                        .concat("*name may be short and will be used in a glob match")
                    );
                    break;
                case "tag":
                    this.print("Help for: tag\n"
                        .concat("\ttag <task_name> <tag_name>")
                    );
                    break;
                case "untag":
                    this.print("Help for: untag\n"
                        .concat("\tuntag <task_name> <tag_name>")
                    );
                    break;
                case "report":
                    this.print("Help for: report\n"
                        .concat("\treport -\tPrint a report of worked time")
                    );
                    break;
                default:
                    this.error("No help for \""+cmd+"\"");
                    break;
            }

            return;
        }

        this.print("General Help\n\ntempor>\n"
            .concat("\tstart <task_name>\t\t\t- start a task\n")
            .concat("\tstop\t\t\t\t\t\t- stop the current task\n")
            .concat("\tshow <tasks|tags> [name]\t\t- show all or one of tag or task\n")
            .concat("\tcreate <task|tag> <name>\t- create a task or tag\n")
            .concat("\tdelete <task|tag> <name>\t- delete a task or tag\n")
            .concat("\ttag <task_name> <tag_name>\t- tag <task_name> with tag <tag_name>\n")
            .concat("\treport\t- print a report of worked time\n")
            .concat("\tuntag <task_name> <tag_name>\t- remove <tag_name> from <task_name>\n")
            .concat("\tquit\t\t\t\t- quit the console\n")
            .concat("\nStrings with spaces MUST use double-quotes e.g. \"Some Tag\"\n")
            .concat("\nFor more specific help run 'help <cmd>'")
        );
    }

    private void startTask(String[] args) throws IOException {
        if (args.length == 0) {
            this.error("'start' requires <name>");
            return;
        }
        this.debug("Start task "+args[0]);
        int r = 0;
        try {
            this.debug("Calling db.startTask with "+args[0]);
            r = this.db.startTask(args[0]);
            this.debug("Result from startTask "+r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (r == 0) {
            this.error("No task by name \""+args[0]+"\"");
            return;
        }
        this.print("Started task "+args[0]);
    }

    private void stopTask(String[] args) throws IOException {
        this.debug("Stopping current task");
        String r = "";
        try {
            r = this.db.stopTask();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (r.equals("")) {
            this.error("No task was running");
        } else {
            this.print("Stopped task "+r);
            // TODO perhaps we should print some summation of
            // time worked etc. when the current task stops
        }
    }

    private void createTag(String[] args) throws IOException {
        int r = 0;
        if (args.length < 1) {
            this.showHelp(new String[]{"create tag"});
        } else {
            try {
                r = this.db.createTag(args[0]);
                this.debug("createTag result "+r);
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE")) {
                    this.error("Tag \""+args[0]+"\" already exists");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private void assignTag(String[] args) throws IOException {
        int r = 0;
        if (args.length < 2) {
            this.showHelp(new String[]{"tag"});
        } else {
            try {
                r = this.db.assignTag(args[0], args[1]);
                this.debug("Assigning "+args[0]+" from "+args[1]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void unassignTag(String[] args) throws IOException {
        int r = 0;
        if (args.length < 2) {
            this.showHelp(new String[]{"untag"});
        } else {
            try {
                r = this.db.unassignTag(args[0], args[1]);
                this.debug("Unassigning "+args[0]+" from "+args[1]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTask(String[] args) throws IOException {
        int r = 0;
        Date d = null;
        int allowedTime = 0;

        if (args.length > 1) {
            Matcher dateMatcher = datePattern.matcher(args[1]);
            if (!dateMatcher.matches()){
                this.error("Date pattern does not match yyyyMMdd "+args[1]);
                // the gatherQuotes function has matched without quotes at the start...
                return;
            }
            try {
                d = Console.dateFormat.parse(args[1]);
            } catch (ParseException e) {
                this.error("Could not parse date from "+args[1]);
                return;
            }
            this.debug("DueBy "+d);
        }

        if (args.length > 2) {
            try {

                allowedTime = Integer.parseInt(args[2]);
                // The user inputs allowedTime as minutes but we store it as seconds
                allowedTime = allowedTime * 60;
            } catch (NumberFormatException e) {
                this.error("Could not parse allowedTime from "+args[2]);
                return;
            }

            this.debug("AllowedTime "+allowedTime);
        }

        // if we pass d=null here the database sets the column to
        // a time far in the future
        try {
            r = this.db.createTask(args[0], d, allowedTime);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                this.error("Task \""+args[0]+"\" already exists");
            } else {
                e.printStackTrace();
            }
        }
    }

    private void printReport(String[] args) throws IOException {
        // we don't care about args here for now; just print something
        ResultSet rs;
        try {
            this.print("Hours worked by tag:");
            rs = this.db.reportTag();
            while (rs.next()) {
                this.print(rs.getString(1), false);
                this.print(" ", false);
                this.print(String.format("%.2f", rs.getFloat(2)), false);
                this.print("\n", false);
            }
            this.print("\n", false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            this.print("Hours worked by task:");
            rs = this.db.reportTask();
            while (rs.next()) {
                this.print(rs.getString(1), false);
                this.print(" ", false);
                this.print(String.format("%.2f", rs.getFloat(2)), false);
                this.print("\n", false);
            }
            this.print("\n", false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTag(String[] args) throws IOException {
        int r = 0;
        try {
            r = this.db.deleteTag(args[0]);
            this.debug("deleteTag result "+r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (r == 0) {
            this.error("No tag named \""+args[0]+"\"");
        } else {
            this.print("Deleted tag \""+args[0]+"\"");
        }
    }

    private void deleteTask(String[] args) throws IOException {
        int r = 0;
        if (args.length == 0) {
            this.error("delete task requires <task_name>");
        } else {
            try {
                r = this.db.deleteTask(args[0]);
                if (r == 0) {
                    this.error("No task \""+args[0]+"\"");
                } else {
                    this.print("Deleted task \""+args[0]+"\"");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showTags(String[] args) throws IOException {
        ResultSet rs;
        try {
            if (args.length == 0) {
                rs = this.db.showTags("");
            } else {
                rs = this.db.showTags(args[0]);
            }
            while (rs.next()) {
                this.print(rs.getString(2), false);
                this.print("\n", false);
            }
            this.print("\n", false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTasks(String[] args) throws IOException {
        ResultSet rs;
        try {
            if (args.length == 0) {
                rs = this.db.showTasks("");
            } else {
                rs = this.db.showTasks(args[0]);
            }
            while (rs.next()) {
                this.print(rs.getString(2), false);
                this.print("\n", false);
            }
            this.print("\n", false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearConsole(String[] args) throws IOException {
        this.console.clear();
    }

    private void quitConsole(String[] args) throws IOException {
        this.print("Bye!");
        try {
            this.console.stop();
        } catch (Exception e) {}
        System.exit(0);
    }
    // end command section

    /**
     * Gather quoted strings to array indexes
     *
     * example: ['"some', 'thing' ,'here"', 'foo']
     * becomes: ['some thing here', 'foo']
     */
    private String[] gatherQuotes(String[] args) throws IOException {
        String s = String.join(" ", args);
        Matcher m = Console.p.matcher(s);

        this.debug("Attempting match with pattern "+Console.p.pattern()+ " against "+s);

        // short-circuit if we don't need to parse quotes
        if (!m.find()) {
            this.debug("No match!");
            return args;
        }

        this.debug("Matched!");

        int offset = 0;
        int start = 0;
        int end = m.start(0);

        ArrayList<String> res = new ArrayList<String>();

        // this could maybe be cleaned up but it works for now
        // TODO this function should split normal strings and gather the quotes
        while (true) {
            if (start != end) {
                // we need to preserve the original args
                // while collapsing the quoted strings
                //
                // we have a string to capture
                // and then split by spaces
                // and insert into the arraylist
                String st = s.substring(start, end).strip();
                if (!st.isEmpty()) {
                    this.debug("Pushing unquoted substring "+st+" to results");
                    res.add(st);
                }
            }

            this.debug("Offset is "+offset);

            // group 1 because we do not want the quotes
            this.debug("Adding "+m.group(1)+" to result");
            res.add(m.group(1));

            // end 0 so we don't recapture the quote
            offset = m.end(0);

            if (!m.find(offset)) {
                // if end < s.length()
                // perhaps we check here for the last arg
                start = offset;
                if (start < s.length()) {
                    this.debug("Capturing remaining end");
                    String[] st = s.substring(start, s.length()).strip().split(" ");
                    if (st.length > 0) {
                        this.debug("Pushing unquoted substring "+st+" to results");
                        for (int i = 0; i < st.length; i++) {
                            res.add(st[i]);
                        }
                    }
                }
                this.debug("Breaking loop");
                break;
            }
            start = offset;
            end = m.start(0);
        }
        // we should convert our arraylist back to String[]
        // and return it
        return res.toArray(new String[0]);
    }

    /**
     * Return a new String[] of String[] s
     * from index 'start' to 'end' exclusive
     */
    private String[] getSlice(String[] s, int start, int end) throws IOException {
        this.debug("Computing slice for "+String.join(" ", s)+" with start: "+start+" end: "+end);
        if (end - start <= 0) {
            return null;
        }
        String[] res = new String[end-start];
        this.debug("target length "+res.length);
        for (int i = 0; i < res.length; i++) {
            this.debug("Adding \""+s[i+start]+"\" to resulting slice");
            res[i] = s[i+start];
        }
        return res;
    }

    /**
     * Print output to this.console
     */
    private void print(String line, boolean end) throws IOException {
        String p = line;
        if (end) {
            p = p += "\n\n";
        }
        this.console.pushToStdOut(p);
    }

    /**
     * Print output to this.console
     */
    private void print(String line) throws IOException {
        this.console.pushToStdOut("\n"+line+"\n\n");
    }

    /**
     * Print info messages to this.console
     */
    private void info(String line) throws IOException {
        this.console.pushToStdOut("\n<tempor.Console: INFO>: "+line+"\n\n");
    }

    /**
     * Print error messages to this.console
     */
    private void error(String line) throws IOException {
        this.console.pushToStdOut("\n<tempor.Console: ERROR>: "+line+"\n\n");
    }

    /**
     * Print debug messages to this.console
     * Only prints when this.debug = True
     */
    private void debug(String line) throws IOException {
        if (this.debug) {
            this.console.pushToStdOut("<tempor.Console: DEBUG>: "+line+"\n");
        }
    }

    /**
     * Run the console
     */
    public void run() throws 
        IOException,
        InvocationTargetException,
        IllegalAccessException,
        IllegalArgumentException,
        NoSuchMethodException
    {
        ConsoleOutput line;
        String buf = "";
        String cmd = "";

        String[] scmd = null;
        String[] args = null;

        // main loop
        while ((line = console.read("tempor> ")) != null) {
            // reset variables
            buf = "";
            cmd = "";
            scmd = null;
            args = null;

            buf = line.getBuffer().trim();
            this.debug("Received "+buf);

            // short-circuit if empty buffer
            if (buf.equals("")) {
                this.error("Please type a command");
                continue;
            }

            // we need to split the cmd on spaces
            // to determine the base command to call
            scmd = buf.split(" ");
            args = null;

            // we parse the args here for the base command
            // and the arguments to pass

            // we should decrement the join each loop to find the base command e.g.
            // 'create tag some thing date-string-here'
            // 'create tag some thing'
            // 'create tag some'
            // 'create tag' -> MATCH break loop and return ending slice

            // TODO multiple spaces should be collapsed
            // so that 'create  tag' doesn't return no cmd

            this.debug("scmd is "+String.join(" ", scmd));
            for (int i = scmd.length; i > 0; i--) {
                cmd = String.join(" ", this.getSlice(scmd, 0, i));

                this.debug("Got slice \""+cmd+"\"");
                cmd = this.commands.get(cmd);

                if (cmd != null) {
                    this.debug("Found method to call "+cmd);

                    // for single commands we do not compute args
                    if (scmd.length <= 1) {
                        this.debug("Skipping argument capture");
                        break;
                    }

                    // set the args to return here
                    args = getSlice(scmd, i, scmd.length);
                    break;
                }
            }

            if (cmd == null) {
                this.error("No such command \""+buf+"\"");
                continue;
            }
            if (args == null) {
                // prevent null errors
                args = new String[0];
            }

            // gather quoted strings
            args = this.gatherQuotes(args);

            this.debug("Calling function \""+cmd+"\""
                .concat(" with args \""+String.join(", ", args)+"\"")
            );

            // we use reflection to call the method specified in the command hashmap
            this.getClass()
                .getDeclaredMethod(cmd, String[].class)
                .invoke(this, new Object[] {args});
        }
    }
}
