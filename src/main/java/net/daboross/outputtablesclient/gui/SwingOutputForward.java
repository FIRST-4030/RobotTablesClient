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

import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.listener.OutputListener;

public class SwingOutputForward implements OutputListener {

    private final OutputListener innerListener;

    public SwingOutputForward(OutputListener innerListener) {
        this.innerListener = innerListener;
    }

    @Override
    public void onTableCreate(final String tableKey, final String tableName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableCreate(tableKey, tableName);
            }
        });
    }

    @Override
    public void onTableStale(final String tableKey) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onTableStale(tableKey);
            }
        });
    }

    @Override
    public void onKeyCreate(final String tableKey, final String keyName, final String keyValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onKeyCreate(tableKey, keyName, keyValue);
            }
        });
    }

    @Override
    public void onKeyUpdate(final String tableKey, final String keyName, final String keyValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onKeyUpdate(tableKey, keyName, keyValue);
            }
        });
    }

    @Override
    public void onKeyDelete(final String tableKey, final String keyName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onKeyDelete(tableKey, keyName);
            }
        });
    }
}
