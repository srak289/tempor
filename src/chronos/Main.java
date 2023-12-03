package chronos;

import java.io.IOException;
import java.sql.SQLException;
import java.lang.reflect.*;

import chronos.Console;
import chronos.DatabaseClient;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;
import net.sourceforge.argparse4j.impl.Arguments;


public class Main {

    public static void main(String[] args) {
        try {

            ArgumentParser ap = ArgumentParsers.newFor("chronos").build();
            Namespace parsed_args = null;
            ap.addArgument("--dbpath");
            ap.addArgument("--debug").action(Arguments.storeTrue());
            parsed_args = ap.parseArgs(args);
            if (parsed_args.get("debug")) {
                System.out.println("Parsed args \""+parsed_args+"\"");
            }

            // argparse guarantees we won't get an empty argument if the `--dbpath`
            // flag is specified; and if it wasn't will give us `null` when we `get("dbpath")
            // like HashMap would
            //
            // if the client gets `null` it will use the default path (./time.db)
            DatabaseClient db = new DatabaseClient(
                parsed_args.get("dbpath"),
                parsed_args.get("debug")
            );
            db.connect();
            db.setupTables();

            Console console = new Console(db, parsed_args.get("debug"));
            console.run();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
