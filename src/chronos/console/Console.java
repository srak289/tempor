package chronos.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.*;
import java.sql.SQLException;

import org.jboss.jreadline.console.*;
import org.jboss.jreadline.console.settings.Settings;
import org.jboss.jreadline.complete.*;
import org.jboss.jreadline.edit.actions.Operation;

import chronos.db.DatabaseClient;


public class Console implements Completion {

    private org.jboss.jreadline.console.Console console;
    private Completion completer;
    private DatabaseClient db;
    private HashMap<String, String> commands;
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
        // search <tags|tasks> <name>
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

        this.commands.put("create", "showHelp");
        this.commands.put("delete", "showHelp");

        this.commands.put("show", "showCurrent");
        this.commands.put("search", "showHelp");
        this.commands.put("start", "startTask");
        this.commands.put("stop", "stopTask");

        this.commands.put("create tag", "createTag");
        this.commands.put("create task", "createTask");

        this.commands.put("delete tag", "deleteTag");
        this.commands.put("delete task", "deleteTask");

        this.commands.put("show tags", "showTags");
        this.commands.put("show tasks", "showTasks");

        this.commands.put("search tags", "searchTags");
        this.commands.put("search tasks", "searchTasks");
    }

    // begin command section
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
                        .concat("\tcreate <task|tag> <name>")
                    );
                    break;
                case "delete":
                    this.print("Help for: delete\n"
                        .concat("\tdelete <task|tag> <name>")
                    );
                    break;
                case "show":
                    this.print("Help for: show\n"
                        .concat("\tshow <task|tag> <name>")
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
                case "show tag":
                    this.print("Help for: show tag\n"
                        .concat("\tshow tag <name>")
                    );
                    break;
                case "show task":
                    this.print("Help for: show task\n"
                        .concat("\tshow task <name>")
                    );
                    break;
                default:
                    this.error("No help for \""+cmd+"\"");
                    break;
            }

            return;
        }

        this.print("General Help\n\nchronos>\n"
            .concat("\tstart <task_name>\t\t- start a task\n")
            .concat("\tstop\t\t\t\t- stop the current task\n")
            .concat("\tshow <task|tag> [name]\t- show all or one of tag or task\n")
            .concat("\tcreate <task|tag> <name>\t- create a task or tag\n")
            .concat("\tdelete <task|tag> <name>\t- delete a task or tag\n")
            .concat("\tquit\t\t\t\t- quit the console\n")
            .concat("\nFor more specific help run 'help <cmd>'")
        );
    }

    private void startTask(String[] args) throws IOException {
        this.debug("Start task");
        if (args.length == 0) {
            this.error("'start' requires <name>");
        }
        this.debug("Args is "+String.join(" ", args));
        try {
            this.debug("Calling db.startTask with "+args[0]);
            int result = this.db.startTask(args[0]);
            this.debug("Result from startTask "+result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void stopTask(String[] args) throws IOException {
        this.info("Stopping current task");
        // print "no task" if no task running
        // else print report
    }

    private void createTag(String[] args) throws IOException {
    }

    private void createTask(String[] args) throws IOException {
        //public int createTask(String name, Date dueBy, int allowedTime) throws SQLException {
    }

    private void deleteTag(String[] args) throws IOException {
    }

    private void deleteTask(String[] args) throws IOException {
    }

    private void showTags(String[] args) throws IOException {
    }

    private void showTasks(String[] args) throws IOException {
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
     * Print messages to this.console
     */
    private void print(String line) throws IOException {
        this.console.pushToStdOut("\n"+line+"\n\n");
    }

    /**
     * Print info messages to this.console
     */
    private void info(String line) throws IOException {
        this.console.pushToStdOut("\n<chronos.console.Console: INFO>: "+line+"\n\n");
    }

    /**
     * Print error messages to this.console
     */
    private void error(String line) throws IOException {
        this.console.pushToStdOut("\n<chronos.console.Console: ERROR>: "+line+"\n\n");
    }

    /**
     * Print debug messages to this.console
     * Only prints when this.debug = True
     */
    private void debug(String line) throws IOException {
        if (this.debug) {
            this.console.pushToStdOut("<chronos.console.Console: DEBUG>: "+line+"\n");
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
        while ((line = console.read("chronos> ")) != null) {
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

            this.debug("scmd is "+String.join(" ", scmd));
            for (int i = scmd.length; i > 0; i--) {
                cmd = String.join(" ", this.getSlice(scmd, 0, i));

                this.debug("Got slice \""+cmd+"\"");
                cmd = this.commands.get(cmd);

                // WISHLIST:
                // if we want unambiguous shorthand
                // we should make best-effort matches with short words
                // e.g. c tas -> create task
                // or q -> quit
                // or cl -> clear

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

            this.debug("Calling function \""+cmd+"\""
                .concat(" with args \""+String.join(" ", args)+"\"")
            );

            // we use reflection to call the method specified in the command hashmap
            this.getClass()
                .getDeclaredMethod(cmd, String[].class)
                .invoke(this, new Object[] {args});
        }
    }
}
