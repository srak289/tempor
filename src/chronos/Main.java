import chronos.db.*;
import chronos.exceptions.*;
import chronos.models.*;

public class Main {

    showHelp() {
    }

    tabComplete() {
    }

    clearScreen() {
    }

    public static void main(String[] args) {
        ConnectionManager cm = new ConnectionManager();
        cm.connect();
        cm.setupTables();

        while(true) {
            System.out.print("chronos> ");

        }

        System.out.println("Welcome!");
    }
}
