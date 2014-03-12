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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.daboross.outputtablesclient.listener.InputListener;
import net.daboross.outputtablesclient.listener.InputListenerForward;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.persist.PersistStorage;
import org.ingrahamrobotics.dotnettables.DotNetTable;
import org.ingrahamrobotics.dotnettables.DotNetTables;
import org.json.JSONObject;

public class InputTablesMain implements DotNetTable.DotNetTableEvents {

    private static final String FEEDBACK_KEY = "_DRIVER_FEEDBACK_KEY";
    private static final int FEEDBACK_THRESHOLD = 2;
    private static final String DEFAULT_TABLE = "robot-input-default";
    private static final String SETTING_TABLE = "robot-input";
    private final InputListenerForward l = new InputListenerForward();
    private final Map<String, String> values = new HashMap<>();
    private final DotNetTable defaultSettingsTable;
    private final DotNetTable settingsTable;
    private final Timer timer = new Timer();
    private boolean stale = true;
    private long currentFeedback;
    private final PersistStorage storage;
    private final JSONObject storageObj;

    public InputTablesMain() {
        defaultSettingsTable = DotNetTables.subscribe(DEFAULT_TABLE);
        settingsTable = DotNetTables.publish(SETTING_TABLE);
        storage = new PersistStorage();
        JSONObject tempObject = storage.obj().optJSONObject("input-save");
        if (tempObject == null) {
            tempObject = new JSONObject();
            storage.obj().put("input-save", tempObject);
        }
        storageObj = tempObject;
    }

    public void subscribe() {
        for (String key : (Set<String>) storageObj.keySet()) {
            String value = storageObj.getString(key);
            settingsTable.setValue(key, value);
            values.put(key, value);
            l.onCreateDefaultKey(key, value);
        }
        defaultSettingsTable.onChange(this);
        defaultSettingsTable.onStale(this);
        settingsTable.setInterval(1000);
    }

    public void addListener(InputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(InputListener listener) {
        l.removeListener(listener);
    }

    public void updateKey(String key, String newValue) {
        settingsTable.setValue(key, newValue);
        storageObj.put(key, newValue);
        storage.save();
        sendSettings();
    }

    @Override
    public synchronized void changed(DotNetTable dnt) {
        if (!dnt.name().equals(DEFAULT_TABLE)) {
            Output.iLog("Non-input table '%s' ignored", dnt.name());
            return;
        }
        updateStale();
        Output.iLog("Table changed");
        for (Enumeration<String> e = dnt.keys(); e.hasMoreElements();) {
            String key = e.nextElement();
            if (key.startsWith("_")) {
                continue;
            }
            String value = dnt.getValue(key);
            if (!values.containsKey(key)) {
                values.put(key, value);
                l.onCreateDefaultKey(key, value);
            } else if (!values.get(key).equals(value)) {
                values.put(key, value);
                l.onUpdateDefaultKey(key, value);
            }
        }
        for (String key : new ArrayList<>(values.keySet())) {
            if (!dnt.exists(key)) {
                values.remove(key);
                l.onDeleteKey(key);
            }
        }
    }

    @Override
    public synchronized void stale(DotNetTable dnt) {
        updateStale();
    }

    private void updateStale() {
        // Check the feedback key, if it exists
        boolean feedbackStale = true;
        if (defaultSettingsTable.exists(FEEDBACK_KEY)) {
            double feedback = -1;
            try {
                feedback = defaultSettingsTable.getDouble(FEEDBACK_KEY);
            } catch (NumberFormatException ex) {
                Output.iLog("Non-number feedback '%s'.", defaultSettingsTable.getValue(FEEDBACK_KEY));
            }
            if (currentFeedback < feedback + FEEDBACK_THRESHOLD) {
                feedbackStale = false;
            }
        }
        
        // Determine the new master stale state
        boolean masterStale = true;
        if (!defaultSettingsTable.isStale() && !feedbackStale) {
            masterStale = false;
        }
        
        // Update the UI if needed
        if (stale != masterStale) {
            if (masterStale) {
                l.onStale();
            } else {
                l.onNotStale();
            }
        }
        
        // Always save the new state
        stale = masterStale;
    }

    private void sendSettings() {
        currentFeedback++;
        settingsTable.setValue(FEEDBACK_KEY, currentFeedback);
        settingsTable.send();
    }

    public PersistStorage getStorage() {
        return storage;
    }
}
