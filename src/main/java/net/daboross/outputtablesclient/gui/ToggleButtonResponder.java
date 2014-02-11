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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JToggleButton;

public class ToggleButtonResponder implements ItemListener {

    private final JToggleButton button;
    private final Component toToggle;
    private final Container toggleOn;
    private final Object toggleConstraints;
    private boolean toggled;

    public static JToggleButton createToggleButton(String name, Component toToggle, Container container, Object toggleConstraints) {
        JToggleButton button = new JToggleButton();
        button.setText(name);
        button.addItemListener(new ToggleButtonResponder(button, toToggle, container, toggleConstraints));
        button.setSelected(true);
        return button;
    }

    public ToggleButtonResponder(JToggleButton button, Component toToggle, Container toggleOn, Object toggleConstraints) {
        this.button = button;
        this.toToggle = toToggle;
        this.toggleOn = toggleOn;
        this.toggleConstraints = toggleConstraints;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (button.isSelected() && !toggled) {
            toggleOn.add(toToggle, toggleConstraints);
        } else if (!button.isSelected() && toggled) {
            toggleOn.remove(toToggle);
        }
        toggleOn.revalidate();
        toggleOn.repaint();
        toggled = !toggled;
    }
}
