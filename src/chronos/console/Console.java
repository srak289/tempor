package chronos.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.*;

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

    public Console(DatabaseClient db) throws IOException {

        this.commands = new HashMap<String, String>();

        initCommands();

        this.db = db;
        this.console = new org.jboss.jreadline.console.Console();
        this.console.addCompletion(this);
    }

    /**
     * create <tag|task> <name> [allowed_hours] [due_date]
     * show [tags,tasks]
     * search <tags|tasks> <name>
     * tag <name> <tag_name>
     * untag <name> <tag_name>
     * start <task_name> (can start while another task is started, it will stop the current)
     * stop (no argument, just stop the current task or report if there is none)
     *
     * rollup <tag|task> <name> [date-date]
     *
     */
    private void initCommands() {

        // construct the full set of commands for our cli app
        // the keys of this map will be used in auto-completion

        // we'll use reflection to call the function referenced in this map
        this.commands.put("help", "showHelp");
        this.commands.put("?", "showHelp");
        this.commands.put("quit", "quitConsole");
        this.commands.put("clear", "clearConsole");
        this.commands.put("cls", "clearConsole");

        this.commands.put("create", "showCreateHelp");
        this.commands.put("delete", "showDeleteHelp");

        this.commands.put("show", "showCurrent");
        this.commands.put("search", "showSearchHelp");
        this.commands.put("start", "startTask");
        this.commands.put("stop", "stopTask");

        this.commands.put("create tag", "createTag");
        this.commands.put("create task", "createTask");
        this.commands.put("create help", "showCreateHelp");

        this.commands.put("delete tag", "deleteTag");
        this.commands.put("delete task", "deleteTask");
        this.commands.put("delete help", "showDeleteHelp");

        this.commands.put("show tags", "showTags");
        this.commands.put("show tasks", "showTasks");
        this.commands.put("show help", "showShowHelp");

        this.commands.put("search tags", "searchTags");
        this.commands.put("search tasks", "searchTasks");
        this.commands.put("search help", "showSearchHelp");

        this.commands.put("start help", "showStartHelp");
        this.commands.put("stop help", "showStopHelp");
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

    // begin command section
    // begin help section
    private void showHelp(String[] args) throws IOException {
        this.print("The chronos CLI\n"
            .concat("\tstart <task_name>\t- start a task\n")
            .concat("\tstop\t- stop the current task\n")
            .concat("\tshow <task|tag> <name>\n")
            .concat("\tcreate <task|tag> <name>\t- create a task or tag\n")
            .concat("\tdelete <task|tag> <name>\t- delete a task or tag\n")
            .concat("\tquit\t- quit the console\n")
        );
    }

    private void showCreateHelp(String[] args) throws IOException {
        this.print("Help for: show\n"
            .concat("\tshow <task|tag> <name>")
        );
    }

    private void showDeleteHelp(String[] args) throws IOException {
        this.print("Help for: show\n"
            .concat("\tshow <task|tag> <name>")
        );
    }

    private void showShowHelp(String[] args) throws IOException {
        this.print("Help for: show\n"
            .concat("\tshow <task|tag> <name>")
        );
    }

    private void showSearchHelp(String[] args) throws IOException {
    }

    private void showStartHelp(String[] args) throws IOException {
        this.print("Help for: start\n"
            .concat("\tstart <task_name>\t- start a task given by task_name\n")
        );
    }

    private void showStopHelp(String[] args) throws IOException {
        this.print("Help for: stop\n"
            .concat("\tstop\t- stop the current task\n")
        );
    }

    // end help section

    private void create(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void showCurrent(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void startTask(String[] args) throws IOException {
        // accepts a task name to start
        // 
    }

    private void stopTask(String[] args) throws IOException {
        // print "no task" if no task running
        // else print report
    }

    private void createTag(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void createTask(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void deleteTag(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void deleteTask(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void showTags(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void showTasks(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void searchTags(String[] args) throws IOException {
        this.print("Got args "+args);
    }

    private void searchTasks(String[] args) throws IOException {
        this.print("Got args "+args);
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
        //this.print("Computing slice for "+String.join(" ", s)+" with start: "+start+" end: "+end);
        if (end - start <= 0) {
            return null;
        }
        String[] res = new String[end-start];
        //this.print("DBG: target length "+res.length);
        for (int i = 0; i < res.length; i++) {
            //this.print("DBG: Adding \""+s[i+start]+"\" to resulting slice");
            res[i] = s[i+start];
        }
        return res;
    }

    /**
     * Convenience funciton to print to this.console
     */
    private void print(String line) throws IOException {
        this.console.pushToStdOut(line+"\n");
    }

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

        while ((line = console.read("chronos> ")) != null) {
            // reset variables
            buf = "";
            cmd = "";
            scmd = null;
            args = null;

            buf = line.getBuffer().trim();
            //this.print("DBG: Received "+buf);

            // short-circuit if empty buffer
            if (buf.equals("")) {
                this.print("E: Please type a command");
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

            //this.print("DBG: scmd is "+String.join(" ", scmd));
            for (int i = scmd.length; i >= 0; i--) {
                cmd = String.join(" ", this.getSlice(scmd, 0, i));

                this.print("DBG: Got slice \""+cmd+"\"");
                cmd = this.commands.get(cmd);

                if (cmd != null) {
                    //this.print("DBG: Found method to call "+cmd);

                    // for single commands we do not compute args
                    if (scmd.length <= 1) {
                        break;
                    }

                    // set the args to return here
                    args = getSlice(scmd, i, scmd.length);
                    //this.print("DBG: args is "+String.join(" ", args));
                    break;
                }
            }

            if (cmd == null) {
                this.print("E: No such command \""+buf+"\"");
                continue;
            }

            // we use reflection to call the method specified in the command hashmap
            this.getClass()
                .getDeclaredMethod(cmd, String[].class)
                .invoke(this, new Object[] {args});
        }
    }
}
