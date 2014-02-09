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

import java.util.LinkedHashSet;
import java.util.Set;
import net.daboross.outputtablesclient.Output;
import net.daboross.outputtablesclient.api.OutputListener;

public class ListenerForward implements OutputListener {

    private final Set<OutputListener> listeners = new LinkedHashSet<>();

    public void addListener(OutputListener listener) {
        listeners.add(listener);
    }

    public void removeListener(OutputListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onTableCreate(String tableKey, String tableName) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableCreate(tableKey, tableName);
            } catch (Throwable t) {
                Output.log("Error onTableCreate", t);
            }
        }
    }

    @Override
    public void onTableStale(String tableKey) {
        for (OutputListener listener : listeners) {
            try {
                listener.onTableStale(tableKey);
            } catch (Throwable t) {
                Output.log("Error onTableStale", t);
            }
        }
    }

    @Override
    public void onKeyCreate(String tableKey, String keyName, String keyValue) {
        for (OutputListener listener : listeners) {
            try {
                listener.onKeyCreate(tableKey, keyName, keyValue);
            } catch (Throwable t) {
                Output.log("Error onKeyCreate", t);
            }
        }
    }

    @Override
    public void onKeyUpdate(String tableKey, String keyName, String keyValue) {
        for (OutputListener listener : listeners) {
            try {
                listener.onKeyUpdate(tableKey, keyName, keyValue);
            } catch (Throwable t) {
                Output.log("Error onKeyUpdate", t);
            }
        }
    }

    @Override
    public void onKeyDelete(String tableKey, String keyName) {
        for (OutputListener listener : listeners) {
            try {
                listener.onKeyDelete(tableKey, keyName);
            } catch (Throwable t) {
                Output.log("Error onKeyDelete", t);
            }
        }
    }
}
