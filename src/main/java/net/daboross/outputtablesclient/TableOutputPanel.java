/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.outputtablesclient;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import org.ingrahamrobotics.dotnettables.DotNetTable;

public class TableOutputPanel extends JPanel implements DotNetTable.DotNetTableEvents {

    private static final Border border = new LineBorder(Color.BLACK);
    private final Map<String, JLabel> labels = new HashMap<>();
    private final ClientFrameManager log;
    private final String name;

    public TableOutputPanel(ClientFrameManager manager, String name) {
        this.log = manager;
        this.name = name;
    }

    public void init(DotNetTable table) {
        table.onChange(this);
    }

    private boolean set(String key, String value) {
        JLabel valueLabel = labels.get(key);
        if (valueLabel == null) {
            log.log("[%s][%s*] %s", name, key, value);
            valueLabel = new JLabel(value);
            valueLabel.setBorder(border);
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            JLabel keyLabel = new JLabel(key);
            keyLabel.setBorder(border);
            panel.add(keyLabel, constraints);
            constraints.gridy++;
            panel.add(valueLabel, constraints);
            panel.setBorder(border);
            add(panel);
            labels.put(key, valueLabel);
            return true;
        } else if (!valueLabel.getText().equals(value)) {
            log.log("[%s][%s] %s", name, key, value);
            valueLabel.setText(value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void changed(final DotNetTable table) {
        boolean changed = false;
        for (Enumeration e = table.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            if (!key.equals("_UPDATE_INTERVAL") && !key.equals("bump")) {
                String value = table.getValue(key);
                changed = changed || set(key, value);
            }
        }
        if (changed) {
            updateGraphics();
        }
    }

    @Override
    public void stale(final DotNetTable table) {
    }

    private void updateGraphics() {
        Container p = getParent();
        if (p != null) {
            p.revalidate();
            Container p2 = p.getParent();
            if (p2 != null) {
                p2.revalidate();
            }
        }
    }
}
