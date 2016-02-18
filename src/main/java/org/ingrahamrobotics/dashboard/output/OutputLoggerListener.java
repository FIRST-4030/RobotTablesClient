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
package org.ingrahamrobotics.dashboard.output;

import org.ingrahamrobotics.dashboard.listener.OutputListener;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;

public class OutputLoggerListener implements OutputListener {

    public OutputLoggerListener() {
    }

    @Override
    public void onTableCreate(RobotTable table) {
        Output.oLog(" created table: %s", table.getName());
    }

    @Override
    public void onTableDisplayNameChange(final RobotTable table, final String newDisplayName) {
    }

    @Override
    public void onTableStaleChange(String tableKey, boolean nowStale) {
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        if (action == UpdateAction.NEW) {
            Output.oLog("[%s][%s*] %s", table.getName(), key, value);
        } else if (action == UpdateAction.UPDATE) {
            Output.oLog("[%s][%s] %s", table.getName(), key, value);
        } else if (action == UpdateAction.DELETE) {
            Output.oLog("[%s] delete: %s)", table.getName(), key);
        }
    }

    @Override
    public void onTableCleared(final RobotTable table) {
    }
}
