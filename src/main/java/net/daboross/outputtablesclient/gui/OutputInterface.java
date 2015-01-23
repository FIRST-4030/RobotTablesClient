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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.main.OutputTablesMain;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.util.GBC;
import net.daboross.outputtablesclient.util.WrapLayout;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.json.JSONObject;

public class OutputInterface implements OutputListener {

    private final Application application;
    final GridBagConstraints toggleButtonConstraints;
    final GridBagConstraints tablePanelConstraints;
    final JPanel mainTabPanel;
    final JPanel toggleButtonPanel;
    final JPanel tableRootPanel;
    final Map<String, JToggleButton> tableKeyToTableButton;
    final Map<String, Boolean> tableKeyToTableEnabled;
    final Map<String, JPanel> tableKeyToTablePanel;
    final Map<String, Map<String, JPanel>> tableKeyAndKeyToValuePanel;
    final Map<String, Map<String, JLabel>> tableKeyAndKeyToValueLabel;
    final JSONObject persistEnabled;

    public OutputInterface(final Application application) {
        this.application = application;

        // persistEnabled
        JSONObject parentObj = application.getPersist().obj();
        JSONObject tempPersistEnabled = parentObj.optJSONObject("last-shown-panels");
        if (tempPersistEnabled == null) {
            tempPersistEnabled = new JSONObject();
            parentObj.put("last-shown-panels", tempPersistEnabled);
        }
        persistEnabled = tempPersistEnabled;

        // constraints
        toggleButtonConstraints = new GBC().ipadx(2).ipady(2).gridx(0).gridy(-1).fill(GridBagConstraints.HORIZONTAL);
        tablePanelConstraints = new GBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.BOTH);


        // mainTabPanel
        mainTabPanel = new JPanel();
        mainTabPanel.setLayout(new GridBagLayout());
        application.getRoot().getInputOutputPanel().add(mainTabPanel);


        // toggleButtonPanel
        toggleButtonPanel = new JPanel();
        toggleButtonPanel.setLayout(new GridBagLayout());
        mainTabPanel.add(toggleButtonPanel, new GBC().weightx(0).weighty(0).gridx(0).gridy(0).insets(new Insets(0, 0, 10, 10)).anchor(GridBagConstraints.NORTHWEST));


        // tableRootPanel
        tableRootPanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(tableRootPanel, new GBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(2).gridy(0).anchor(GridBagConstraints.EAST));


        // maps
        tableKeyToTableButton = new TreeMap<>();
        tableKeyToTableEnabled = new TreeMap<>();
        tableKeyToTablePanel = new HashMap<>();
        tableKeyAndKeyToValuePanel = new HashMap<>();
        tableKeyAndKeyToValueLabel = new HashMap<>();
    }

    private void ensureTableExists(String tableKey) {
        if (tableKeyToTablePanel.get(tableKey) == null) {
            createTable(tableKey);
        }
    }

    private void createTable(String tableKey) {
        JPanel tablePanel = new JPanel(new WrapLayout());
        Border lineBorder = new LineBorder(new Color(0, 0, 0));
        Border titleBorder = new TitledBorder(lineBorder, tableKey);
        Border spaceBorder = new EmptyBorder(5, 5, 5, 5);
        Border compoundBorder = new CompoundBorder(titleBorder, spaceBorder);
        tablePanel.setBorder(compoundBorder);
        tableKeyToTablePanel.put(tableKey, tablePanel);

        JToggleButton toggleButton = new JToggleButton(tableKey);
        TableToggleListener listener = new TableToggleListener(toggleButton, tableKey);
        toggleButton.addItemListener(listener);
        listener.initialAdd();
        tableKeyToTableButton.put(tableKey, toggleButton);
        toggleButtonPanel.removeAll();
        for (JToggleButton button : tableKeyToTableButton.values()) {
            toggleButtonPanel.add(button, toggleButtonConstraints);
        }

        tableKeyAndKeyToValuePanel.put(tableKey, new HashMap<String, JPanel>());
        tableKeyAndKeyToValueLabel.put(tableKey, new HashMap<String, JLabel>());
    }

    @Override
    public void onTableCreate(final RobotTable table) {
    }

    @Override
    public void onTableStale(final String tableKey) {
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        if (action == UpdateAction.NEW) {
            if (key.equalsIgnoreCase(":RangeGUI")) {
                Output.oLog("Range: %s", value);
                try {
                    application.getCustomInterface().setTo(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    Output.oLog("Invalid range '%s'", value);
                }
            }
            ensureTableExists(table.getName());
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new LineBorder(Color.BLACK));
            tableKeyAndKeyToValuePanel.get(table.getName()).put(key, panel);

            JLabel keyLabel = new JLabel(key);
            keyLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel.add(keyLabel, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

            JSeparator separator = new JSeparator(JSeparator.VERTICAL);
            separator.setPreferredSize(new Dimension(2, 20));
            panel.add(separator, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

            JLabel valueLabel = new JLabel(value);
            valueLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel.add(valueLabel, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));
            tableKeyAndKeyToValueLabel.get(table.getName()).put(key, valueLabel);

            JPanel parentPanel = tableKeyToTablePanel.get(table.getName());
            parentPanel.add(panel);
            parentPanel.revalidate();
        } else if (action == UpdateAction.UPDATE) {
            if (key.equalsIgnoreCase(":RangeGUI")) {
                Output.oLog("Range: %s", value);
                try {
                    application.getCustomInterface().setTo(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    Output.oLog("Invalid range '%s'", value);
                }
            }
            ensureTableExists(table.getName());
            JLabel valueLabel = tableKeyAndKeyToValueLabel.get(table.getName()).get(key);
            valueLabel.setText(value);
        } else if (action == UpdateAction.DELETE) {
            JPanel parentPanel = tableKeyToTablePanel.get(table.getName());
            JPanel valuePanel = tableKeyAndKeyToValuePanel.get(table.getName()).get(key);
            parentPanel.remove(valuePanel);
            parentPanel.revalidate();
        }
    }

    @Override
    public void onTableCleared(final RobotTable table) {
    }

    private class TableToggleListener implements ItemListener {

        private final JToggleButton button;
        private final String tableKey;

        public TableToggleListener(JToggleButton button, String tableKey) {
            this.button = button;
            this.tableKey = tableKey;
        }

        private void initialAdd() {
            boolean enabled = persistEnabled.optBoolean(tableKey, true);
            tableKeyToTableEnabled.put(tableKey, enabled);
            JPanel panel = tableKeyToTablePanel.get(tableKey);
            tableRootPanel.add(panel, tablePanelConstraints);
            button.setSelected(!enabled); // twice to make sure itemStateChanged is called
            button.setSelected(enabled);
        }

        @Override
        public void itemStateChanged(ItemEvent event) {
            boolean isSelected = button.isSelected();
            tableRootPanel.removeAll();
            for (Map.Entry<String, Boolean> e : tableKeyToTableEnabled.entrySet()) {
                if (e.getKey().equals(tableKey)) {
                    if (isSelected != e.getValue()) {
                        e.setValue(isSelected);
                        persistEnabled.put(tableKey, isSelected);
                        application.getPersist().save();
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
