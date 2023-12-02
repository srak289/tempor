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
        this.commands.put("quit", null);
        this.commands.put("clear", null);
        this.commands.put("cls", null);

        this.commands.put("create", null);
        this.commands.put("delete", null);

        this.commands.put("show", null);
        this.commands.put("search", null);
        this.commands.put("start", null);
        this.commands.put("stop", null);

        this.commands.put("create tag", null);
        this.commands.put("create task", null);
        this.commands.put("create help", null);

        this.commands.put("delete tag", null);
        this.commands.put("delete task", null);
        this.commands.put("delete help", null);

        this.commands.put("show tags", null);
        this.commands.put("show tasks", null);
        this.commands.put("show help", null);

        this.commands.put("search tags", null);
        this.commands.put("search tasks", null);
        this.commands.put("search help", null);

        this.commands.put("start help", null);
        this.commands.put("stop help", null);
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

    private void showHelp(String args) {
        System.out.println("This is the help");
    }

    private void showConnection(String args) {
        System.out.println("The connection");
    }

    public void run() throws IOException {
        ConsoleOutput line;
        String buf = "";
        String cmd = "";
        while ((line = console.read("chronos> ")) != null) {

            // perhaps we'll end up with something like
            // for key : commands.keySet
            // if key.equals(buf)
            // commands.get(key).call() or whatever
            // commands.get(key).callWithArgs(console, buf.substring()) or whatever
            // we might substring to get the important args from the last part
            // of the command

            buf = line.getBuffer().trim();
            this.console.pushToStdOut("======>\"" +buf+ "\"\n");

            if (buf.equalsIgnoreCase("quit")) {
                try {
                    this.console.stop();
                } catch (Exception e) {}
                System.exit(0);

            } else if (buf.equalsIgnoreCase("clear")){
                this.console.clear();
            } else {
                try {
                    cmd = this.commands.get(buf);
                    if (cmd == null) {
                        System.out.println("No commamd "+buf);
                    } else {
                        //System.out.println(this.getClass().getDeclaredMethod(cmd, String.class));
                        Console.class.getDeclaredMethod(cmd, String.class).invoke(this, cmd);
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
