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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import net.daboross.outputtablesclient.api.OutputListener;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class OutputTableMain implements DotNetTable.DotNetTableEvents {

    private final ListenerForward l = new ListenerForward();
    private final Map<String, Map<String, String>> values = new HashMap<>();

    public Map<String, String> getTable(String tableKey) {
        return Collections.unmodifiableMap(values.get(tableKey));
    }

    public void subscribe() {
        DotNetTable table = DotNetTables.subscribe("output-tables");
        table.onChange(this);
    }

    public void addListener(OutputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(OutputListener listener) {
        l.removeListener(listener);
    }

    @Override
    public void changed(DotNetTable dnt) {
        if (dnt.name().equals("output-tables")) {
            for (Enumeration<String> e = dnt.keys(); e.hasMoreElements();) {
                String tableKey = e.nextElement();
                Map<String, String> valueTable = values.get(tableKey);
                if (valueTable == null) {
                    valueTable = new HashMap<>();
                    values.put(tableKey, valueTable);
                    l.onTableCreate(tableKey, dnt.getValue(tableKey));
                    DotNetTable dotNetTable = DotNetTables.subscribe(tableKey);
                    dotNetTable.onChange(this);
                }
            }
        } else {
            String tableKey = dnt.name();
            Map<String, String> valueTable = values.get(tableKey);
            for (Enumeration<String> e = dnt.keys(); e.hasMoreElements();) {
                String key = e.nextElement();
                String value = dnt.getValue(key);
                if (!valueTable.containsKey(key)) {
                    valueTable.put(key, value);
                    l.onKeyCreate(tableKey, key, value);
                } else if (!valueTable.get(key).equals(key)) {
                    valueTable.put(key, value);
                    l.onKeyUpdate(tableKey, key, value);
                }
            }
            for (String key : valueTable.keySet()) {
                if (!dnt.exists(key)) {
                    valueTable.remove(key);
                    l.onKeyDelete(tableKey, key);
                }
            }
        }
    }

    @Override
    public void stale(DotNetTable dnt) {
        if (!dnt.name().equals("output-tables")) {
            l.onTableStale(dnt.name());
        }
    }
}
