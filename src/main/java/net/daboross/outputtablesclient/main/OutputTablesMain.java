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

import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.listener.OutputListenerForward;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;
import org.ingrahamrobotics.robottables.api.listeners.TableUpdateListener;

public class OutputTablesMain implements ClientUpdateListener, TableUpdateListener {

    private final OutputListenerForward l = new OutputListenerForward();
    private final RobotTablesClient client;

    public OutputTablesMain(Application application) {
        client = application.getTables();
    }

    public void subscribe() {
        client.addClientListener(this);
    }

    public void addListener(OutputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(OutputListener listener) {
        l.removeListener(listener);
    }

    // TODO: Stale processing
//    @Override
//    public synchronized void stale(DotNetTable dnt) {
//        if (!dnt.name().equals(OUTPUT_TABLE)) {
//            l.onTableStale(dnt.name());
//        }
//    }

    @Override
    public void onTableChangeType(final RobotTable table, final TableType oldType, final TableType newType) {

    }

    @Override
    public void onNewTable(final RobotTable table) {
        Output.oLog("Table created '%s'", table.getName());
        table.addUpdateListener(this);
        l.onTableCreate(table);
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        Output.oLog("Table updated '%s'", table.getName());
        l.onUpdate(table, key, value, action);
    }

    @Override
    public void onUpdateAdmin(final RobotTable table, final String key, final String value, final UpdateAction action) {
        // TODO: Something here
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        l.onTableCleared(table);
    }
}
