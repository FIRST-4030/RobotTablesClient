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
package org.ingrahamrobotics.dashboard.listener;

import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;

public interface OutputListener {

    public void onTableCreate(RobotTable table);

    public void onTableDisplayNameChange(RobotTable table, String newDisplayName);

    public void onTableStaleChange(String tableKey, boolean nowStale);

    public void onUpdate(RobotTable table, String key, String value, UpdateAction action);

    public void onTableCleared(RobotTable table);
}
