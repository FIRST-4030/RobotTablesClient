/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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
package net.daboross.outputtablesclient.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.daboross.outputtablesclient.output.Output;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONHomeStorage {

    private final ExecutorService saveService = Executors.newSingleThreadExecutor();
    private final SaveRunnable saveRunnable = new SaveRunnable();
    private final File saveFileBuffer;
    private final File saveFile;
    private final JSONObject mainObj;

    public JSONHomeStorage() {
        this.saveFile = new File(System.getProperty("user.home"), ".java-output-client-persist.json");
        this.saveFileBuffer = new File(System.getProperty("user.home"), ".java-output-client-persist.json~");
        JSONObject tempObj;
        try {
            tempObj = load();
        } catch (IOException ex) {
            ex.printStackTrace();
            tempObj = new JSONObject();
        }
        this.mainObj = tempObj;
    }

    private JSONObject load() throws IOException {
        if (!saveFile.exists()) {
            if (saveFile.createNewFile()) {
                return new JSONObject();
            } else {
                throw new IOException("Couldn't create file " + saveFile.getAbsolutePath());
            }
        }
        if (!saveFile.isFile()) {
            throw new IOException("File '" + saveFile.getAbsolutePath() + "' is not a file (perhaps a directory?).");
        }

        try (FileInputStream fis = new FileInputStream(saveFile)) {
            return new JSONObject(new JSONTokener(fis));
        } catch (JSONException ex) {
            try (FileInputStream fis = new FileInputStream(saveFile)) {
                byte[] buffer = new byte[10];
                int read = fis.read(buffer);
                if (read <= 0) return new JSONObject();
                String str = new String(buffer, 0, read, Charset.forName("UTF-8"));
                if (str.trim().length() == 0) {
                    return new JSONObject();
                }
            }
            throw new IOException("JSONException loading " + saveFile.getAbsolutePath(), ex);
        }
    }

    public void save() {
        saveService.execute(saveRunnable);
    }

    public JSONObject obj() {
        return mainObj;
    }

    private class SaveRunnable implements Runnable {

        @Override
        public void run() {

            if (!saveFileBuffer.exists()) {
                try {
                    if (!saveFileBuffer.createNewFile()) {
                        Output.logError("Failed to create file '%s'.", saveFileBuffer);
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Output.logError("Failed to create file '%s'.", saveFileBuffer);
                    return;
                }
            }
            try (FileOutputStream fos = new FileOutputStream(saveFileBuffer)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"))) {
                    mainObj.write(writer);
                }
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                Output.logError("Couldn't write to %s", saveFileBuffer.getAbsolutePath());
                return;
            }
            try {
                Files.move(saveFileBuffer.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
                Output.logError("Failed to move buffer file '%s' to actual save location '%s'", saveFileBuffer.getAbsolutePath(), saveFile);
                return;
            }
        }
    }
}
