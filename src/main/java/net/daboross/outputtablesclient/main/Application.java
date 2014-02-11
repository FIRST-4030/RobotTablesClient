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
import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.gui.GUIOutput;
import net.daboross.outputtablesclient.gui.OutputTablesGUI;
import net.daboross.outputtablesclient.gui.SwingListenerForward;
import net.daboross.outputtablesclient.output.LoggerListener;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class Application {

    public static void main(String[] args) throws IOException {
//        DotNetTables.startClient("4030");
        DotNetTables.startClient("127.0.0.1");

        final OutputTableMain main = new OutputTableMain();
        main.subscribe();

        LoggerListener loggerListener = new LoggerListener(main);
        main.addListener(loggerListener);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                OutputTablesGUI gui = new OutputTablesGUI(main);
                main.addListener(new SwingListenerForward(gui));
                Output.setLogger(new GUIOutput(gui));
                gui.show();
            }
        });


    }
}
