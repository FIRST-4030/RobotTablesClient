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
import net.daboross.outputtablesclient.gui.OutputTablesInterfaceMain;
import net.daboross.outputtablesclient.gui.OutputTablesInterfaceRoot;
import net.daboross.outputtablesclient.gui.SwingListenerForward;
import net.daboross.outputtablesclient.output.LoggerListener;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Application {

    private static final String CLIENT_ADDRESS = "4030";
    private OutputTablesInterfaceRoot root;

    public void run() throws InvocationTargetException, InterruptedException, IOException {
        Output.log("Initiating root interface");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                root = new OutputTablesInterfaceRoot();
                root.show();
                Output.setLogger(new GUIOutput(root));
                System.setOut(new PrintStream(new Output.StaticOutputStream(), true));
                System.setErr(new PrintStream(new Output.StaticOutputStream(), true));
            }
        });
        Output.log("Starting client on " + CLIENT_ADDRESS);
        DotNetTables.startClient(CLIENT_ADDRESS);
        Output.log("Initiating OutputTablesMain");
        final OutputTablesMain main = new OutputTablesMain();
        Output.log("Initiating LoggerListener");
        LoggerListener loggerListener = new LoggerListener(main);
        main.addListener(loggerListener);
        Output.log("Starting main interface initiation");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Output.log("Initiating main interface");
                OutputTablesInterfaceMain gui = new OutputTablesInterfaceMain(main, root);
                Output.log("Adding listener");
                main.addListener(new SwingListenerForward(gui));
            }
        });
        Output.log("Subscribing to output-tables");
        main.subscribe();
        Output.log("Finished startup sequence");
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
        new Application().run();
    }
}
