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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.daboross.outputtablesclient.listener.OutputListener;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.util.GBC;
import net.daboross.outputtablesclient.util.WrapLayout;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.UpdateAction;

public class OutputInterface implements OutputListener {

    private static final class PanelWithKey {

        private final JPanel panel;
        private final String key;

        private PanelWithKey(final JPanel panel, final String key) {
            this.panel = panel;
            this.key = key.toLowerCase();
        }
    }

    private final Application application;
    final GridBagConstraints toggleButtonConstraints;
    final GridBagConstraints tablePanelConstraints;
    final JPanel mainTabPanel;
    final JPanel toggleButtonPanel;
    final JPanel tableRootPanel;
    final JTextArea searchArea;
    private String lastSearchString = "";
    final Map<String, JToggleButton> tableKeyToTableButton;
    final Map<String, Boolean> tableKeyToTableEnabled;
    final Map<String, JPanel> tableKeyToTablePanel;
    final Map<String, Map<String, JPanel>> tableKeyAndKeyToValuePanel;
    final Map<String, Map<String, JLabel>> tableKeyAndKeyToValueLabel;
    final Map<String, TitledBorder> tableKeyToTableTitledBoarder;
    final Map<String, Boolean> persistEnabled;
    final List<PanelWithKey> allKeyAndValuePanels;

    public OutputInterface(final Application application) {
        this.application = application;

        // persistEnabled
        Object tempPersistEnabledObj = application.getPersist().getStorageObject().get("last-shown-panels");
        if (tempPersistEnabledObj != null && tempPersistEnabledObj instanceof Map) {
            persistEnabled = (Map) tempPersistEnabledObj;
        } else {
            persistEnabled = new HashMap<>();
            application.getPersist().getStorageObject().put("last-shown-panels", persistEnabled);
        }

        // constraints
        toggleButtonConstraints = new GBC().ipadx(2).ipady(2).gridx(0).gridy(-1).fill(GridBagConstraints.HORIZONTAL);
        tablePanelConstraints = new GBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.HORIZONTAL);


        // mainTabPanel
        mainTabPanel = new JPanel();
        mainTabPanel.setLayout(new GridBagLayout());
        application.getRoot().getMainPanel().add(mainTabPanel, new GBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(0).gridy(1));

