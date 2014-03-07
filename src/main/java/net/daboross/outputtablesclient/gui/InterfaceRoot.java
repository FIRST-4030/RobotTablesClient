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
package net.daboross.outputtablesclient.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class InterfaceRoot {

    final JFrame rootFrame;
    final JTabbedPane tabbedPane;
    final JTextArea loggingTextArea;

    public InterfaceRoot() {
        // rootFrame
        rootFrame = new JFrame();
        rootFrame.setMinimumSize(new Dimension(640, 480));
        rootFrame.setPreferredSize(new Dimension(640, 480));
        rootFrame.setLayout(new BorderLayout());
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setExtendedState(rootFrame.getExtendedState() | JFrame.MAXIMIZED_HORIZ);
        rootFrame.setTitle("Robot Output " + OutputInterface.class.getPackage().getImplementationVersion());

        // tabbedPane
        tabbedPane = new JTabbedPane();
        rootFrame.add(tabbedPane, BorderLayout.CENTER);

        // loggingTextArea
        loggingTextArea = new JTextArea();
        ((DefaultCaret) loggingTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane loggingPane = new JScrollPane(loggingTextArea);
        tabbedPane.addTab("Log", loggingPane);
    }

    public void show() {
        rootFrame.setVisible(true);
    }
}
