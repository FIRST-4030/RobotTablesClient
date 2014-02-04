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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;
import org.jdesktop.swingx.JXCollapsiblePane;

public class ClientFrameManager {

    private final GridBagConstraints constraints = new GridBagConstraints();
    private final JTextArea loggingText = new JTextArea(30, 40);
    private final JFrame frame = new JFrame();

    public ClientFrameManager() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ((DefaultCaret) loggingText.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        constraints.fill = GridBagConstraints.VERTICAL;
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new GridBagLayout());
        addCollapsibleLabeledComponent("Log", loggingText);
        frame.setTitle("Robot Output");
    }

    public void show() {
        frame.setVisible(true);
    }

    public final JLabel addCollapsibleLabeledComponent(final String labelText, JComponent internalComponent) {
        final JButton collapse = new JButton("Hide");
        final JXCollapsiblePane collapsiblePane = new JXCollapsiblePane();
        JLabel label = new JLabel(labelText);
        final JComponent scrollPane = new JScrollPane(internalComponent);
        collapsiblePane.add(scrollPane);
        collapse.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (collapsiblePane.isCollapsed()) {
                    collapsiblePane.add(scrollPane);
                    collapsiblePane.setCollapsed(false);
                    collapse.setText("Hide");
                } else {
                    collapsiblePane.setCollapsed(true);
                    collapse.setText("Show");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(1000);
                                collapsiblePane.remove(scrollPane);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }.start();
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.ABOVE_BASELINE;
        c.gridy = 1;
        panel.add(label, c);
        c.anchor = GridBagConstraints.BELOW_BASELINE;
        c.gridy = 2;
        panel.add(collapsiblePane, c);
        c.anchor = GridBagConstraints.BELOW_BASELINE;
        c.gridy = 3;
        panel.add(collapse, c);

        addComponent(panel);
        return label;
    }

    public void addComponent(Component component) {
        constraints.gridx++;
        frame.add(component, constraints);
    }

    public void log(String msg, Object... args) {
        String message = String.format("[%s] %s\n", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(msg, args));
        loggingText.append(message);
        System.out.print(message);
    }
}
