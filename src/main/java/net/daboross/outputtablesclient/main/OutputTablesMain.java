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
import java.util.HashMap;
import java.util.Map;
import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.listener.OutputListenerForward;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class OutputTablesMain implements DotNetTable.DotNetTableEvents {

    private static final String OUTPUT_TABLE = "output-tables";
    private final OutputListenerForward l = new OutputListenerForward();
    private final Map<String, Map<String, String>> values = new HashMap<>();
    private final DotNetTable nameTable;
    private final HashMap<String, String> nameMap = new HashMap<>();

    public OutputTablesMain() {
        nameTable = DotNetTables.subscribe(OUTPUT_TABLE);
    }

    public void subscribe() {
        nameTable.onChange(this);
    }

    public synchronized String getTableName(String tableKey) {
        String value = nameTable.getValue(tableKey);
        return value == null ? nameMap.get(tableKey) : value;
    }

    public synchronized Map<String, String> getTable(String tableKey) {
        return Collections.unmodifiableMap(values.get(tableKey));
    }

    public void addListener(OutputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(OutputListener listener) {
        l.removeListener(listener);
    }

    public synchronized void manualUpdate(String tableKey, String key, String value) {
        Map<String, String> valueTable = values.get(tableKey);
        if (valueTable == null) {
            valueTable = new HashMap<>();
            values.put(tableKey, valueTable);
            String name = nameTable.getValue(tableKey);
            if (name == null) {
                name = tableKey;
                nameMap.put(tableKey, name);
            }
            l.onTableCreate(tableKey, name);
            DotNetTable dotNetTable = DotNetTables.subscribe(tableKey);
            dotNetTable.onChange(this);
        }
        if (!valueTable.containsKey(key)) {
            valueTable.put(key, value);
            l.onKeyCreate(tableKey, key, value);
        } else if (!valueTable.get(key).equals(value)) {
            valueTable.put(key, value);
            l.onKeyUpdate(tableKey, key, value);
        }
    }

    @Override
    public synchronized void changed(DotNetTable dnt) {
        // Disabled until we can fix this
//        Output.oLog("Table changed '%s'", dnt.name());
//        if (dnt.name().equals(OUTPUT_TABLE)) {
//            for (Enumeration<String> e = dnt.keys(); e.hasMoreElements(); ) {
//                String tableKey = e.nextElement();
//                if (tableKey.startsWith("_")) {
//                    continue;
//                }
//                Map<String, String> valueTable = values.get(tableKey);
//                if (valueTable == null) {
//                    valueTable = new HashMap<>();
//                    values.put(tableKey, valueTable);
//                    l.onTableCreate(tableKey, dnt.getValue(tableKey));
//                    DotNetTable dotNetTable = DotNetTables.subscribe(tableKey);
//                    dotNetTable.onChange(this);
//                }
//            }
//        } else {
//            String tableKey = dnt.name();
//            Map<String, String> valueTable = values.get(tableKey);
//            for (Enumeration<String> e = dnt.keys(); e.hasMoreElements(); ) {
//                String key = e.nextElement();
//                if (key.startsWith("_")) {
//                    continue;
//                }
//                String value = dnt.getValue(key);
//                if (!valueTable.containsKey(key)) {
//                    valueTable.put(key, value);
//                    l.onKeyCreate(tableKey, key, value);
//                } else if (!valueTable.get(key).equals(value)) {
//                    valueTable.put(key, value);
//                    l.onKeyUpdate(tableKey, key, value);
//                }
//            }
//            for (String key : new ArrayList<>(valueTable.keySet())) {
//                if (!dnt.exists(key)) {
//                    valueTable.remove(key);
//                    l.onKeyDelete(tableKey, key);
//                }
//            }
//        }
    }

    @Override
    public synchronized void stale(DotNetTable dnt) {
        if (!dnt.name().equals(OUTPUT_TABLE)) {
            l.onTableStale(dnt.name());
        }
    }
}
