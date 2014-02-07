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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

public class ClientFrameManager implements StaticLog.StaticLogger {

    private final GridBagConstraints sConstraints = new GridBagConstraints();
    private final JTextArea loggingText = new JTextArea(30, 40);
    private final ToggleAreaPanel toggleArea = new ToggleAreaPanel();
    private final JFrame frame = new JFrame();
    private final JPanel subComponentPanel = new JPanel();

    public ClientFrameManager() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ((DefaultCaret) loggingText.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        frame.setTitle("Robot Output - " + ClientFrameManager.class.getPackage().getImplementationVersion());
        frame.setMinimumSize(new Dimension(320, 240));
        subComponentPanel.setLayout(new GridBagLayout());

        GridBagConstraints fConstraints = new GridBagConstraints();
        fConstraints.fill = GridBagConstraints.NONE;
        fConstraints.weightx = 1;
        fConstraints.weighty = 1;
        fConstraints.anchor = GridBagConstraints.NORTHWEST;
        fConstraints.gridx = 0;
        fConstraints.gridy = 0;
        frame.add(toggleArea, fConstraints);
        fConstraints.anchor = GridBagConstraints.EAST;
        fConstraints.gridx = 1;
        toggleArea.addToToggle("Log", new JScrollPane(loggingText), frame, fConstraints.clone());
        fConstraints.gridx = 2;
        frame.add(subComponentPanel, fConstraints);

        sConstraints.anchor = GridBagConstraints.EAST;
        sConstraints.fill = GridBagConstraints.HORIZONTAL;
        sConstraints.weightx = 1;
        sConstraints.weighty = 1;
    }

    public void show() {
        frame.setVisible(true);
    }

    public final void addSubComponent(final String labelText, JComponent component) {
        sConstraints.gridy++;
        toggleArea.addToToggle(labelText, component, subComponentPanel, sConstraints.clone());
        update();
    }

    @Override
    public void log(String msg, Object... args) {
        String message = String.format("[%s] %s\n", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(msg, args));
        loggingText.append(message);
        System.out.print(message);
    }

    private void update() {
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }
}
