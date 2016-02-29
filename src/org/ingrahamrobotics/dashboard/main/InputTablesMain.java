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
package org.ingrahamrobotics.dashboard.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ingrahamrobotics.dashboard.listener.InputListener;
import org.ingrahamrobotics.dashboard.listener.InputListenerForward;
import org.ingrahamrobotics.dashboard.output.Output;
import org.ingrahamrobotics.dashboard.persist.PersistStorage;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;
import org.ingrahamrobotics.robottables.api.listeners.TableUpdateListener;
import org.ingrahamrobotics.robottables.util.UpdateableDelayedRunnable;

public class InputTablesMain implements TableUpdateListener, ClientUpdateListener {

    private static final String DEFAULT_TABLE = "robot-input-default";
    private static final String SETTING_TABLE = "robot-input";
    private final UpdateableDelayedRunnable valueSaveRunnable;
    private final InputListenerForward l = new InputListenerForward();
    private final Map<String, String> values = new HashMap<>();
    private final Set<String> valuesNotDefault = new HashSet<>();
    private RobotTable defaultSettingsTable;
    private final RobotTable settingsTable;
    private final PersistStorage storage;

    public InputTablesMain(Application application) {
        defaultSettingsTable = application.getTables().subscribeToTable(DEFAULT_TABLE);
        settingsTable = application.getTables().publishTable(SETTING_TABLE);
        storage = application.getPersist();
        Object storedValues = storage.getStorageObject().get("input-save");
        if (storedValues instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) storedValues).entrySet()) {
                values.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        storage.getStorageObject().put("input-save", values);
        Object storedNotDefault = storage.getStorageObject().get("input-not-default");
        if (storedNotDefault instanceof Iterable) {
            for (Object o : (Iterable) storedNotDefault) {
                valuesNotDefault.add(String.valueOf(o));
            }
        }
        storage.getStorageObject().put("input-not-default", valuesNotDefault);

        valueSaveRunnable = new UpdateableDelayedRunnable(storage::save);
    }
    
    public Set<String> defaultKeys() {
    	if (defaultSettingsTable == null) {
    		return null;
    	}
    	return defaultSettingsTable.getKeySet();    		
    }

    public void updateDefault(String key, String value) {
        if (valuesNotDefault.contains(key)) {
            return;
        }
        values.put(key, value);
        valueSaveRunnable.delayUntil(System.currentTimeMillis() + 1000);
    }

    public void updateKey(String key, String value) {
        settingsTable.set(key, value);
        values.put(key, value);
        valuesNotDefault.add(key);
        valueSaveRunnable.delayUntil(System.currentTimeMillis() + 1000);
    }

    public void subscribe() {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            settingsTable.set(entry.getKey(), entry.getValue());
            l.onCreateDefaultKey(entry.getKey(), entry.getValue());
        }
        defaultSettingsTable.addUpdateListener(this, true);
    }

    public void addListener(InputListener listener) {
        l.addListener(listener);
    }

    public void removeListener(InputListener listener) {
        l.removeListener(listener);
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
            if (!values.containsKey(key)) {
                l.onCreateDefaultKey(key, value);
            } else {
                l.onUpdateDefaultKey(key, value);
            }
            updateDefault(key, value);
        } else if (action == UpdateAction.UPDATE) {
            updateDefault(key, value);
            l.onUpdateDefaultKey(key, value);
        }
    }

    @Override
    public void onUpdateAdmin(final RobotTable table, final String key, final String value, final UpdateAction action) {
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        values.keySet().forEach(l::onDeleteKey);
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
