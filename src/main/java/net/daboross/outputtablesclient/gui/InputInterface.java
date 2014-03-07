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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.daboross.outputtablesclient.api.InputListener;
import net.daboross.outputtablesclient.main.InputTablesMain;
import net.daboross.outputtablesclient.util.GBC;

public class InputInterface implements InputListener {

    private final InputTablesMain main;
    final GridBagConstraints toggleButtonConstraints;
    final GridBagConstraints tablePanelConstraints;
    final JPanel mainTabPanel;
    final JPanel toggleButtonPanel;
    final JPanel tableRootPanel;
    final Map<String, JPanel> keyToValuePanel;
    final Map<String, JTextField> keyToValueField;

    public InputInterface(InputTablesMain main, InterfaceRoot root) {
        this.main = main;

        // constraints
        toggleButtonConstraints = new GBC().ipadx(2).ipady(2).gridx(0).gridy(-1).fill(GridBagConstraints.HORIZONTAL);
        tablePanelConstraints = new GBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.BOTH);


        // mainTabPanel
        mainTabPanel = new JPanel();
        mainTabPanel.setLayout(new GridBagLayout());
        root.tabbedPane.addTab("Input", mainTabPanel);
        root.tabbedPane.setSelectedComponent(mainTabPanel);


        // toggleButtonPanel
        toggleButtonPanel = new JPanel();
        toggleButtonPanel.setLayout(new GridBagLayout());
        mainTabPanel.add(toggleButtonPanel, new GBC().weightx(0).weighty(0).gridx(0).gridy(0).insets(new Insets(0, 0, 10, 10)).anchor(GridBagConstraints.NORTHWEST));


        // tableRootPanel
        tableRootPanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(tableRootPanel, new GBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(2).gridy(0).anchor(GridBagConstraints.EAST));


        // maps
        keyToValuePanel = new HashMap<>();
        keyToValueField = new HashMap<>();
    }

    @Override
    public void onNotStale() {
    }

    @Override
    public void onStale() {
    }

    @Override
    public void onCreateDefaultKey(final String keyName, final String keyValue) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));
        keyToValuePanel.put(keyName, panel);

        JLabel keyLabel = new JLabel(keyName);
        keyLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(keyLabel, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 20));
        panel.add(separator, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

        JTextField valueField = new JTextField(keyValue);
        valueField.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(valueField, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));
        keyToValueField.put(keyName, valueField);

        tableRootPanel.add(panel);
        tableRootPanel.revalidate();
    }

    @Override
    public void onUpdateDefaultKey(final String keyName, final String keyValue) {
    }

    @Override
    public void onDeleteKey(final String keyName) {
        JPanel valuePanel = keyToValuePanel.get(keyName);
        tableRootPanel.remove(valuePanel);
        tableRootPanel.revalidate();
    }
}
