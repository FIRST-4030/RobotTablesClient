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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.daboross.jsonserialization.JsonException;
import net.daboross.jsonserialization.JsonParser;
import net.daboross.jsonserialization.JsonSerialization;
import net.daboross.outputtablesclient.output.Output;

public class PersistStorage {

    private final ExecutorService saveService = Executors.newSingleThreadExecutor();
    private final SaveRunnable saveRunnable = new SaveRunnable();
    private final Path saveFileBuffer;
    private final Path saveFile;
    private final Map<String, Object> mainObject;

    public PersistStorage() {
        String home = System.getProperty("user.home");
        System.out.println("User.home = " + home);
        Path homePath = Paths.get(home);
        this.saveFile = homePath.resolve(".java-output-client-persist.json");
        this.saveFileBuffer = homePath.resolve(".java-output-client-persist.json~");
        Map<String, Object> tempObject;
        try {
            tempObject = load();
        } catch (IOException ex) {
            ex.printStackTrace();
            tempObject = new LinkedHashMap<>();
        }
        this.mainObject = tempObject;
    }

    private Map<String, Object> load() throws IOException {
        if (!Files.exists(saveFile)) {
            Files.createFile(saveFile);
            return new LinkedHashMap<>();
        }
        if (!Files.isRegularFile(saveFile)) {
            throw new IOException("File '" + saveFile.toAbsolutePath() + "' is not a file (perhaps a directory?).");
        }

        try (FileInputStream fis = new FileInputStream(saveFile.toFile());
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            return new JsonParser(br).parseJsonObject();
        } catch (JsonException ex) {
            try (FileInputStream fis = new FileInputStream(saveFile.toFile())) {
                byte[] buffer = new byte[10];
                int read = fis.read(buffer);
                if (read <= 0) return new LinkedHashMap<>();
                String str = new String(buffer, 0, read, Charset.forName("UTF-8"));
                if (str.trim().length() == 0) {
                    return new LinkedHashMap<>();
                }
            }
            throw new IOException("JsonException loading " + saveFile.toAbsolutePath(), ex);
        }
    }

    public void save() {
        saveService.execute(saveRunnable);
    }

    public Map<String, Object> getStorageObject() {
        return mainObject;
    }

    private class SaveRunnable implements Runnable {

        @Override
        public void run() {
            if (!Files.exists(saveFileBuffer)) {
                try {
                    Files.createFile(saveFileBuffer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Output.logError("Failed to create file '%s'.", saveFileBuffer);
                    return;
                }
            }
            System.out.println("Saving storage to " + saveFile);
            try (FileOutputStream fos = new FileOutputStream(saveFileBuffer.toFile())) {
                try (OutputStreamWriter writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
                     BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                    JsonSerialization.writeJsonObject(bufferedWriter, mainObject, 4, 0);
                }
            } catch (IOException | JsonException ex) {
                ex.printStackTrace();
                Output.logError("Couldn't write to %s", saveFileBuffer.toAbsolutePath());
                return;
            }
            try {
                Files.move(saveFileBuffer, saveFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                ex.printStackTrace();
                Output.logError("Failed to move buffer file '%s' to actual save location '%s'", saveFileBuffer.toAbsolutePath(), saveFile);
            }
        }
    }
}
