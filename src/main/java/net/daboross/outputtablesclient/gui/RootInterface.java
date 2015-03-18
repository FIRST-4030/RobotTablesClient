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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;
import net.daboross.outputtablesclient.main.Application;

public class RootInterface {

    private final JFrame rootFrame;
    private final JTabbedPane tabbedPane;
    private final JTextArea loggingTextArea;
    private final JPanel mainPanel;

    public RootInterface() {
        // rootFrame
        rootFrame = new JFrame();
        rootFrame.setMinimumSize(new Dimension(640, 480));
        rootFrame.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 240));
        rootFrame.setLayout(new BorderLayout());
        rootFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        rootFrame.setTitle(String.format("RobotTablesClient %s", OutputInterface.class.getPackage().getImplementationVersion()));

        // tabbedPane
        tabbedPane = new JTabbedPane();
        rootFrame.add(tabbedPane, BorderLayout.CENTER);

        // loggingTextArea
        loggingTextArea = new JTextArea();
        loggingTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        ((DefaultCaret) loggingTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane loggingPane = new JScrollPane(loggingTextArea);
        tabbedPane.addTab("Log", loggingPane);

        // mainPanel
        mainPanel = new JPanel(new GridBagLayout());
        tabbedPane.add(mainPanel, "Main");
        tabbedPane.setSelectedComponent(mainPanel);

        // Video input
        VideoCameraInput cameraPane = new VideoCameraInput();
        tabbedPane.add(cameraPane, "Camera");
        cameraPane.init();
    }

    public void registerRestart() {
        final JPanel emptyPanel = new JPanel();
        tabbedPane.add("Restart", emptyPanel);
        tabbedPane.addChangeListener(evt -> {
            if (tabbedPane.getSelectedComponent() == emptyPanel) {
                tabbedPane.setSelectedComponent(mainPanel);
                new Thread(() -> {
                    try {
                        String path = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
                        PrintStream originalSystemOut = new PrintStream(new FileOutputStream(FileDescriptor.out));
                        originalSystemOut.println("Starting: java -jar " + path);
                        Runtime.getRuntime().exec(new String[]{"java", "-jar", path});
                        System.exit(0);
                    } catch (IOException | URISyntaxException ignored) {
                    }
                }).start();
            }
        });
    }

    public void show() {
        rootFrame.setVisible(true);
    }

    public JFrame getRootFrame() {
        return rootFrame;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JTextArea getLoggingTextArea() {
        return loggingTextArea;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
