package net.daboross.outputtablesclient.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.util.GBC;
import org.ingrahamrobotics.robottables.api.RobotTable;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;

public class StaleInterface implements ClientUpdateListener {

    private final RobotTablesClient tablesClient;
    private final AtomicInteger nextStaleY = new AtomicInteger();
    private final AtomicInteger nextSubscriberStaleY = new AtomicInteger();
    private final Map<String, JLabel> subscriberStaleTableLabels = new HashMap<>();
    private final Map<String, JLabel> staleTableLabels = new HashMap<>();
    private final Map<String, JLabel> staleStateLabels = new HashMap<>();
    private final Map<String, JLabel> subscriberStaleStateLabels = new HashMap<>();
    private final JPanel stalePanel;
    private final JLabel mainStatusLabel;
    private final JPanel subscriberStalePanel;

    public StaleInterface(final Application application) {
        this.tablesClient = application.getTables();
        // mainTabPanel
        JPanel mainTabPanel = new JPanel(new GridBagLayout());
        mainTabPanel.setBorder(new TitledBorder("status"));
        application.getRoot().getTabbedPane().add(mainTabPanel, "Status", 3);

        stalePanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(stalePanel, new GBC().weightx(1).fill(GridBagConstraints.BOTH).gridy(0).anchor(GridBagConstraints.EAST));


        subscriberStalePanel = new JPanel(new GridBagLayout());
        mainTabPanel.add(subscriberStalePanel, new GBC().weightx(1).fill(GridBagConstraints.BOTH).gridy(1).anchor(GridBagConstraints.EAST));

        mainStatusLabel = new JLabel("DISCONNECTED - NO INITIAL CONNECTION RECEIVED");
        mainStatusLabel.setFont(mainStatusLabel.getFont().deriveFont(20f));
        mainStatusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        application.getRoot().getMainPanel().add(mainStatusLabel,
                new GBC().weightx(1).gridx(0).gridy(0).anchor(GridBagConstraints.NORTH).gridwidth(2));

        // stalePanel refresh
        stalePanel.revalidate();
    }

    @Override
    public void onTableChangeType(final RobotTable table, final TableType oldType, final TableType newType) {
        JLabel tableLabel;
        JLabel stateLabel;
        JPanel panel;
        switch (table.getType()) {
            case LOCAL:
                tableLabel = subscriberStaleTableLabels.remove(table.getName());
                stateLabel = subscriberStaleStateLabels.remove(table.getName());
                panel = subscriberStalePanel;
                break;
            case REMOTE:
                tableLabel = staleTableLabels.remove(table.getName());
                stateLabel = staleStateLabels.remove(table.getName());
                panel = stalePanel;
                break;
            default:
                return;
        }
        // minimum code duplication
        panel.remove(tableLabel);
        panel.remove(stateLabel);
        panel.revalidate();
        updateMainStatusLabel();
    }

    @Override
    public void onTableStaleChange(final RobotTable table, final boolean nowStale) {
        staleStateLabels.get(table.getName()).setText(nowStale ? "stale" : "fresh");
        stalePanel.revalidate();
        updateMainStatusLabel();
    }

    @Override
    public void onAllSubscribersStaleChange(final RobotTable table, final boolean nowStale) {
        subscriberStaleStateLabels.get(table.getName()).setText(nowStale ? "robot stale" : "robot fresh");
        subscriberStalePanel.revalidate();
        updateMainStatusLabel();
    }

    @Override
    public void onNewTable(final RobotTable table) {
        switch (table.getType()) {
            case LOCAL:
                addLocalTable(table.getName());
                break;
            case REMOTE:
                addRemoteTable(table.getName());
                break;
        }
        updateMainStatusLabel();
    }

    private void addLocalTable(String tableName) {
        int gridY = nextSubscriberStaleY.getAndAdd(1);

        JLabel tableLabel = new JLabel(tableName);
        tableLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        subscriberStaleTableLabels.put(tableName, tableLabel);
        subscriberStalePanel.add(tableLabel, new GBC().gridx(0).gridy(gridY).anchor(GridBagConstraints.WEST));

        JLabel stateLabel = new JLabel("robot stale");
        stateLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        subscriberStaleStateLabels.put(tableName, stateLabel);
        subscriberStalePanel.add(stateLabel, new GBC().gridx(1).gridy(gridY).anchor(GridBagConstraints.EAST));

        subscriberStalePanel.revalidate();
    }

    private void addRemoteTable(String tableName) {
        int gridY = nextStaleY.getAndAdd(1);

        JLabel tableLabel = new JLabel(tableName);
        tableLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        staleTableLabels.put(tableName, tableLabel);
        stalePanel.add(tableLabel, new GBC().gridx(0).gridy(gridY).anchor(GridBagConstraints.WEST));

        JLabel stateLabel = new JLabel("stale");
        stateLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        staleStateLabels.put(tableName, stateLabel);
        stalePanel.add(stateLabel, new GBC().gridx(1).gridy(gridY).anchor(GridBagConstraints.EAST));

        stalePanel.revalidate();
    }

    private void updateMainStatusLabel() {
        boolean localStale = false;
        boolean remoteStale = false;
        for (RobotTable table : tablesClient.getTableSet()) {
            if (table.getType() == TableType.LOCAL) {
                if (table.isSubcriberStale()) {
                    localStale = true;
                }
            } else {
                if (table.isStale()) {
                    remoteStale = true;
                }
            }
        }
        if (localStale) {
            if (remoteStale) {
                mainStatusLabel.setText("DISCONNECTED - NOT ALL ROBOT SETTINGS ARE ACTIVE, NOT ALL DATA IS UP TO DATE");
            } else {
                mainStatusLabel.setText("DISCONNECTED - NOT ALL ROBOT SETTINGS ARE ACTIVE");
            }
        } else {
            if (remoteStale) {
                mainStatusLabel.setText("DISCONNECTED - NOT ALL DATA IS UP TO DATE");
            } else {
                mainStatusLabel.setText("CONNECTED - ALL UP TO DATE");
            }
        }
    }
}
