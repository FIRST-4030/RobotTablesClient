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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.ingrahamrobotics.dotnettables.DotNetTable;

public class TableOutputPanel extends JPanel implements DotNetTable.DotNetTableEvents {

    private final Map<String, JLabel> labels = new HashMap<>();

    public TableOutputPanel(final DotNetTable table) {
        changed(table);
        table.onChange(this);
    }

    private void set(String key, String value) {
        JLabel label = labels.get(key.toLowerCase());
        if (label == null) {
            label = new JLabel(value);
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            panel.add(new JLabel(key), constraints);
            constraints.gridy++;
            panel.add(label, constraints);
            panel.setBorder(new LineBorder(Color.BLACK));
            add(panel);
        } else {
            label.setText(value);
        }
    }

    @Override
    public void changed(final DotNetTable table) {
        for (Enumeration e = table.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String value = table.getValue(key);
            set(key, value);
        }
    }

    @Override
    public void stale(final DotNetTable table) {
    }
}
