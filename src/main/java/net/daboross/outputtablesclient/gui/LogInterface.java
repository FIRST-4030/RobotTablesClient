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
package net.daboross.outputtablesclient.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingUtilities;
import net.daboross.outputtablesclient.output.Output;

public class LogInterface implements Output.StaticLogger {

    private final RootInterface root;
    private final PrintStream loggingStream;

    public LogInterface(RootInterface root) {
        this.root = root;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "java-output-log-" + format.format(new Date()) + "-" + System.currentTimeMillis();
        File dir = new File(System.getProperty("user.home"), "logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        System.out.printf("Log file is '%s'%n", file.getAbsolutePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PrintStream temp = null;
        try {
            temp = new PrintStream(new FileOutputStream(file), true);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        loggingStream = temp;
    }

    @Override
    public void log(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            root.getLoggingTextArea().append(message + "\n");
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    root.getLoggingTextArea().append(message + "\n");
                }
            });
        }
        if (loggingStream != null) {
            loggingStream.append(message).append("\n");
        }
    }
}
