import chronos.db.*;
import chronos.exceptions.*;
import chronos.models.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;


public class Main {
    public static void main(String[] args) {
        // cm = new ConnectionManager();
        // cm.connect();
        // cm.setupTables();
        ArgumentParser ap = ArgumentParsers.newFor("chronos").build();
        ap.addArgument("--cli");
        System.out.println("What");
        try {
            System.out.println(ap.parseArgs(args));
        } catch(ArgumentParserException e) {
            System.out.println(e.getStackTrace());
        }
    }
}
