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
import net.daboross.outputtablesclient.gui.GUIOutput;
import net.daboross.outputtablesclient.gui.InputInterface;
import net.daboross.outputtablesclient.gui.InterfaceRoot;
import net.daboross.outputtablesclient.gui.NetConsoleInterface;
import net.daboross.outputtablesclient.gui.OutputInterface;
import net.daboross.outputtablesclient.gui.SwingOutputForward;
import net.daboross.outputtablesclient.output.LoggerListener;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Application {

    private static final String CLIENT_ADDRESS = "4030";
    private InterfaceRoot root;

    public void run() throws InvocationTargetException, InterruptedException, IOException {
        Output.log("Initiating root interface");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                root = new InterfaceRoot();
                root.show();
                Output.setLogger(new GUIOutput(root));
                System.setOut(new PrintStream(new Output.StaticOutputStream(), true));
                System.setErr(new PrintStream(new Output.StaticOutputStream(), true));
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Output.log("Initiating NetConsole");
                new NetConsoleInterface().addTo(root);
                Output.log("NetConsole initiated");
            }
        });
        Output.log("Starting client on " + CLIENT_ADDRESS);
        DotNetTables.startClient(CLIENT_ADDRESS);
        startOutput();
        startInput();
        Output.log("Finished startup sequence");
    }

    public void startOutput() throws InvocationTargetException, InterruptedException {
        Output.log("Initiating output-tables");
        final OutputTablesMain outputMain = new OutputTablesMain();
        Output.log("Initiating output-tables logger");
        LoggerListener loggerListener = new LoggerListener(outputMain);
        outputMain.addListener(loggerListener);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Output.log("Initiating output-tables interface");
                OutputInterface outputGui = new OutputInterface(outputMain, root);
                outputMain.addListener(new SwingOutputForward(outputGui));
            }
        });
        Output.log("Subscribing to output-tables");
        outputMain.subscribe();
    }

    public void startInput() throws InvocationTargetException, InterruptedException {
        final InputTablesMain inputMain = new InputTablesMain();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Output.logI("Initiating input-tables interface");
                InputInterface inputGui = new InputInterface(inputMain, root);
                inputMain.addListener(inputGui);
            }
        });
        Output.logI("Subscribing to input-tables");
        inputMain.subscribe();
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        new Application().run();
    }
}
