package org.ingrahamrobotics.robottables.interfaces;

import org.ingrahamrobotics.robottables.InternalTable;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;

public interface InternalTableHandler extends RobotTablesClient {

    public void internalKeyUpdated(InternalTable table, String key, String newValue);

    public void internalKeyRemoved(InternalTable table, String key);

    public void internalTableCleared(InternalTable table);

    public void internalAdminKeyUpdated(InternalTable table, String key, String newValue);

    public void internalAdminKeyRemoved(InternalTable table, String key);

    public void externalPublishedTable(String tableName);

    public void externalKeyUpdated(String tableName, String key, String newValue);

    public void externalKeyRemoved(String tableName, String key);

    public void externalAdminKeyUpdated(String tableName, String key, String newValue);

    public void externalAdminKeyRemoved(String tableName, String key);

    public void fireStaleEvent(final RobotTable table, final boolean nowStale);

    public void fireSubscriberStaleEvent(final RobotTable table, final boolean nowStale);

    public ProtocolTable getTable(String tableName);

    public RobotProtocol getProtocolHandler();
}
