package chronos;

import chronos.db.*;
import chronos.exceptions.*;
import chronos.models.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

import org.jboss.jreadline.console.*;
import org.jboss.jreadline.console.settings.Settings;
import org.jboss.jreadline.complete.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Main {

    private String[] commands = {"help", "new", "clear", "cls", "?"};

    void showHelp() {
    }

    public static void main(String[] args) {

        ArgumentParser ap = ArgumentParsers.newFor("chronos").build();
        ap.addArgument("--dbpath");
        try {
            Namespace parsed_args = ap.parseArgs(args);
            // System.out.println("Parsed args \""+parsed_args+"\"");
        } catch(ArgumentParserException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // end parsing

        DatabaseClient db = new DatabaseClient();
        try {
            db.connect();
            db.setupTables();
        } catch (ChronosDatabaseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Completion completer = new Completion() {
            @Override
            public void complete(CompleteOperation co) {
            }
        };

        try {
            Console console = new Console();
            ConsoleOutput line;
            while ((line = console.read("chronos> ")) != null) {
                    console.pushToStdOut("======>\"" +line.getBuffer()+ "\"\n");
                if (
                    line.getBuffer().equalsIgnoreCase("quit") ||
                    line.getBuffer().equalsIgnoreCase("exit")
                ) {
                    try {
                        console.stop();
                    } catch (Exception e) {}
                    System.exit(0);
                } else if (
                    line.getBuffer().equalsIgnoreCase("help") ||
                    line.getBuffer().equalsIgnoreCase("?")
                ) {
                    console.pushToStdOut("the help\n");
                } else if (
                    line.getBuffer().equalsIgnoreCase("cls") ||
                    line.getBuffer().equalsIgnoreCase("clear")
                ) {
                    console.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
