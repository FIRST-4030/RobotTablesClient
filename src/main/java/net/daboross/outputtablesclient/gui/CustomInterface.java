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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.util.GBC;

public class CustomInterface {

    private static final int MAX = 100;
    private final Application application;
    private final JPanel rangePanel;
    private boolean shown;
    private final JProgressBar progressBar;

    public CustomInterface(final Application application) {
        this.application = application;

        // rangePanel
        rangePanel = new JPanel(new GridBagLayout());
        rangePanel.add(new JLabel("Range"), new GBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.BOTH));

        // progressBar
        progressBar = new JProgressBar(0, MAX);
        rangePanel.add(progressBar, new GBC().gridx(0).gridy(-1).weightx(1).weighty(0).anchor(GridBagConstraints.EAST).fill(GridBagConstraints.BOTH));
    }

    public void setTo(int value) {
        if (!shown) {
            shown = true;
            application.getRoot().getMainPanel().add(rangePanel, BorderLayout.SOUTH);
        }
        progressBar.setStringPainted(true);
        progressBar.setValue(value);
    }
}
