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
package org.ingrahamrobotics.dashboard.main;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

import org.ingrahamrobotics.dashboard.gui.InputInterface;
import org.ingrahamrobotics.dashboard.gui.LogInterface;
import org.ingrahamrobotics.dashboard.gui.NetConsoleInterface;
import org.ingrahamrobotics.dashboard.gui.OutputInterface;
import org.ingrahamrobotics.dashboard.gui.RootInterface;
import org.ingrahamrobotics.dashboard.gui.StaleInterface;
import org.ingrahamrobotics.dashboard.gui.SwingClientForward;
import org.ingrahamrobotics.dashboard.gui.SwingInputForward;
import org.ingrahamrobotics.dashboard.gui.SwingOutputForward;
import org.ingrahamrobotics.dashboard.listener.InputListener;
import org.ingrahamrobotics.dashboard.listener.OutputListener;
import org.ingrahamrobotics.dashboard.output.Output;
import org.ingrahamrobotics.dashboard.output.OutputLoggerListener;
import org.ingrahamrobotics.dashboard.persist.PersistStorage;
import org.ingrahamrobotics.robottables.RobotTables;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;

public class Application {

	private RobotTablesClient tables;
	private RootInterface root;
	private OutputTablesMain outputMain;
	private InputTablesMain inputMain;
	private OutputInterface outputInterface;
	private InputInterface inputInterface;
	private StaleInterface staleInterface;
	private PersistStorage persistStorage;

	public void run() throws InvocationTargetException, InterruptedException, IOException {
		Output.oLog("Initiating root interface");
		SwingUtilities.invokeAndWait(() -> {
			root = new RootInterface();
			root.show();
		});
		Output.setLogger(new LogInterface(root));
		System.setOut(new PrintStream(new Output.StaticOutputStream(), true));
		System.setErr(new PrintStream(new Output.StaticOutputStream(), true));
		SwingUtilities.invokeLater(() -> {
			Output.oLog("Initiating NetConsole");
			new NetConsoleInterface().addTo(root);
			Output.oLog("NetConsole initiated");
		});
		SwingUtilities.invokeLater(root::registerRestart);
		RobotTables tablesStart = new RobotTables();
		tables = tablesStart.getClientInterface();
		Output.oLog("Loading persist");
		persistStorage = new PersistStorage();
		startOutput();
		startInput();
		startStale();
		Output.oLog("Starting RobotTables");
		tablesStart.run();
		Output.oLog("Finished startup sequence");
	}

	public void startOutput() throws InvocationTargetException, InterruptedException {
		Output.oLog("Initiating output-tables");
		outputMain = new OutputTablesMain(this);
		Output.oLog("Initiating output-tables logger");
		OutputLoggerListener outputLoggerListener = new OutputLoggerListener();
		outputMain.addListener(outputLoggerListener);
		SwingUtilities.invokeAndWait(() -> {
			Output.oLog("Initiating output-tables interface");
			outputInterface = new OutputInterface(this);
		});
		OutputListener outputInterfaceListener = new SwingOutputForward(outputInterface);
		outputMain.addListener(outputInterfaceListener);
		Output.oLog("Subscribing to output-tables");
		outputMain.subscribe();
	}

	public void startInput() throws InvocationTargetException, InterruptedException {
		inputMain = new InputTablesMain(this);
		SwingUtilities.invokeAndWait(() -> {
			Output.iLog("Initiating input-tables interface");
			inputInterface = new InputInterface(this);
		});
		InputListener inputInterfaceListener = new SwingInputForward(inputInterface);
		inputMain.addListener(inputInterfaceListener);
		Output.iLog("Subscribing to input-tables");
		inputMain.subscribe();
	}

	public void startStale() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(() -> {
			Output.oLog("Initiating stale interface");
			staleInterface = new StaleInterface(this);
		});
		tables.addClientListener(new SwingClientForward(staleInterface), true);
	}

	public static void main(String[] args) throws IOException, InvocationTargetException, InterruptedException {
		new Application().run();
	}

	public RootInterface getRoot() {
		return root;
	}

	public OutputTablesMain getOutput() {
		return outputMain;
	}

	public InputTablesMain getInput() {
		return inputMain;
	}

	public PersistStorage getPersist() {
		return persistStorage;
	}

	public RobotTablesClient getTables() {
		return tables;
	}
}
