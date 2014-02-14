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
package net.daboross.outputtablesclient.gui;

import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.output.Output;

public class GUIOutput implements Output.StaticLogger {

    private final OutputTablesGUI main;

    public GUIOutput(OutputTablesGUI main) {
        this.main = main;
    }

    @Override
    public void log(final String message) {
        System.out.println(message);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                main.loggingTextArea.append(message + "\n");
            }
        });
    }
}
