package com.alphalaneous;

import com.alphalaneous.SettingsPanels.ChaosModeSettings;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;

public class GDMod {

	public static void runNew(String... args) {

		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}

		if (args[0].equalsIgnoreCase("gamemode")) {

			switch (args[1]) {
				case "cube":
					args[1] = "-1";
					break;
				case "ship":
					args[1] = "0";
					break;
				case "ufo":
					args[1] = "1";
					break;
				case "ball":
					args[1] = "2";
					break;
				case "wave":
					args[1] = "3";
					break;
				case "robot":
					args[1] = "4";
					break;
				case "spider":
					args[1] = "5";
					break;
				default:
					return;
			}
		}
		StringBuilder message = new StringBuilder();
		for (String arg : args) {
			message.append(" ").append(arg);
		}
		message = new StringBuilder(message.toString().trim());
		GDHelper.send(message.toString());
	}

	public static void run(String... args) {

		new Thread(() ->
				ProcessHandle.allProcesses().forEach(process -> {
					if (process.info().command().isPresent()) {
						if (process.info().command().get().endsWith("GeometryDash.exe")) {
							long PID = process.pid();
							String[] cmd = new String[]{Defaults.saveDirectory + "\\GDBoard\\bin\\gdmod.exe", String.valueOf(PID)};
							String[] fillCmd = ArrayUtils.addAll(cmd, args);
							ProcessBuilder pb = new ProcessBuilder(fillCmd).redirectErrorStream(true);
							try {
								pb.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				})).start();
	}

	public static String doChaos(String... args) {
		System.out.println(args[0]);
		if (!args[0].equalsIgnoreCase("gamemode") && !args[0].equalsIgnoreCase("kill")) {
			if (args[1] != null) {
				try {
					Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					return "";
				}
			}
		}
		assert args[1] != null;
		switch (args[0]) {
			case "x":
				if (ChaosModeSettings.minXOption && (Double.parseDouble(args[1]) < ChaosModeSettings.minX)) {
					return "";
				}
				if (ChaosModeSettings.maxXOption && (Double.parseDouble(args[1]) > ChaosModeSettings.maxX)) {
					return "";
				}
				break;
			case "y":
				if (ChaosModeSettings.minYOption && (Double.parseDouble(args[1]) < ChaosModeSettings.minY)) {
					return "";
				}
				if (ChaosModeSettings.maxYOption && (Double.parseDouble(args[1]) > ChaosModeSettings.maxY)) {
					return "";
				}
				break;
			case "size":
				if (ChaosModeSettings.minSizeOption && (Double.parseDouble(args[1]) < ChaosModeSettings.minSize)) {
					return "";
				}
				if (ChaosModeSettings.maxSizeOption && (Double.parseDouble(args[1]) > ChaosModeSettings.maxSize)) {
					return "";
				}
				break;
			case "speed":
				if (ChaosModeSettings.minSpeedOption && (Double.parseDouble(args[1]) < ChaosModeSettings.minSpeed)) {
					return "";
				}
				if (ChaosModeSettings.maxSpeedOption && (Double.parseDouble(args[1]) > ChaosModeSettings.maxSpeed)) {
					return "";
				}
				break;
			case "kill":
				if (!ChaosModeSettings.disableKillOption) {
					run("kill");
				}
				return "";
			default:
				break;
		}

		runNew(args);
		return "Success " + Arrays.toString(args);
	}
}
