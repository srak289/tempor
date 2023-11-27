package chronos;

import chronos.db.*;
import chronos.exceptions.*;
import chronos.models.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

import java.util.Scanner;


public class Main {

    void showHelp() {
    }

    void tabComplete() {
    }

    void clearScreen() {
    }

    public static void main(String[] args) {

        ArgumentParser ap = ArgumentParsers.newFor("chronos").build();
        ap.addArgument("--cli");
        System.out.println("What");
        try {
            System.out.println(ap.parseArgs(args));
        } catch(ArgumentParserException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // end parsing

        ConnectionManager cm = new ConnectionManager();
        try {
            cm.connect();
            cm.setupTables();
        } catch (ChronosDatabaseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scanner stdin = new Scanner(System.in);
        String buf;

        while(true) {
            System.out.print("chronos> ");
            buf = stdin.nextLine();
            System.out.print("You entered "+buf);
        }
    }
}
