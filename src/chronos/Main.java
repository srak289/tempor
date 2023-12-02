package chronos;

import java.io.IOException;

import chronos.console.Console;
import chronos.db.*;
import chronos.exceptions.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;


public class Main {

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

        // if dbpacth is spec'd we should pass to the client
        // otherwise use the default
        DatabaseClient db = new DatabaseClient();
        try {
            db.connect();
            db.setupTables();
        } catch (ChronosDatabaseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Console console = new Console(db);
            console.run();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
