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
import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.gui.InputInterface;
import net.daboross.outputtablesclient.gui.LogInterface;
import net.daboross.outputtablesclient.gui.NetConsoleInterface;
import net.daboross.outputtablesclient.gui.OutputInterface;
import net.daboross.outputtablesclient.gui.RootInterface;
import net.daboross.outputtablesclient.gui.SwingInputForward;
import net.daboross.outputtablesclient.gui.SwingOutputForward;
import net.daboross.outputtablesclient.listener.InputListener;
import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.output.OutputLoggerListener;
import net.daboross.outputtablesclient.persist.PersistStorage;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Application {

    private static final String CLIENT_ADDRESS = "4030";
    private RootInterface root;
    private OutputTablesMain outputMain;
    private InputTablesMain inputMain;
    private OutputInterface outputInterface;
    private InputInterface inputInterface;
    private InputListener inputInterfaceListener;
    private OutputListener outputInterfaceListener;
    private PersistStorage persistStorage;

    public void run() throws InvocationTargetException, InterruptedException, IOException {
        Output.oLog("Initiating root interface");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                root = new RootInterface(Application.this);
                root.show();
                Output.setLogger(new LogInterface(root));
                System.setOut(new PrintStream(new Output.StaticOutputStream(), true));
                System.setErr(new PrintStream(new Output.StaticOutputStream(), true));
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Output.oLog("Initiating NetConsole");
                new NetConsoleInterface().addTo(root);
                Output.oLog("NetConsole initiated");
            }
        });
        Output.oLog("Starting client on " + CLIENT_ADDRESS);
        DotNetTables.startClient(CLIENT_ADDRESS);
        Output.oLog("Loading persist");
        persistStorage = new PersistStorage();
        startOutput();
        startInput();
        Output.oLog("Finished startup sequence");
    }

    public void startOutput() throws InvocationTargetException, InterruptedException {
        Output.oLog("Initiating output-tables");
        outputMain = new OutputTablesMain();
        Output.oLog("Initiating output-tables logger");
        OutputLoggerListener outputLoggerListener = new OutputLoggerListener(outputMain);
        outputMain.addListener(outputLoggerListener);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Output.oLog("Initiating output-tables interface");
                outputInterface = new OutputInterface(Application.this);
                outputInterfaceListener = new SwingOutputForward(outputInterface);
                outputMain.addListener(outputInterfaceListener);
            }
        });
        Output.oLog("Subscribing to output-tables");
        outputMain.subscribe();
    }

    public void startInput() throws InvocationTargetException, InterruptedException {
        inputMain = new InputTablesMain();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Output.iLog("Initiating input-tables interface");
                inputInterface = new InputInterface(Application.this);
                inputInterfaceListener = new SwingInputForward(inputInterface);
                inputMain.addListener(inputInterfaceListener);
            }
        });
        Output.iLog("Subscribing to input-tables");
        inputMain.subscribe();
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        new Application().run();
    }

    public RootInterface getRoot() {
        return root;
    }

    public String getClientAddress() {
        return CLIENT_ADDRESS;
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
}
