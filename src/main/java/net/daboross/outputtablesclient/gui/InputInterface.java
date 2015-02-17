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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.daboross.outputtablesclient.listener.InputListener;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.main.InputTablesMain;
import net.daboross.outputtablesclient.output.Output;
import net.daboross.outputtablesclient.util.GBC;

public class InputInterface implements InputListener {

    private final InputTablesMain main;
    private final JPanel tableRootPanel;
    private final Map<String, JPanel> keyToValuePanel;

    public InputInterface(final Application application) {
        this.main = application.getInput();

        // tableRootPanel
        tableRootPanel = new JPanel(new GridBagLayout());
        tableRootPanel.setMinimumSize(new Dimension(600, 40));
        application.getRoot().getInputOutputAndStalePanel().add(tableRootPanel, new GBC().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).gridx(1).gridy(0));

        // tableRootPanel refresh
        tableRootPanel.revalidate();

        // maps
        keyToValuePanel = new HashMap<>();
    }

    @Override
    public void onNotStale() {
        // TODO: At this point, we should trim all keys which don't exist in the defaultSettingsTable
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

        JTextField valueField = new JTextField(keyValue, 20);
        valueField.setMinimumSize(new Dimension(100, 10));
        valueField.setBorder(new EmptyBorder(5, 5, 5, 5));
        valueField.getDocument().addDocumentListener(new JFieldActionListener(keyName, valueField));
        panel.add(valueField, new GBC().fill(GridBagConstraints.VERTICAL).gridy(0));

        tableRootPanel.add(panel, new GBC().gridx(0).insets(new Insets(5, 0, 5, 0)).anchor(GBC.EAST));
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

    public class JFieldActionListener implements DocumentListener {

        /**
         * This is the time that we should actually update the key. This is pushed to the current time + 1000 every time
         * the field is updated, so we do automatically send, but not with the user's every keystroke.
         */
        private final UpdateRunnable updateRunnable;
        private final Object updateLock;
        private final String key;
        private final JTextField field;
        private long updateTime;
        private boolean updaterRunning;

        public JFieldActionListener(final String key, final JTextField field) {
            this.key = key;
            this.field = field;
            updateRunnable = new UpdateRunnable();
            updateLock = new Object();
        }

        private void startUpdate() {
            synchronized (updateLock) {
                updateTime = System.currentTimeMillis() + 1000;
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
                        Output.logError("Warning! UpdateRunnable interrupted! Key %s will no longer be updated!", key);
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
                    String text = field.getText();
                    Output.iLog("Updated key '%s': '%s'", key, text);
                    main.updateKey(key, field.getText());
                });
            }
        }
    }
}
