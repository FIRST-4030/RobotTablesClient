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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JPanel;

public class OutputTablesNetConsole {

    private static final String ADDRESS = "10.40.30.2";
    private static final int RECEIVING_PORT = 6666;
    private static final int SENDING_PORT = 6668;
    private final JPanel outputPanel;
    private InetAddress address;
    private DatagramSocket receiving;
    private DatagramSocket sending;

    public OutputTablesNetConsole() {
        // GUI
        outputPanel = new JPanel();


        // Init
        try {
            this.address = InetAddress.getByName(ADDRESS);
            this.receiving = new DatagramSocket(RECEIVING_PORT);
            this.sending = new DatagramSocket(SENDING_PORT);
            this.receiving.connect(address, RECEIVING_PORT);
        } catch (SocketException | UnknownHostException e) {
            return;
        }
    }

    private void send(String command) throws IOException {
        byte[] buf = command.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SENDING_PORT);
        sending.send(packet);
    }

    private void recieve(String str) {
    }
}
