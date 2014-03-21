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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.main.Application;
import net.daboross.outputtablesclient.output.Output;

public class NetConsoleInterface {

    private static final Pattern outputPattern = Pattern.compile(
            "^\\[Output\\]\\[\\s*([^\\]]+)\\s*\\]\\s*\\[\\s*([^\\]]+)\\s*\\]\\s*(\\S.*)$"
    );
    private static final int RECEIVING_PORT = 6666;
    private final JPanel rootPanel;
    private final JTextArea textArea;
    private final Application application;
    private DatagramSocket receiving;

    public NetConsoleInterface(Application application) {
        this.application = application;
        // GUI
        rootPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane textPane = new JScrollPane(textArea);
        rootPanel.add(textPane, BorderLayout.CENTER);

        new NetConsoleListenerThread().start();
    }

    public void addTo(RootInterface root) {
        root.getTabbedPane().addTab("Robot NetConsole", rootPanel);
    }

    public class NetConsoleListenerThread extends Thread {

        private byte[] buffer = new byte[2048];
        private StringBuilder lineBuilder;

        @Override
        public void run() {
            // Init
            try {
                for (boolean started = false; !started; Thread.sleep(2000)) {
                    try {
                        NetConsoleInterface.this.receiving = new DatagramSocket(RECEIVING_PORT);
                        started = true;
                    } catch (SocketException e) {
                        // e.printStackTrace();
                        // return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    receiving.receive(packet);
                } catch (IOException e) {
                    Output.oLog("IOException receiving DatagramPacket: %s", e);
                }
                if (packet.getLength() != 0) {
                    String str = new String(buffer, 0, packet.getLength(), Charset.forName("UTF-8"));
                    while (str.contains("\n")) {
                        String[] split = str.split("\n", 2);
                        lineBuilder.append(split[0]);
                        processLine(lineBuilder.toString().trim());
                        lineBuilder = new StringBuilder();
                        if (split.length == 2) {
                            str = split[1];
                        }
                    }
                    if (!str.isEmpty()) {
                        lineBuilder.append(str);
                    }
                }
            }
        }

        private void processLine(final String line) {
            Matcher m = outputPattern.matcher(line);
            if (m.find()) {
                try {
                    MatchResult mr = m.toMatchResult();
                    String table = mr.group(1);
                    String key = mr.group(2);
                    String value = mr.group(3);

                    if (!table.isEmpty() && !key.isEmpty()) {
//                                        System.out.println("Table data: [" + table + "] " + key + " => " + value);
//
//                                        // Special handling for Important.:RangeGUI
//                                        if ("Important".equals(table) && ":RangeGUI".equals(key)) {
//                                            try {
//                                                double parsed = Double.parseDouble(value);
//                                                application.getCustomInterface().setTo(parsed);
//                                            } catch (NumberFormatException ex) {
//                                                System.out.printf("Invalid double: %s\n", value);
//                                            }
//                                        }

                        // Send a manual update to OutputTablesMain - this will automatically update the RangeGUI and other values.
                        application.getOutput().manualUpdate(table, key.trim(), value.trim());
                    }
                } catch (IllegalStateException | IndexOutOfBoundsException ignored) {
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    textArea.append(line);
                }
            });
        }
    }
}
