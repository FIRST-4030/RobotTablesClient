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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import net.daboross.outputtablesclient.api.InputListener;
import net.daboross.outputtablesclient.output.Output;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;

public class InputTablesMain implements DotNetTable.DotNetTableEvents {

    private static final String FEEDBACK_KEY = "_DRIVER_FEEDBACK_KEY";
    private final InputListenerForward l = new InputListenerForward();
    private final Map<String, Map<String, String>> values = new HashMap<>();
    private final DotNetTable defaultSettingsTable;
    private final DotNetTable settingsTable;
    private int currentFeedback;

    public InputTablesMain() {
        defaultSettingsTable = DotNetTables.subscribe("robot-input-default");
        settingsTable = DotNetTables.publish("robot-input");
    }

    public void subscribe() {
        settingsTable.onChange(this);
    }

    public void addListener(InputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(InputListener listener) {
        l.removeListener(listener);
    }

    public void updateKey(String key, String newValue) {
        currentFeedback++;
        settingsTable.setValue(FEEDBACK_KEY, currentFeedback);

    }

    @Override
    public synchronized void changed(DotNetTable dnt) {
        if (!dnt.name().equals("robot-input-default")) {
            Output.logI("Warning, non-input table '%s' ignored", dnt.name());
            return;
        }
        Output.logI("Table changed");
        String tableKey = dnt.name();
        Map<String, String> valueTable = values.get(tableKey);
        for (Enumeration<String> e = dnt.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            if (key.startsWith("_")) {
                continue;
            }
            String value = dnt.getValue(key);
            if (!valueTable.containsKey(key)) {
                valueTable.put(key, value);
                l.onCreateDefaultKey(key, value);
            } else if (!valueTable.get(key).equals(key)) {
                valueTable.put(key, value);
                l.onUpdateDefaultKey(key, value);
            }
        }
        for (String key : new ArrayList<>(valueTable.keySet())) {
            if (!dnt.exists(key)) {
                valueTable.remove(key);
                l.onDeleteDefaultkey(key);
            }
        }
    }

    @Override
    public synchronized void stale(DotNetTable dnt) {
        l.onStale();
    }
}
