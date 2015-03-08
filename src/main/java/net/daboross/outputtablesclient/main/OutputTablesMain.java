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
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;
import org.ingrahamrobotics.robottables.api.listeners.TableUpdateListener;

public class OutputTablesMain implements ClientUpdateListener, TableUpdateListener {

    private final long CHECK_NETWORK_EVERY = 5 * 60 * 1000;
    private final OutputListenerForward l = new OutputListenerForward();
    private final RobotTablesClient client;
    private final RobotTable nameTable;
    private final RecheckNetworkRunnable recheckNetworkInterfacesRunnable;

    public OutputTablesMain(Application application) {
        client = application.getTables();
        nameTable = client.subscribeToTable("__output_display_names");
        recheckNetworkInterfacesRunnable = new RecheckNetworkRunnable();
    }

    public void subscribe() {
        client.addClientListener(this, true);
        nameTable.addUpdateListener(this, true);
        recheckNetworkInterfacesRunnable.delay();
    }

    public void addListener(OutputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(OutputListener listener) {
        l.removeListener(listener);
    }

    @Override
    public void onTableChangeType(final RobotTable table, final TableType oldType, final TableType newType) {
        // TODO: Something here, perhaps note when someone else has taken over dealing with input?
    }

    @Override
    public void onTableStaleChange(final RobotTable table, final boolean nowStale) {
        if (!nowStale) {
            recheckNetworkInterfacesRunnable.delay();
        }
        l.onTableStaleChange(table.getName(), nowStale);
    }

    @Override
    public void onAllSubscribersStaleChange(final RobotTable table, boolean nowStale) {
    }

    @Override
    public void onNewTable(final RobotTable table) {
//        Output.oLog("Table created '%s'", table.getName());
        table.addUpdateListener(this, true);
        l.onTableCreate(table);
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
//        Output.oLog("Table updated '%s'", table.getName());
        if (table.getName().equals("__output_display_names")) {
            if (action == UpdateAction.NEW || action == UpdateAction.UPDATE) {
                RobotTable updatedTable = client.getTable(key);
                if (updatedTable == null) {
                    client.subscribeToTable(key).addUpdateListener(this, true);
                } else {
                    l.onTableDisplayNameChange(updatedTable, value);
                }
            }
            // DELETE is ignored here, as it is never used and we wouldn't know what to do if it was.
        }

        l.onUpdate(table, key, value, action);
    }

    @Override
    public void onUpdateAdmin(final RobotTable table, final String key, final String value, final UpdateAction action) {
        // TODO: Do we want to display admin values at all?
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        l.onTableCleared(table);
    }

    public RobotTable getNameTable() {
        return nameTable;
    }

    public class RecheckNetworkRunnable implements Runnable {

        private final Object updateLock = new Object();
        private long timeoutTime;

        public RecheckNetworkRunnable() {
            new Thread(this).start();
        }

        public void delay() {
            synchronized (updateLock) {
                this.timeoutTime = System.currentTimeMillis() + CHECK_NETWORK_EVERY;
            }
        }

        public void run() {
            while (true) {
                long currentUpdateTime;
                synchronized (updateLock) {
                    currentUpdateTime = timeoutTime;
                }
                while (true) {
                    long waitTime = currentUpdateTime - System.currentTimeMillis();
                    if (waitTime > 0) {
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (updateLock) {
                        if (timeoutTime > currentUpdateTime) {
                            // If the updateTime has changed since we started, we should sleep again.
                            currentUpdateTime = timeoutTime;
                        } else {
                            // Otherwise, let's run it!
                            break;
                        }
                    }
                }
                if (System.currentTimeMillis() > nameTable.getLastUpdateTime() + Double.parseDouble(nameTable.getAdmin("UPDATE_INTERVAL")) * 4) {
                    client.recheckNetworkInterfaces(true);
                } else if (nameTable.isStale()) {
                    client.recheckNetworkInterfaces(false);
                }
                this.timeoutTime = System.currentTimeMillis() + CHECK_NETWORK_EVERY;
            }
        }
    }
}
