package chronos;

import java.io.IOException;
import java.sql.SQLException;
import java.lang.reflect.*;

import chronos.console.Console;
import chronos.db.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;


public class Main {

    public static void main(String[] args) throws
        IOException,
        SQLException,
        ArgumentParserException,
        InvocationTargetException,
        IllegalAccessException,
        NoSuchMethodException
    {
        // any of these exceptions crash our program so we may as well
        // just throw them

        ArgumentParser ap = ArgumentParsers.newFor("chronos").build();
        Namespace parsed_args = null;
        ap.addArgument("--dbpath");
        parsed_args = ap.parseArgs(args);
        // System.out.println("Parsed args \""+parsed_args+"\"");

        // argparse guarantees we won't get an empty argument if the `--dbpath`
        // flag is specified; and if it wasn't will give us `null` when we `get("dbpath")
        // like HashMap would
        //
        // if the client gets `null` it will use the default path (./time.db)
        DatabaseClient db = new DatabaseClient(parsed_args.get("dbpath"));
        db.connect();
        db.setupTables();

        Console console = new Console(db);
        console.run();
    }
}
