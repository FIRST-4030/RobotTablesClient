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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

public class ClientFrameManager implements StaticLog.StaticLogger {

    private final GridBagConstraints constraints = new GridBagConstraints();
    private final JTextArea loggingText = new JTextArea(30, 40);
    private final ToggleAreaPanel toggleArea = new ToggleAreaPanel();
    private final JFrame frame = new JFrame();

    public ClientFrameManager() {
        toggleArea.setToggleOn(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ((DefaultCaret) loggingText.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setTitle("Robot Output");
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        frame.add(toggleArea, constraints);
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx++;
        addCollapsibleComponent("Log", new JScrollPane(loggingText));
        constraints.gridx++;
    }

    public void show() {
        frame.setVisible(true);
    }

    public final void addCollapsibleComponent(final String labelText, JComponent component) {
        constraints.gridy++;
        toggleArea.addToToggle(labelText, component, constraints.clone());
        frame.add(component, constraints);
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }

    public void log(String msg, Object... args) {
        String message = String.format("[%s] %s\n", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(msg, args));
        loggingText.append(message);
        System.out.print(message);
    }
}
