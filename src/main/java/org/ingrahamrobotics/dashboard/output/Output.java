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
package org.ingrahamrobotics.dashboard.output;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Output {

    private static StaticLogger logger = new DefaultLogger();

    public static void oLog(String message, Object... args) {
        if (logger != null) {
            logger.log(String.format("[output] [%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(message, args)));
        }
    }

    public static void iLog(String message, Object... args) {
        if (logger != null) {
            logger.log(String.format("[input] [%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(message, args)));
        }
    }

    public static void logError(String message, Object... args) {
        if (logger != null) {
            if (args.length > 0) {
                try {
                    logger.log(String.format("[error] [%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(message, args)));
                } catch (RuntimeException e) {
                    logger.log(String.format("[error] [%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), message + " " + Arrays.toString(args)));
                }
            } else {
                logger.log(String.format("[error] [%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), message));
            }
        }
    }

    public static void setLogger(StaticLogger logger) {
        Output.logger = logger;
    }

    public static interface StaticLogger {

        public void log(String message);
    }

    public static class DefaultLogger implements StaticLogger {

        @Override
        public void log(String message) {
            System.out.println(message);
        }
    }

    public static class StaticOutputStream extends OutputStream {

        private StringBuffer buffer = new StringBuffer();

        @Override
        public void write(final int b) throws IOException {
            if (b == '\n' || b == '\r') {
                logError(buffer.toString());
                buffer = new StringBuffer();
            } else {
                buffer.append((char) b);
            }
        }
    }
}
