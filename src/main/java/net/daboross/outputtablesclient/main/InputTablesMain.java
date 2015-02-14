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

import java.util.HashMap;
import java.util.Map;
import net.daboross.outputtablesclient.listener.InputListener;
import net.daboross.outputtablesclient.listener.InputListenerForward;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.persist.PersistStorage;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;
import org.ingrahamrobotics.robottables.api.listeners.TableUpdateListener;
import org.json.JSONObject;

public class InputTablesMain implements TableUpdateListener, ClientUpdateListener {

    private static final String DEFAULT_TABLE = "robot-input-default";
    private static final String SETTING_TABLE = "robot-input";
    private final InputListenerForward l = new InputListenerForward();
    private final Map<String, String> values = new HashMap<>();
    private RobotTable defaultSettingsTable;
    private final RobotTable settingsTable;
    private boolean stale = true;
    private long currentFeedback;
    private final PersistStorage storage;
    private final JSONObject storageObj;

    public InputTablesMain(Application application) {
        defaultSettingsTable = application.getTables().subscribeToTable(DEFAULT_TABLE);
        settingsTable = application.getTables().publishTable(SETTING_TABLE);
        storage = application.getPersist();
        JSONObject tempObject = storage.obj().optJSONObject("input-save");
        if (tempObject == null) {
            tempObject = new JSONObject();
            storage.obj().put("input-save", tempObject);
        }
        storageObj = tempObject;
    }

    public void subscribe() {
        for (String key : storageObj.keySet()) {
            String value = storageObj.getString(key);
            settingsTable.set(key, value);
            values.put(key, value);
            l.onCreateDefaultKey(key, value);
        }
        defaultSettingsTable.addUpdateListener(this, true);
//        settingsTable.setInterval(1000);
    }

    public void addListener(InputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(InputListener listener) {
        l.removeListener(listener);
    }

    public void updateKey(String key, String newValue) {
        settingsTable.set(key, newValue);
        storageObj.put(key, newValue);
        storage.save();
    }

    public PersistStorage getStorage() {
        return storage;
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        if (table != defaultSettingsTable) {
            Output.iLog("Non-input table '%s' ignored", table.getName());
            return;
        }
        Output.iLog("Table updated");
        if (action == UpdateAction.DELETE) {
            values.remove(key);
            l.onDeleteKey(key);
        } else if (action == UpdateAction.NEW) {
            values.put(key, value);
            l.onCreateDefaultKey(key, value);
        } else if (action == UpdateAction.UPDATE) {
            values.put(key, value);
            l.onUpdateDefaultKey(key, value);
        }
    }

    @Override
    public void onUpdateAdmin(final RobotTable table, final String key, final String value, final UpdateAction action) {

    }

    @Override
    public void onTableCleared(final RobotTable table) {
        for (String key : values.keySet()) {
            l.onDeleteKey(key);
        }
        values.clear();
    }

    @Override
    public void onTableChangeType(final RobotTable table, final TableType oldType, final TableType newType) {
        // TODO: Handle other clients which also want to send settings here
    }

    @Override
    public void onTableStaleChange(final RobotTable table, final boolean nowStale) {
    }

    @Override
    public void onAllSubscribersStaleChange(final RobotTable table, boolean nowStale) {
        // TODO: Complex hover-over interface which shows all tables which are and aren't up to date
        if (nowStale) {
            l.onStale();
        } else {
            l.onNotStale();
        }
    }

    @Override
    public void onNewTable(final RobotTable table) {
    }
}
