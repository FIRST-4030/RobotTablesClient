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
package net.daboross.outputtablesclient;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.JLabel;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class ClientApplication implements DotNetTable.DotNetTableEvents {

    private final ClientFrameManager manager;
    private final DotNetTable names;
    private final HashSet<String> alreadyAddedTables;

    public ClientApplication() {
        manager = new ClientFrameManager();
        names = DotNetTables.subscribe("output-tables");
        alreadyAddedTables = new HashSet<>();
    }

    public void start() {
        names.onChange(this);
        manager.show();
        manager.log("Started");
        this.changed(names);
    }

    public static void main(String[] args) throws IOException {
        DotNetTables.startClient("4030");
        new ClientApplication().start();
    }

    @Override
    public void changed(final DotNetTable table) {
        if (table.name().equals("output-tables")) {
            for (Enumeration e = table.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                if (alreadyAddedTables.add(key) && !key.equals("_UPDATE_INTERVAL")) {
                    manager.log("[%s] *", key);
                    String value = table.getValue(key);
                    TableOutputPanel panel = new TableOutputPanel(manager, value);
                    panel.init(DotNetTables.subscribe(key));
                    JLabel label = manager.addCollapsibleLabeledComponent(value, panel);
                    panel.setLabel(label);
                }
            }
        }
    }

    @Override
    public void stale(final DotNetTable table) {

    }
}
