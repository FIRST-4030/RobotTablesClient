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
package net.daboross.outputtablesclient.gui;

import java.awt.Robot;
import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.listener.OutputListener;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;

public class SwingOutputForward implements OutputListener {

    private final OutputListener innerListener;

    public SwingOutputForward(OutputListener innerListener) {
        this.innerListener = innerListener;
    }

    @Override
    public void onTableCreate(final RobotTable table) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableCleared(table);
            }
        });
    }

    @Override
    public void onTableDisplayNameChange(final RobotTable table, final String newDisplayName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableDisplayNameChange(table, newDisplayName);
            }
        });
    }

    @Override
    public void onTableStaleChange(final String tableKey, final boolean nowStale) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableStaleChange(tableKey, nowStale);
            }
        });
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onUpdate(table, key, value, action);
            }
        });
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableCleared(table);
            }
        });
    }
}
