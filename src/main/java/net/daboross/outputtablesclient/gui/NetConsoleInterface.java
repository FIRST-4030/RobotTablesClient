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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.output.Output;

public class NetConsoleInterface {

    private static final String ADDRESS = "10.40.30.2";
    private static final int RECEIVING_PORT = 6666;
    private static final int SENDING_PORT = 6668;
    private final JPanel rootPanel;
    private final JTextArea textArea;
    private DatagramSocket receiving;

    public NetConsoleInterface() {
        // GUI
        rootPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane textPane = new JScrollPane(textArea);
        rootPanel.add(textPane, BorderLayout.CENTER);

        // Init
        try {
            this.receiving = new DatagramSocket(RECEIVING_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        new NetConsoleListenerThread().start();
    }

    public void addTo(InterfaceRoot root) {
        root.tabbedPane.addTab("Console", rootPanel);
    }

    public class NetConsoleListenerThread extends Thread {

        private byte[] buffer = new byte[2048];

        @Override
        public void run() {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    receiving.receive(packet);
                } catch (IOException e) {
                    Output.log("IOException receiving DatagramPacket: %s", e);
                }
                if (packet.getLength() != 0) {
                    final String str = new String(buffer, 0, packet.getLength(), Charset.forName("UTF-8"));
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            textArea.append(str);
                        }
                    });
                }
            }
        }
    }
}
