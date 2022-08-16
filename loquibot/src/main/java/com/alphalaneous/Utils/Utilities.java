package com.alphalaneous.Utils;

import com.alphalaneous.*;
import com.alphalaneous.Interactive.Commands.CommandData;
import com.alphalaneous.Interactive.Keywords.KeywordData;
import com.alphalaneous.Interactive.Timers.TimerData;
import com.alphalaneous.Services.Twitch.TwitchAPI;
import com.alphalaneous.Settings.SettingsHandler;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Tabs.RequestsTab;
import com.alphalaneous.Windows.LogWindow;
import com.alphalaneous.Windows.Window;

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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

	static {
		try {
			image = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader()
					.getResource("Icons/windowIcon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Image image;
	private static final SystemTray tray = SystemTray.getSystemTray();
	private static final TrayIcon trayIcon = new TrayIcon(image, "loquibot");

	static {
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("loquibot");
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
		MenuItem consoleItem = new MenuItem("Console");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem forceExitItem = new MenuItem("Force Exit");

		forceExitItem.addActionListener(e -> System.exit(0));
		exitItem.addActionListener(e -> Main.close());
		aboutItem.addActionListener(e -> RequestsTab.showAttributions());
		consoleItem.addActionListener(e -> LogWindow.toggleLogWindow());
		popup.add(aboutItem);
		popup.add(consoleItem);
		popup.addSeparator();
		popup.add(exitItem);
		popup.add(forceExitItem);
		trayIcon.setPopupMenu(popup);
	}
	public static void openURL(URI uri){

		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + uri);
		}
		catch (Exception e){
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException ignored) {
			}
		}
	}

	public static String readIntoString(BufferedReader reader){
		return readIntoString(reader, false);
	}

	public static String readIntoString(BufferedReader reader, boolean includeLineBreaks){
		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if(includeLineBreaks) builder.append(line).append("\n");
				else builder.append(line);
			}
			reader.close();
		}
		catch (IOException ignored){}
		return builder.toString();
	}

	public static String fetchURL(String url) {
		return TwitchAPI.fetchURL(url);
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

	public static void load(String file, HashMap<String, String> location){
		Path path = Paths.get(Defaults.saveDirectory + file);
		if (Files.exists(path)) {
			Scanner sc = null;
			try {
				sc = new Scanner(path.toFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			assert sc != null;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("=")) {
					location.put(line.split("=", 2)[0].trim(), line.split("=", 2)[1].trim());
				}
			}
			sc.close();
		}
	}

	public static void save(String file, HashMap<String, String> values){
		Path fileA = Paths.get(Defaults.saveDirectory + file);

		try {
			if (!Files.exists(fileA)) Files.createFile(fileA);

			Iterator<Map.Entry<String, String>> it = values.entrySet().iterator();
			StringBuilder pairs = new StringBuilder();
			while (it.hasNext()) {
				Map.Entry<String, String> pair = it.next();
				pairs.append(pair.getKey()).append(" = ").append(pair.getValue()).append("\n");
			}

			Files.write(fileA, pairs.toString().getBytes());
		} catch (IOException e1) {
			DialogBox.showDialogBox("Error!", e1.toString(), "There was an error writing to the file!", new String[]{"OK"});

		}
	}

	public static String getLocalizedString(String format) {
		String[] words = format.split(" ");
		for (String word : words) {
			if (word.startsWith("$") && word.endsWith("$")) {
				String newWord = Language.getString(word.replaceAll("\\$", ""));
				format = format.replace(word, newWord);
			}
		}
		return format;
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

	public static void disposeTray() {
		tray.remove(trayIcon);
	}

	public static void notify(String title, String message) {
		if (!SettingsHandler.getSettings("disableNotifications").asBoolean()) {
			trayIcon.displayMessage(title, message, TrayIcon.MessageType.NONE);
		}

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

	static void copyFromJar(InputStream source , String destination) {
		try {
			Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
		if (caught == null) return false;
		else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return true;
		else return isCausedBy(caught.getCause(), isOfOrCausedBy);
	}

	public static void sleep(int milliseconds){
		sleep(milliseconds, 0);
	}
	public static void sleep(int milliseconds, int nano){
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
			TimeUnit.NANOSECONDS.sleep(nano);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<CommandData> alphabetizeCommandData(ArrayList<CommandData> commandDataArrayList){
		ArrayList<String> commandNames = new ArrayList<>();
		for(CommandData data : commandDataArrayList){
			commandNames.add(data.getCommand());
		}
		commandNames.sort(String.CASE_INSENSITIVE_ORDER);
		ArrayList<CommandData> newCommandDataArrayList = new ArrayList<>();
		for(String commandName : commandNames){
			for(CommandData commandData : commandDataArrayList){
				if(commandData.getCommand().equalsIgnoreCase(commandName)){
					newCommandDataArrayList.add(commandData);
					break;
				}
			}
		}
		return newCommandDataArrayList;
	}
	public static ArrayList<KeywordData> alphabetizeKeywordData(ArrayList<KeywordData> keywordDataArrayList){
		ArrayList<String> commandNames = new ArrayList<>();
		for(KeywordData data : keywordDataArrayList){
			commandNames.add(data.getKeyword());
		}
		commandNames.sort(String.CASE_INSENSITIVE_ORDER);
		ArrayList<KeywordData> newKeywordDataArrayList = new ArrayList<>();
		for(String commandName : commandNames){
			for(KeywordData keywordData : keywordDataArrayList){
				if(keywordData.getKeyword().equalsIgnoreCase(commandName)){
					newKeywordDataArrayList.add(keywordData);
					break;
				}
			}
		}
		return newKeywordDataArrayList;
	}
	public static ArrayList<TimerData> alphabetizeTimerData(ArrayList<TimerData> timerDataArrayList){
		ArrayList<String> timerNames = new ArrayList<>();
		for(TimerData data : timerDataArrayList){
			timerNames.add(data.getName());
		}
		timerNames.sort(String.CASE_INSENSITIVE_ORDER);
		ArrayList<TimerData> newTimerArrayList = new ArrayList<>();
		for(String timerName : timerNames){
			for(TimerData timerData : timerDataArrayList){
				if(timerData.getName().equalsIgnoreCase(timerName)){
					newTimerArrayList.add(timerData);
					break;
				}
			}
		}
		return newTimerArrayList;
	}
}
