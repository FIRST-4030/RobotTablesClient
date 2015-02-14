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
package net.daboross.outputtablesclient.listener;

import java.util.LinkedHashSet;
import java.util.Set;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;

public class OutputListenerForward implements OutputListener {

    private final Set<OutputListener> listeners = new LinkedHashSet<>();

    public void addListener(OutputListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OutputListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onTableCreate(final RobotTable table) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableCreate(table);
            } catch (Throwable t) {
                Output.oLog("Error onTableCreate", t);
            }
        }
    }

    @Override
    public void onTableDisplayNameChange(final RobotTable table, final String newDisplayName) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableDisplayNameChange(table, newDisplayName);
            } catch (Throwable t) {
                Output.oLog("Error onTableDisplayNameChange", t);
            }
        }

    }

    @Override
    public void onTableStaleChange(String tableKey, boolean staleNow) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableStaleChange(tableKey, staleNow);
            } catch (Throwable t) {
                Output.oLog("Error onTableStale", t);
            }
        }
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        for (OutputListener listener : listeners) {
            try {
                listener.onUpdate(table, key, value, action);
            } catch (Throwable t) {
                Output.oLog("Error onCreateDefaultKey", t);
            }
        }
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableCleared(table);
            } catch (Throwable t) {
                Output.oLog("Error onCreateDefaultKey", t);
            }
        }
    }
}
