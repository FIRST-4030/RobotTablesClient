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
package net.daboross.outputtablesclient.old;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ToggleAreaPanel extends JPanel {

    private GridBagConstraints constraints = new GridBagConstraints();

    public ToggleAreaPanel() {
        setLayout(new GridBagLayout());
        constraints.ipadx = 2;
        constraints.ipady = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public void addToToggle(final String name, Component toToggle, Container container, Object toggleConstraints) {
        final ToggleThing thing = new ToggleThing(toToggle, container, toggleConstraints);
        final JToggleButton button = new JToggleButton();
        button.setText(name);
        button.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (button.isSelected()) {
                    thing.ensureOn();
                } else {
                    thing.ensureOff();
                }
            }
        });
        button.setSelected(true);
        constraints.gridy++;
        add(button, constraints);
    }

    public class ToggleThing {

        private final Component toToggle;
        private final Container toggleOn;
        private final Object toggleConstraints;
        private boolean toggled;

        public ToggleThing(Component toToggle, Container toggleOn, Object toggleConstraints) {
            this.toToggle = toToggle;
            this.toggleOn = toggleOn;
            this.toggleConstraints = toggleConstraints;
        }

        public void toggle() {
            if (toggled) {
                toggleOn.remove(toToggle);
            } else {
                toggleOn.add(toToggle, toggleConstraints);
            }
            update();
            toggled = !toggled;
        }

        public void ensureOn() {
            if (!toggled) {
                toggleOn.add(toToggle, toggleConstraints);
                update();
                toggled = !toggled;
            }
        }

        public void ensureOff() {
            if (toggled) {
                toggleOn.remove(toToggle);
                update();
                toggled = !toggled;
            }
        }

        private void update() {
            Container parent = getParent();
            parent.revalidate();
            parent.repaint();
            toggleOn.revalidate();
            toggleOn.repaint();
        }
    }
}
