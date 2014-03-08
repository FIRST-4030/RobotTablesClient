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
import net.daboross.outputtablesclient.listener.InputListener;

public class SwingInputForward implements InputListener {

    private final InputListener innerListener;

    public SwingInputForward(InputListener innerListener) {
        this.innerListener = innerListener;
    }

    @Override
    public void onNotStale() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onNotStale();
            }
        });
    }

    @Override
    public void onStale() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onStale();
            }
        });
    }

    @Override
    public void onCreateDefaultKey(final String keyName, final String keyValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onCreateDefaultKey(keyName, keyValue);
            }
        });
    }

    @Override
    public void onUpdateDefaultKey(final String keyName, final String keyValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onUpdateDefaultKey(keyName, keyValue);
            }
        });
    }

    @Override
    public void onDeleteKey(final String keyName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                innerListener.onDeleteKey(keyName);
            }
        });
    }
}
