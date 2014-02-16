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
package net.daboross.outputtablesclient.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Output {

    private static StaticLogger logger = new DefaultLogger();

    public static void log(String message, Object... args) {
        if (logger != null) {
            logger.log(String.format("[%s] %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), String.format(message, args)));
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

        private StringBuffer currentBuffer = new StringBuffer();

        @Override
        public void write(final int b) throws IOException {
            String str = new String(new byte[b], Charset.forName("UTF-8"));
            if (str.equals("\n") || (str.length() > 1 && str.contains("\n"))) {
                currentBuffer.append(str);
                log(currentBuffer.toString());
                currentBuffer = new StringBuffer();
            }
        }
    }
}