        // leftSidePanel
        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new GridBagLayout());
        mainTabPanel.add(leftSidePanel, new GBC().weightx(0).weighty(0).gridx(0).gridy(0).anchor(GridBagConstraints.NORTHWEST));

        // toggleButtonPanel
        toggleButtonPanel = new JPanel();
        toggleButtonPanel.setLayout(new GridBagLayout());
        leftSidePanel.add(toggleButtonPanel, new GBC().weightx(0).weighty(0).gridx(0).gridy(0).insets(new Insets(0, 0, 10, 10)).anchor(GridBagConstraints.NORTHWEST));


        // tableRootPanel
        tableRootPanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(tableRootPanel, new GBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(2).gridy(0).anchor(GridBagConstraints.EAST));

        // searchArea
        searchArea = new JTextArea();
        searchArea.setMinimumSize(new Dimension(100, 23));
        searchArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        searchArea.getDocument().addDocumentListener(new SearchAreaActionListener());
        leftSidePanel.add(searchArea, new GBC().weightx(0).weighty(0).gridx(0).gridy(1).insets(new Insets(0, 0, 10, 10)).anchor(GridBagConstraints.NORTHWEST));

        // maps
        tableKeyToTableButton = new TreeMap<>();
        tableKeyToTableEnabled = new TreeMap<>();
        tableKeyToTablePanel = new HashMap<>();
        tableKeyAndKeyToValuePanel = new HashMap<>();
        tableKeyAndKeyToValueLabel = new HashMap<>();
        tableKeyToTableTitledBoarder = new HashMap<>();
        allKeyAndValuePanels = new ArrayList<>();
    }

    private void ensureTableExists(String tableKey) {
        if (tableKeyToTablePanel.get(tableKey) == null) {
            createTable(tableKey);
        }
    }

    private void createTable(String tableKey) {
        String displayName = application.getOutput().getNameTable().get(tableKey);
        if (displayName == null) {
            Output.logError("Warning! No known display name for table %s", tableKey);
            displayName = tableKey;
        }
        JPanel tablePanel = new JPanel(new WrapLayout());
        Border lineBorder = new LineBorder(new Color(0, 0, 0));
        TitledBorder titleBorder = new TitledBorder(lineBorder, displayName);
        tableKeyToTableTitledBoarder.put(tableKey, titleBorder);
        Border spaceBorder = new EmptyBorder(5, 5, 5, 5);
        Border compoundBorder = new CompoundBorder(titleBorder, spaceBorder);
        tablePanel.setBorder(compoundBorder);
        tableKeyToTablePanel.put(tableKey, tablePanel);

        JToggleButton toggleButton = new JToggleButton(displayName);
        TableToggleListener listener = new TableToggleListener(toggleButton, tableKey);
        toggleButton.addItemListener(listener);
        listener.initialAdd();
        tableKeyToTableButton.put(tableKey, toggleButton);
        toggleButtonPanel.removeAll();
        for (JToggleButton button : tableKeyToTableButton.values()) {
            toggleButtonPanel.add(button, toggleButtonConstraints);
        }

        tableKeyAndKeyToValuePanel.put(tableKey, new HashMap<>());
        tableKeyAndKeyToValueLabel.put(tableKey, new HashMap<>());
    }

    @Override
    public void onTableCreate(final RobotTable table) {
    }

    @Override
    public void onTableDisplayNameChange(final RobotTable table, final String newDisplayName) {
        if (tableKeyToTablePanel.get(table.getName()) == null) {
            // In case we were out of date because we ignored the table updates
            for (String key : table.getKeys()) {
                this.onUpdate(table, key, table.get(key), UpdateAction.NEW);
            }
        } else {
            tableKeyToTableButton.get(table.getName()).setText(newDisplayName);
            tableKeyToTableTitledBoarder.get(table.getName()).setTitle(newDisplayName);
        }
    }

    @Override
    public void onTableStaleChange(final String tableKey, boolean nowStale) {
    }

    @Override
    public void onUpdate(final RobotTable table, final String key, final String value, final UpdateAction action) {
        if (!application.getOutput().getNameTable().contains(table.getName())) {
            return;
        }
        if (action == UpdateAction.NEW) {
            ensureTableExists(table.getName());
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new LineBorder(Color.BLACK));
            tableKeyAndKeyToValuePanel.get(table.getName()).put(key, panel);
            allKeyAndValuePanels.add(new PanelWithKey(panel, key));
            if (!key.toLowerCase().contains(lastSearchString)) {
                panel.setVisible(false);
            }

            JLabel keyLabel = new JLabel(key);
            keyLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel.add(keyLabel, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

            JSeparator separator = new JSeparator(JSeparator.VERTICAL);
            separator.setPreferredSize(new Dimension(2, 20));
            panel.add(separator, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

            JLabel valueLabel = new JLabel(value);
            valueLabel.setMinimumSize(new Dimension(50, 20));
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            valueLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel.add(valueLabel, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));
            tableKeyAndKeyToValueLabel.get(table.getName()).put(key, valueLabel);

            JPanel parentPanel = tableKeyToTablePanel.get(table.getName());
            parentPanel.add(panel);
            parentPanel.revalidate();
        } else if (action == UpdateAction.UPDATE) {
            ensureTableExists(table.getName());
            JLabel valueLabel = tableKeyAndKeyToValueLabel.get(table.getName()).get(key);
            valueLabel.setText(value);
        } else if (action == UpdateAction.DELETE) {
            if (tableKeyToTablePanel.get(table.getName()) == null) {
                return;
            }
            JPanel parentPanel = tableKeyToTablePanel.get(table.getName());
            JPanel valuePanel = tableKeyAndKeyToValuePanel.get(table.getName()).remove(key);
            parentPanel.remove(valuePanel);
            parentPanel.revalidate();
        }
    }

    @Override
    public void onTableCleared(final RobotTable table) {
        if (!application.getOutput().getNameTable().contains(table.getName())) {
            return;
        }
        if (tableKeyToTablePanel.get(table.getName()) == null) {
            return;
        }
        JPanel parentPanel = tableKeyToTablePanel.get(table.getName());
        if (parentPanel == null) {
            return;
        }
        tableKeyAndKeyToValuePanel.get(table.getName()).values().forEach(parentPanel::remove);
        parentPanel.revalidate();
        tableKeyAndKeyToValuePanel.get(table.getName()).clear();
    }

    private class TableToggleListener implements ItemListener {

        private final JToggleButton button;
        private final String tableKey;

        public TableToggleListener(JToggleButton button, String tableKey) {
            this.button = button;
            this.tableKey = tableKey;
        }

        private void initialAdd() {
            boolean enabled = persistEnabled.getOrDefault(tableKey, true);
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

    public void setSearchContents(String searchContents) {
        searchContents = searchContents.toLowerCase();
        for (PanelWithKey panelWithKey : allKeyAndValuePanels) {
            panelWithKey.panel.setVisible(panelWithKey.key.contains(searchContents));
        }
    }

    public class SearchAreaActionListener implements DocumentListener {

        /**
         * This is the time that we should actually update the key. This is pushed to the current time + 1000 every time
         * the field is updated, so we do automatically send, but not with the user's every keystroke.
         */
        private final UpdateRunnable updateRunnable;
        private final Object updateLock;
        private long updateTime;
        private boolean updaterRunning;

        public SearchAreaActionListener() {
            updateRunnable = new UpdateRunnable();
            updateLock = new Object();
        }

        private void startUpdate() {
            synchronized (updateLock) {
                updateTime = System.currentTimeMillis() + 100;
                if (!updaterRunning) {
                    new Thread(updateRunnable).start();
                }
            }
        }

        @Override
        public void insertUpdate(final DocumentEvent e) {
            startUpdate();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            startUpdate();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            startUpdate();
        }

        private class UpdateRunnable implements Runnable {

            @Override
            public void run() {
                long currentUpdateTime;
                synchronized (updateLock) {
                    if (updaterRunning) {
                        return;
                    }
                    updaterRunning = true;
                    currentUpdateTime = updateTime;
                }
                while (true) {
                    long waitTime = currentUpdateTime - System.currentTimeMillis();
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Output.logError("Warning! SearchAreaUpdateRunnable interrupted! Search area will no longer be updated!");
                        e.printStackTrace();
                        synchronized (updateLock) {
                            updaterRunning = false;
                        }
                        return;
                    }
                    synchronized (updateLock) {
                        if (updateTime > currentUpdateTime) {
                            // If the updateTime has changed since we started, we should sleep again.
                            currentUpdateTime = updateTime;
                        } else {
                            // Otherwise, let's update it!
                            updaterRunning = false;
                            break;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    String text = searchArea.getText();
                    Output.iLog("Setting search text to: '%s'", text);
                    lastSearchString = text;
                    setSearchContents(text);
                });
            }
        }
    }
}
