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

import net.daboross.outputtablesclient.api.OutputListener;
import net.daboross.outputtablesclient.main.OutputTableMain;

public class LoggerListener implements OutputListener {

    private final OutputTableMain main;

    public LoggerListener(OutputTableMain main) {
        this.main = main;
    }

    @Override
    public void onTableCreate(String tableKey, String tableName) {
        Output.log(" * table(key: %s, name: %s)", tableKey, tableName);
    }

    @Override
    public void onTableStale(String tableKey) {
    }

    @Override
    public void onKeyCreate(String tableKey, String keyName, String keyValue) {
        Output.log("[%s][%s*] %s", main.getTableName(tableKey), keyName, keyValue);
    }

    @Override
    public void onKeyUpdate(String tableKey, String keyName, String keyValue) {
        Output.log("[%s][%s] %s", main.getTableName(tableKey), keyName, keyValue);
    }

    @Override
    public void onKeyDelete(String tableKey, String keyName) {
        Output.log("[%s] - %s", main.getTableName(tableKey), keyName);
    }
}
