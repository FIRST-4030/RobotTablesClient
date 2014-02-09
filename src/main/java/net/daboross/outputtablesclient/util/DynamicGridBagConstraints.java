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
package net.daboross.outputtablesclient.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class DynamicGridBagConstraints extends GridBagConstraints {

    public DynamicGridBagConstraints anchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    public DynamicGridBagConstraints fill(int fill) {
        this.fill = fill;
        return this;
    }

    public DynamicGridBagConstraints gridheight(int gridheight) {
        this.gridheight = gridheight;
        return this;
    }

    public DynamicGridBagConstraints gridwidth(int gridwidth) {
        this.gridwidth = gridwidth;
        return this;
    }

    public DynamicGridBagConstraints gridx(int gridx) {
        this.gridx = gridx;
        return this;
    }

    public DynamicGridBagConstraints gridy(int gridy) {
        this.gridy = gridy;
        return this;
    }

    public DynamicGridBagConstraints insets(Insets insets) {
        this.insets = insets;
        return this;
    }

    public DynamicGridBagConstraints ipadx(int ipadx) {
        this.ipadx = ipadx;
        return this;
    }

    public DynamicGridBagConstraints ipady(int ipady) {
        this.ipady = ipady;
        return this;
    }

    public DynamicGridBagConstraints weightx(double weightx) {
        this.weightx = weightx;
        return this;
    }

    public DynamicGridBagConstraints weighty(double weighty) {
        this.weighty = weighty;
        return this;
    }
}
