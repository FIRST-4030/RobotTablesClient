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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.api.OutputListener;
import net.daboross.outputtablesclient.main.OutputTableMain;
import net.daboross.outputtablesclient.util.DGBC;
import net.daboross.outputtablesclient.util.WrapLayout;

public class OutputTablesGUI implements OutputListener {

    private final OutputTableMain main;
    final GridBagConstraints toggleButtonConstraints;
    final GridBagConstraints tablePanelConstraints;
    final JFrame rootFrame;
    final JTabbedPane tabbedPane;
    final JPanel mainTabPanel;
    final JPanel toggleButtonPanel;
    final JTextArea loggingTextArea;
    final JPanel tableRootPanel;
    final Map<String, Boolean> tableKeyToTableEnabled;
    final Map<String, JPanel> tableKeyToTablePanel;
    final Map<String, Map<String, JPanel>> tableKeyAndKeyToValuePanel;
    final Map<String, Map<String, JLabel>> tableKeyAndKeyToValueLabel;

    public OutputTablesGUI(OutputTableMain main) {
        this.main = main;

        // constraints
        toggleButtonConstraints = new DGBC().ipadx(2).ipady(2).gridx(0).gridy(-1).fill(GridBagConstraints.HORIZONTAL);
        tablePanelConstraints = new DGBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.BOTH);


        // rootFrame
        rootFrame = new JFrame();
        rootFrame.setMinimumSize(new Dimension(640, 480));
        rootFrame.setPreferredSize(new Dimension(640, 480));
        rootFrame.setLayout(new BorderLayout());
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setExtendedState(rootFrame.getExtendedState() | JFrame.MAXIMIZED_HORIZ);
        rootFrame.setTitle("Robot Output " + OutputTablesGUI.class.getPackage().getImplementationVersion());

        // tabbedPane
        tabbedPane = new JTabbedPane();
        rootFrame.add(tabbedPane, BorderLayout.CENTER);


        // mainTabPanel
        mainTabPanel = new JPanel();
        mainTabPanel.setLayout(new GridBagLayout());
        tabbedPane.addTab("Main", mainTabPanel);

        // loggingTextArea
        loggingTextArea = new JTextArea(20, 25);
        ((DefaultCaret) loggingTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane loggingPane = new JScrollPane(loggingTextArea);
        tabbedPane.addTab("Log", loggingPane);

        // toggleButtonPanel
        toggleButtonPanel = new JPanel();
        toggleButtonPanel.setLayout(new GridBagLayout());
        mainTabPanel.add(toggleButtonPanel, new DGBC().weightx(0).weighty(0).gridx(0).gridy(0).insets(new Insets(0, 0, 10, 10)).anchor(GridBagConstraints.NORTHWEST));


        // tableRootPanel
        tableRootPanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(tableRootPanel, new DGBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(2).gridy(0).anchor(GridBagConstraints.EAST));


        // maps
        tableKeyToTableEnabled = new TreeMap<>();
        tableKeyToTablePanel = new HashMap<>();
        tableKeyAndKeyToValuePanel = new HashMap<>();
        tableKeyAndKeyToValueLabel = new HashMap<>();
    }

    public void show() {
        rootFrame.setVisible(true);
    }

    private void ensureTableExists(String tableKey) {
        if (tableKeyToTablePanel.get(tableKey) == null) {
            createTable(tableKey, String.valueOf(main.getTableName(tableKey)));
        }
    }

    private void createTable(String tableKey, String tableName) {
        JPanel tablePanel = new JPanel(new WrapLayout());
        Border lineBorder = new LineBorder(new Color(0, 0, 0));
        Border titleBorder = new TitledBorder(lineBorder, tableName);
        Border spaceBorder = new EmptyBorder(5, 5, 5, 5);
        Border compoundBorder = new CompoundBorder(titleBorder, spaceBorder);
        tablePanel.setBorder(compoundBorder);
        tableKeyToTablePanel.put(tableKey, tablePanel);

        JToggleButton toggleButton = new JToggleButton(tableName);
        TableToggleListener listener = new TableToggleListener(toggleButton, tableKey);
        toggleButton.addItemListener(listener);
        listener.initialAdd();
        toggleButtonPanel.add(toggleButton, toggleButtonConstraints);

        tableKeyAndKeyToValuePanel.put(tableKey, new HashMap<String, JPanel>());
        tableKeyAndKeyToValueLabel.put(tableKey, new HashMap<String, JLabel>());
    }

    @Override
    public void onTableCreate(final String tableKey, final String tableName) {
    }

    @Override
    public void onTableStale(final String tableKey) {
    }

    @Override
    public void onKeyCreate(final String tableKey, final String keyName, final String keyValue) {
        ensureTableExists(tableKey);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.BLACK));
        tableKeyAndKeyToValuePanel.get(tableKey).put(keyName, panel);

        JLabel keyLabel = new JLabel(keyName);
        keyLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(keyLabel, new DGBC().fill(GridBagConstraints.VERTICAL).gridy(0));

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 20));
        panel.add(separator, new DGBC().fill(GridBagConstraints.VERTICAL).gridy(0));

        JLabel valueLabel = new JLabel(keyValue);
        valueLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(valueLabel, new DGBC().fill(GridBagConstraints.VERTICAL).gridy(0));
        tableKeyAndKeyToValueLabel.get(tableKey).put(keyName, valueLabel);

        JPanel parentPanel = tableKeyToTablePanel.get(tableKey);
        parentPanel.add(panel);
        parentPanel.revalidate();
    }

    @Override
    public void onKeyUpdate(final String tableKey, final String keyName, final String keyValue) {
        ensureTableExists(tableKey);
        JLabel valueLabel = tableKeyAndKeyToValueLabel.get(tableKey).get(keyName);
        valueLabel.setText(keyValue);
    }

    @Override
    public void onKeyDelete(final String tableKey, final String keyName) {
        JPanel parentPanel = tableKeyToTablePanel.get(tableKey);
        JPanel valuePanel = tableKeyAndKeyToValuePanel.get(tableKey).get(keyName);
        parentPanel.remove(valuePanel);
        parentPanel.revalidate();
    }

    private class TableToggleListener implements ItemListener {

        private final JToggleButton button;
        private final String tableKey;

        public TableToggleListener(JToggleButton button, String tableKey) {
            this.button = button;
            this.tableKey = tableKey;

        }

        private void initialAdd() {
            tableKeyToTableEnabled.put(tableKey, Boolean.TRUE);
            JPanel panel = tableKeyToTablePanel.get(tableKey);
            tableRootPanel.add(panel, tablePanelConstraints);
            button.setSelected(true);
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            tableRootPanel.removeAll();
            for (Map.Entry<String, Boolean> e : tableKeyToTableEnabled.entrySet()) {
                if (e.getKey().equals(tableKey)) {
                    if (button.isSelected() != e.getValue()) {
                        e.setValue(button.isSelected());
                    }
                }
                if (e.getValue()) {
                    JPanel panel = tableKeyToTablePanel.get(e.getKey());
                    tableRootPanel.add(panel, tablePanelConstraints);
                }
            }
            tableRootPanel.revalidate();
            tableRootPanel.repaint();
        }
    }
}
