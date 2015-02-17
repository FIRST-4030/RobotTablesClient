/*
 * Copyright (C) 2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.outputtablesclient.main;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.gui.InputInterface;
import net.daboross.outputtablesclient.gui.LogInterface;
import net.daboross.outputtablesclient.gui.NetConsoleInterface;
import net.daboross.outputtablesclient.gui.OutputInterface;
import net.daboross.outputtablesclient.gui.RootInterface;
import net.daboross.outputtablesclient.gui.StaleInterface;
import net.daboross.outputtablesclient.gui.SwingClientForward;
import net.daboross.outputtablesclient.gui.SwingInputForward;
import net.daboross.outputtablesclient.gui.SwingOutputForward;
import net.daboross.outputtablesclient.listener.InputListener;
import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.output.OutputLoggerListener;
import net.daboross.outputtablesclient.persist.PersistStorage;
import org.ingrahamrobotics.robottables.RobotTables;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;

public class Application {

    private RobotTablesClient tables;
    private RootInterface root;
    private OutputTablesMain outputMain;
    private InputTablesMain inputMain;
    private OutputInterface outputInterface;
    private InputInterface inputInterface;
    private StaleInterface staleInterface;
    private PersistStorage persistStorage;

    public void run() throws InvocationTargetException, InterruptedException, IOException {
        Output.oLog("Initiating root interface");
        SwingUtilities.invokeAndWait(() -> {
            root = new RootInterface();
            root.show();
        });
        Output.setLogger(new LogInterface(root));
        System.setOut(new PrintStream(new Output.StaticOutputStream(), true));
        System.setErr(new PrintStream(new Output.StaticOutputStream(), true));
        SwingUtilities.invokeLater(() -> {
            Output.oLog("Initiating NetConsole");
            new NetConsoleInterface().addTo(root);
            Output.oLog("NetConsole initiated");
        });
        SwingUtilities.invokeLater(root::registerRestart);
        InetAddress address = findValidBroadcastAddress();
        if (address == null) {
            throw new IOException("Failed to find valid broadcast address!");
        }
        System.out.printf("Found broadcast address: %s%n", address);
        RobotTables tablesStart = new RobotTables(address);
        tables = tablesStart.getClientInterface();
        Output.oLog("Loading persist");
        persistStorage = new PersistStorage();
        startOutput();
        startInput();
        startStale();
        Output.oLog("Starting RobotTables");
        tablesStart.run();
        Output.oLog("Finished startup sequence");
    }

    private InetAddress findValidBroadcastAddress() throws SocketException {
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface iface : Collections.list(ifaces)) {
            for (InterfaceAddress interfaceAddress : iface.getInterfaceAddresses()) {
                InetAddress inetAddress = interfaceAddress.getAddress();
                if (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) {
                    continue;
                }
                InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                // this might be null (only for IPv6 addresses?)
                if (broadcastAddress != null) {
                    return interfaceAddress.getBroadcast();
                }
            }
        }
        return null;
    }

    public void startOutput() throws InvocationTargetException, InterruptedException {
        Output.oLog("Initiating output-tables");
        outputMain = new OutputTablesMain(this);
        Output.oLog("Initiating output-tables logger");
        OutputLoggerListener outputLoggerListener = new OutputLoggerListener();
        outputMain.addListener(outputLoggerListener);
        SwingUtilities.invokeAndWait(() -> {
            Output.oLog("Initiating output-tables interface");
            outputInterface = new OutputInterface(this);
        });
        OutputListener outputInterfaceListener = new SwingOutputForward(outputInterface);
        outputMain.addListener(outputInterfaceListener);
        Output.oLog("Subscribing to output-tables");
        outputMain.subscribe();
    }

    public void startInput() throws InvocationTargetException, InterruptedException {
        inputMain = new InputTablesMain(this);
        SwingUtilities.invokeAndWait(() -> {
            Output.iLog("Initiating input-tables interface");
            inputInterface = new InputInterface(this);
        });
        InputListener inputInterfaceListener = new SwingInputForward(inputInterface);
        inputMain.addListener(inputInterfaceListener);
        Output.iLog("Subscribing to input-tables");
        inputMain.subscribe();
    }

    public void startStale() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            Output.oLog("Initiating stale interface");
            staleInterface = new StaleInterface(this);
        });
        tables.addClientListener(new SwingClientForward(staleInterface), true);
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        new Application().run();
    }

    public RootInterface getRoot() {
        return root;
    }

    public OutputTablesMain getOutput() {
        return outputMain;
    }

    public InputTablesMain getInput() {
        return inputMain;
    }

    public PersistStorage getPersist() {
        return persistStorage;
    }

    public RobotTablesClient getTables() {
        return tables;
    }
}
