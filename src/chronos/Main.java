import chronos.db.*;
import chronos.exceptions.*;
import chronos.models.*;

public class Main {
    public static void main(String[] args) {
        cm = new ConnectionManager();
        cm.connect();
        cm.setupTables();
        System.out.println("Welcomd!");
    }
}