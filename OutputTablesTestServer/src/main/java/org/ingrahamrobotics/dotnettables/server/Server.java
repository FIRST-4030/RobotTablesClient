package org.ingrahamrobotics.dotnettables.server;

import java.io.IOException;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        // Start NetworkTables
        try {
            DotNetTables.startServer();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        DotNetTable outputTables = DotNetTables.publish("output-tables");
        DotNetTable[] tables = new DotNetTable[3];
        for (int i = 0; true; i++) {
            if (tables[i % 3] == null) {
                tables[i % 3] = DotNetTables.publish("table-" + i % 3);
                outputTables.setValue("table-" + (i % 3), "Level" + i % 3);
                Thread.sleep(500);
                outputTables.send();
            }
            tables[i % 3].setValue("Key" + (i % 4), "Value" + i);
            Thread.sleep(1000);
            tables[i % 3].send();
            if (i % 2 == 0) {
                outputTables.send();
            }
        }
    }
}
