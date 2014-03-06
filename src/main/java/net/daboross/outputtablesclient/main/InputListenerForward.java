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

import java.util.LinkedHashSet;
import java.util.Set;
import net.daboross.outputtablesclient.api.InputListener;
import net.daboross.outputtablesclient.output.Output;

public class InputListenerForward implements InputListener {

    private final Set<InputListener> listeners = new LinkedHashSet<>();

    public void addListener(InputListener listener) {
        listeners.add(listener);
    }

    public void removeListener(InputListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onUpdate() {
        for (InputListener listener : listeners) {
            try {
                listener.onUpdate();
            } catch (Throwable t) {
                Output.logI("Error onUpdate", t);
            }
        }
    }

    @Override
    public void onStale() {
        for (InputListener listener : listeners) {
            try {
                listener.onStale();
            } catch (Throwable t) {
                Output.logI("Error onStale", t);
            }
        }
    }

    @Override
    public void onCreateDefaultKey(String keyName, String keyValue) {
        for (InputListener listener : listeners) {
            try {
                listener.onCreateDefaultKey(keyName, keyValue);
            } catch (Throwable t) {
                Output.logI("Error onCreateDefaultKey", t);
            }
        }
    }

    @Override
    public void onUpdateDefaultKey(String keyName, String keyValue) {
        for (InputListener listener : listeners) {
            try {
                listener.onUpdateDefaultKey(keyName, keyValue);
            } catch (Throwable t) {
                Output.logI("Error onUpdateDefaultKey", t);
            }
        }
    }

    @Override
    public void onDeleteDefaultkey(String keyName) {
        for (InputListener listener : listeners) {
            try {
                listener.onDeleteDefaultkey(keyName);
            } catch (Throwable t) {
                Output.logI("Error onDeleteDefaultkey", t);
            }
        }
    }
}
