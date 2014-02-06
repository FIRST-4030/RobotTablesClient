package org.ingrahamrobotics.dotnettables.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        // Start NetworkTables
        try {
            DotNetTables.startServer();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

        // Publish and subscribe to a table
        DotNetTable outputTables = DotNetTables.publish("output-tables");
        DotNetTable[] tables = new DotNetTable[10];
        // Put new data into our published table every second
        for (int i = 0; true; i++) {
            if (tables[i % 10] == null) {
                tables[i % 10] = DotNetTables.publish("table-" + (i % 10));
                outputTables.setValue("table-" + (i % 10), "t" + i % 10);
                Thread.sleep(500);
                outputTables.send();
            }
            tables[i % 10].setValue("k" + (i % 4), "v" + i);
            if (i % 3 == 0) {
                Thread.sleep(1000);
                tables[i % 10].send();
                if (i % 2 == 0) {
                    outputTables.send();
                }
                System.out.println("Sending " + tables[i % 10].name());
            }
        }
    }
}
