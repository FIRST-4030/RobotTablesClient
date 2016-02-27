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
package org.ingrahamrobotics.dashboard.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints {

    public GBC anchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    public GBC fill(int fill) {
        this.fill = fill;
        return this;
    }

    public GBC gridheight(int gridheight) {
        this.gridheight = gridheight;
        return this;
    }

    public GBC gridwidth(int gridwidth) {
        this.gridwidth = gridwidth;
        return this;
    }

    public GBC gridx(int gridx) {
        this.gridx = gridx;
        return this;
    }

    public GBC gridy(int gridy) {
        this.gridy = gridy;
        return this;
    }

    public GBC insets(Insets insets) {
        this.insets = insets;
        return this;
    }

    public GBC ipadx(int ipadx) {
        this.ipadx = ipadx;
        return this;
    }

    public GBC ipady(int ipady) {
        this.ipady = ipady;
        return this;
    }

    public GBC weightx(double weightx) {
        this.weightx = weightx;
        return this;
    }

    public GBC weighty(double weighty) {
        this.weighty = weighty;
        return this;
    }
}
