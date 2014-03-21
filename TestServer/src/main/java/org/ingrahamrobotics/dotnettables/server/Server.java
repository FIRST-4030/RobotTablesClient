package org.ingrahamrobotics.dotnettables.server;

import java.io.IOException;
import java.util.Random;
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
//        Map<String, DotNetTable> map = new HashMap<>();
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.print("> ");
//            String value = scanner.nextLine();
//            String[] split = value.split(" ");
//            if (split.length < 3) {
//                System.err.println("ERR: args < 2");
//                continue;
//            }
//            switch (split[0].toLowerCase()) {
//                case "ctable":
//                    String tableKeyTC = split[1];
//                    String tableNameTC = split[2];
//                    DotNetTable dnt = DotNetTables.publish(tableKeyTC);
//                    outputTables.setValue(tableKeyTC, tableNameTC);
//                    outputTables.send();
//                    dnt.send();
//                    map.put(tableKeyTC, dnt);
//                    break;
//                case "set":
//                    if (split.length < 4) {
//                        System.err.println("ERR: args < 3");
//                        break;
//                    }
//                    String tableKeyTS = split[1];
//                    String keyTS = split[2];
//                    String valueTS = split[3];
//                    DotNetTable tableTS = map.get(tableKeyTS);
//                    if (tableTS == null) {
//                        System.err.println("ERR: Unkown table");
//                        break;
//                    }
//                    tableTS.setValue(keyTS, valueTS);
//                    tableTS.send();
//                    break;
//                case "del":
//                    String tableKeyTD = split[1];
//                    String keyTD = split[2];
//                    DotNetTable tableTD = map.get(tableKeyTD);
//                    if (tableTD == null) {
//                        System.err.println("ERR: Unkown table");
//                        break;
//                    }
//                    tableTD.remove(keyTD);
//                    tableTD.send();
//                    break;
//            }
//        }

        // v2

//        final DotNetTable robotInput = DotNetTables.subscribe("robot-input");
//        final DotNetTable robotInputDefault = DotNetTables.publish("robot-input-default");
//        robotInputDefault.setInterval(2000);
//        robotInputDefault.setValue("key-1", "key-2");
//        robotInputDefault.setValue("key-2", "key-2asdf");
//        robotInputDefault.setValue("key-3", "key-2fdsa");
//        robotInput.onChange(new DotNetTable.DotNetTableEvents() {
//            @Override
//            public void changed(final DotNetTable table) {
//                String value = robotInput.getValue("_DRIVER_FEEDBACK_KEY");
//                System.out.println("Input: " + value);
//                if (value != null) {
//                    robotInputDefault.setValue("_DRIVER_FEEDBACK_KEY", value);
//                }
//            }
//
//            @Override
//            public void stale(final DotNetTable table) {
//
//            }
//        });
//        DotNetTable[] tables = new DotNetTable[10];
//        for (int i = 0; true; i++) {
//            if (tables[i % 10] == null) {
//                tables[i % 10] = DotNetTables.publish("table-" + i % 10);
//                outputTables.setValue("table-" + (i % 10), "Level" + i % 10);
//                Thread.sleep(200);
//                outputTables.send();
//            }
//            if (i % 10 == 0) {
//                double value = new Random().nextDouble() * 50 + 50;
//                tables[0].setValue(":RangeGUI", value);
//                System.out.println("RangeGUI: " + value);
//            }
//            tables[i % 10].setValue("Key" + (i % 7), "Value" + i);
//
//            Thread.sleep(200);
//            tables[i % 10].send();
//            if (i % 2 == 0) {
//                outputTables.send();
//            }
//        }
    }
}
