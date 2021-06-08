package com.alphalaneous;

import com.alphalaneous.Panels.LevelsPanel;
import com.alphalaneous.SettingsPanels.CommandSettings;
import com.alphalaneous.SettingsPanels.PersonalizationSettings;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.Window;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {



	static {
		try {
			image = ImageIO.read(Objects.requireNonNull(LevelsPanel.class.getClassLoader()
					.getResource("Icons/windowIcon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Image image;
	private static final SystemTray tray = SystemTray.getSystemTray();
	private static final TrayIcon trayIcon = new TrayIcon(image, "GDBoard");

	static {
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("GDBoard");
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		trayIcon.addActionListener(System.out::println);
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Window.focus();
			}
		});
		PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem forceExitItem = new MenuItem("Force Exit");

		forceExitItem.addActionListener(e -> System.exit(0));
		exitItem.addActionListener(e -> Main.close());
		aboutItem.addActionListener(e -> Window.showAttributions());
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(exitItem);
		popup.add(forceExitItem);
		trayIcon.setPopupMenu(popup);
	}


	public static void addCommand(String username, String... args) {
		String newCommandName = args[0].replace("\\\\", "").replace("/", "");
		StringBuilder message = new StringBuilder();

		for (String msg : args) {
			message.append(" ").append(msg);
		}
		message = new StringBuilder(StringUtils.replaceOnce(message.toString(), args[0], "").trim());
		String command;
		if (message.toString().startsWith("eval:")) {
			command = "function command(){" + message.toString().replace("eval:", "").trim() + "}";
		} else {
			command = "function command() { return \"" + message.toString() + "\";}";
		}
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\Commands\\" + newCommandName + ".js");
		if (!Files.exists(file)) {
			try {
				if (!Files.exists(Paths.get(Defaults.saveDirectory + "\\GDBoard\\Commands\\"))) {
					Files.createDirectory(Paths.get(Defaults.saveDirectory + "\\GDBoard\\Commands\\"));
				}
				Files.createFile(file);
				Files.write(file, command.getBytes());
				Main.sendMessage(Utilities.format("✔ | $COMMAND_ADDED_SUCCESS$", username, newCommandName));
				CommandSettings.refresh();
				saveOption(newCommandName, "ADVANCED_EDITOR");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Main.sendMessage(Utilities.format("❗ | $COMMAND_EXISTS$", username));
		}
	}

	public static void editCommand(String username, String... args) {
		String newCommandName = args[0];
		StringBuilder message = new StringBuilder();

		for (String msg : args) {
			message.append(" ").append(msg);
		}
		message = new StringBuilder(StringUtils.replaceOnce(message.toString(), args[0], "").trim());
		String command;
		if (message.toString().startsWith("eval:")) {
			command = "function command(){" + message.toString().replace("eval:", "").trim() + "}";
		} else {
			command = "function command() { return \"" + message + "\";}";
		}
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\Commands\\" + newCommandName + ".js");
		if (Files.exists(file)) {
			try {
				Files.write(file, command.getBytes());
				Main.sendMessage(Utilities.format("✔ | $COMMAND_EDIT_SUCCESS$", username, newCommandName));
				saveOption(newCommandName, "ADVANCED_EDITOR");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Main.sendMessage(Utilities.format("❗ | $COMMAND_DOESNT_EXIST$", username));
		}
	}

	public static void deleteCommand(String username, String command) {
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\Commands\\" + command + ".js");
		if (Files.exists(file)) {
			try {
				Files.delete(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.sendMessage(Utilities.format("✔ | $COMMAND_DELETE_SUCCESS$", username, command));
			CommandSettings.refresh();
			deleteCommandA(command);
		} else {
			Main.sendMessage(Utilities.format("❗ | $COMMAND_DOESNT_EXIST$", username, command));

		}
	}

	public static String fetchURL(String url) {
		return APIs.fetchURL(url);
	}

	public static String format(String format, Object... args) {
		format = getLocalizedString(format);
		try {
			return String.format(format, args);
		} catch (Exception e) {
			e.printStackTrace();
			return format;
		}
	}

	static String getLocalizedString(String format) {
		String[] words = format.split(" ");
		for (String word : words) {
			if (word.startsWith("$") && word.endsWith("$")) {
				String newWord = Language.getString(word.replaceAll("\\$", ""));
				format = format.replace(word, newWord);
			}
		}
		return format;
	}

	public static void openLink(String link) {
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("rundll32 url.dll,FileProtocolHandler " + link);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void openSteamApp(int id) {
		new Thread(() -> {
			String choice = DialogBox.showDialogBox("Open Steam App ID: " + id + "?", "Do you want to launch a steam game?", "", new String[]{"$YES$", "$NO$"});
			if (choice.equalsIgnoreCase("yes")) {
				try {
					Desktop.getDesktop().browse(new URI("steam://rungameid/" + id));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void runProgram(String location) {

		String[] loc = location.split("\\\\");
		StringBuilder dir = new StringBuilder();
		for (int i = 0; i < loc.length - 1; i++) {
			dir.append(loc[i]).append("\\\\");
		}
		try {
			Runtime.getRuntime().exec(new String[]{location}, null, new File(dir.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runCommand(String... cmdArray) {
		try {
			Runtime.getRuntime().exec(cmdArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runCommand(String[] args, String[] envp, String fileDirectory) {
		try {
			Runtime.getRuntime().exec(args, envp, new File(fileDirectory));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void delStuff(String command, String identifier) {

		boolean exists = false;
		Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt");
		try {
			if (Files.exists(file)) {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					if (String.valueOf(command).equals(sc.nextLine())) {
						exists = true;
						break;
					}
				}
				sc.close();
				if (exists) {
					Path temp = Paths.get(Defaults.saveDirectory + "\\GDBoard\\_temp" + identifier + "_");
					PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
					Files.lines(file)
							.filter(line -> !line.contains(command))
							.forEach(out::println);
					out.flush();
					out.close();
					Files.delete(file);
					Files.move(temp, temp.resolveSibling(Defaults.saveDirectory + "\\GDBoard\\" + identifier + ".txt"), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} catch (Exception f) {
			f.printStackTrace();
		}

	}

	static void disposeTray() {
		tray.remove(trayIcon);
	}

	public static void notify(String title, String message) {
		if (!PersonalizationSettings.disableNotifOption) {
			trayIcon.displayMessage(title, message, TrayIcon.MessageType.NONE);
		}

	}

	private static void saveOption(String command, String optionType) {
		try {
			String typeA = null;
			if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt"))) {
				Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt").toFile());
				while (sc3.hasNextLine()) {
					String line = sc3.nextLine();
					if (line.split("=").length > 1) {
						if (line.split("=")[0].trim().equalsIgnoreCase(command)) {
							typeA = line.split("=")[1].trim();
							break;
						}
					}
				}
				sc3.close();
			} else {
				Files.createFile(Paths.get(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt"));
			}
			if (typeA != null) {
				BufferedReader file = new BufferedReader(new FileReader(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt"));
				StringBuilder inputBuffer = new StringBuilder();
				String line;
				while ((line = file.readLine()) != null) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
				file.close();

				FileOutputStream fileOut = new FileOutputStream(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt");
				fileOut.write(inputBuffer.toString().replace(command + " = " + typeA, command + " = " + optionType).getBytes());
				fileOut.close();
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(Defaults.saveDirectory + "/GDBoard/" + "commands" + "/options.txt").toFile(), true));
				writer.newLine();
				writer.write(command + " = " + optionType);
				writer.close();
			}
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	private static void delCooldown(String command) {
		try {
			int cooldown = -1;
			if (Files.exists(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"))) {
				Scanner sc3 = new Scanner(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile());
				while (sc3.hasNextLine()) {
					String line = sc3.nextLine();
					if (line.split("=").length > 1) {
						if (line.split("=")[0].trim().equalsIgnoreCase(command)) {
							cooldown = Integer.parseInt(line.split("=")[1].trim());
							break;
						}
					}
				}
				sc3.close();
			} else {
				Files.createFile(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt"));
			}
			if (cooldown != -1) {
				BufferedReader file = new BufferedReader(new FileReader(Defaults.saveDirectory + "/GDBoard/cooldown.txt"));
				StringBuilder inputBuffer = new StringBuilder();
				String line;
				while ((line = file.readLine()) != null) {
					inputBuffer.append(line);
					inputBuffer.append('\n');
				}
				file.close();

				FileOutputStream fileOut = new FileOutputStream(Defaults.saveDirectory + "/GDBoard/cooldown.txt");
				fileOut.write(inputBuffer.toString().replace(command + " = " + cooldown, command + " = " + 0).getBytes());
				fileOut.close();
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(Defaults.saveDirectory + "/GDBoard/cooldown.txt").toFile(), true));
				writer.newLine();
				writer.write(command + " = " + 0);
				writer.close();

			}
		} catch (Exception f) {
			f.printStackTrace();
		}
	}

	private static void deleteCommandA(String command) {
		saveOption(command, "SEND_MESSAGE");
	}

	public static long getID(String message) {
		Matcher m = Pattern.compile("\\s*(\\d{6,})\\s*").matcher(message);
		if (m.find() && !message.startsWith("!")) {
			try {
				String[] msgs = message.split(" ");
				String mention = "";
				for (String s : msgs) {
					if (s.contains("@")) {
						mention = s;
						break;
					}
				}
				if (!mention.contains(m.group(1))) {
					return Long.parseLong(m.group(1));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	static boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
		if (caught == null) return false;
		else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return true;
		else return isCausedBy(caught.getCause(), isOfOrCausedBy);
	}
}
