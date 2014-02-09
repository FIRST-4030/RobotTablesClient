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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.util.DynamicGridBagConstraints;

public class OutputTablesGUI {

    /**
     * Current constraints, one variable so as to not keep creating new ones. No
     * guarantees as to what settings are set and not set.
     */
    private final DynamicGridBagConstraints constraints = new DynamicGridBagConstraints();
    final JFrame rootFrame;
    final JPanel toggleButtonPanel;
    final JTextArea loggingTextArea;
    final JPanel dataOutputRootPanel;
    final Map<String, JPanel> tableKeyToDataPanel;

    public OutputTablesGUI() {
        // rootFrame
        rootFrame = new JFrame();
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setExtendedState(rootFrame.getExtendedState() | JFrame.MAXIMIZED_HORIZ);
        rootFrame.setTitle("Robot Output " + OutputTablesGUI.class.getPackage().getImplementationVersion());


        // toggleButtonPanel
        toggleButtonPanel = new JPanel();
        toggleButtonPanel.setLayout(new GridBagLayout());
        toggleButtonPanel.setBackground(new Color(0, 0, 0));
        rootFrame.add(toggleButtonPanel, constraints.weightx(0).weighty(0)
                .anchor(GridBagConstraints.NORTHWEST).gridx(0).gridy(0));


        // loggingTextArea
        loggingTextArea = new JTextArea(20, 25);
        ((DefaultCaret) loggingTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        loggingTextArea.setBorder(null); // TODO: Set TitledBorder with space on the inside.
        ToggleButtonResponder.createToggleButton("Log", new JScrollPane(loggingTextArea), rootFrame,
                new DynamicGridBagConstraints().weightx(1).weighty(1).anchor(GridBagConstraints.EAST).gridx(1).gridy(0));


        // dataOutputRootPanel
        dataOutputRootPanel = new JPanel();
        rootFrame.add(dataOutputRootPanel, constraints.weightx(2).weighty(1)
                .anchor(GridBagConstraints.EAST).gridx(2).gridy(0));


        // tableKeyToDataPanel
        tableKeyToDataPanel = new HashMap<>();


    }
}
