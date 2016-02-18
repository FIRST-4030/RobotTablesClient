package org.ingrahamrobotics.dashboard.gui;

import javax.swing.SwingUtilities;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;

public class SwingClientForward implements ClientUpdateListener {

    private final ClientUpdateListener listener;

    public SwingClientForward(final ClientUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onTableChangeType(final RobotTable table, final TableType oldType, final TableType newType) {
        SwingUtilities.invokeLater(() -> listener.onTableChangeType(table, oldType, newType));
    }

    @Override
    public void onTableStaleChange(final RobotTable table, final boolean nowStale) {
        SwingUtilities.invokeLater(() -> listener.onTableStaleChange(table, nowStale));
    }

    @Override
    public void onAllSubscribersStaleChange(final RobotTable table, final boolean nowStale) {
        SwingUtilities.invokeLater(() -> listener.onAllSubscribersStaleChange(table, nowStale));
    }

    @Override
    public void onNewTable(final RobotTable table) {
        SwingUtilities.invokeLater(() -> listener.onNewTable(table));
    }
}
